package serverRfid.services.mainservice;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import serverRfid.config.FactoryStaticReaders;
import serverRfid.config.ReaderConfig;
import serverRfid.exception.ReaderException;
import serverRfid.model.Port;
import serverRfid.model.Reader;
import serverRfid.model.ReaderModel;
import serverRfid.model.ReaderStatus;
import serverRfid.model.dto.ReaderStatusInfo;
import serverRfid.model.dto.ReaderRequestDto;
import serverRfid.services.dbservice.JournalService;
import serverRfid.services.dbservice.ReaderModelService;
import serverRfid.services.dbservice.ReaderService;
import serverRfid.services.dbservice.ReaderStatusService;
import serverRfid.services.poolservice.ActiveReadersService;
import serverRfid.services.poolservice.ReadTagsPoolService;
import serverRfid.threads.CheckReadersThread;
import serverRfid.threads.JournalThread;
import serverRfid.threads.ReaderThread;
import serverRfid.uhf_operation.TagCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(ReaderConfig.class)
public class MainReaderServiceImpl implements MainReaderService{
    private static final Logger log = LoggerFactory.getLogger(MainReaderServiceImpl.class);

    private final ReaderConfig readerConfig;

    private final ReaderService readerService;

    private final ReaderModelService readerModelService;

    private final ReaderStatusService readerStatusService;

    private final ActiveReadersService activeReadersService;

    private final FactoryStaticReaders factoryStaticReaders;

    private final ReadTagsPoolService readTagsPoolService;

    private final JournalService journalService;

    @Override
    public int runAllReaders() {

        // При изменении состояния соединения считывателя обновляем статус устройства в БД
        activeReadersService.connectionReaderCallback((reader, isConnection) -> {
            try {
                if (isConnection) {
                    Reader newReader = changeReaderStatus(reader, readerConfig.getSTATUS_RUNNING());
                    readerService.saveReader(newReader);
                    log.info(String.format("Успешное соединение со считывателем: %s", reader.getName()));
                } else {
                    // Здесь поток сам должен корректно завершиться и все Unvisible сложить в базу
                    // journalService.closeVisibleTagsByReader(reader, readerConfig.getDelayVisibleTag());
                    Reader newReader = changeReaderStatus(reader, readerConfig.getSTATUS_DISCONNECTED());
                    readerService.saveReader(newReader);
                    log.info(String.format("Соединение со считывателем потеряно: %s", reader.getName()));
                }
            } catch (Exception ex) {
                log.info(String.format("Ошибка смены состояния считывателя: %s", ex.getMessage()));
            }
        });

        try{
            // Запускаем поток вставки считываемых меток в Journal
            Thread threadJournal = runThreadJournal();

        } catch(Exception ex){
            // Пишем лог. Ошибка запуска потока Journal
            log.info(ex.getMessage());
            throw new ReaderException(ex.getMessage());
        }

        try{
            // Запускаем поток контроля соединений со считывателями
            Thread threadCheckReaders = runThreadCheckReaders();

        } catch(Exception ex){
            // Пишем лог. Ошибка запуска потока контроля соединений со считывателями
            log.info(ex.getMessage());
            throw new ReaderException(ex.getMessage());
        }

        int activeCount = activeReadersService.getActiveCount();
        log.info(String.format("Запущено активных считывателей: %d", activeCount));

        return activeCount;
    }

    @Override
    public ReaderStatusInfo addAndRunNewReader(ReaderRequestDto readerDto) {

        // Сначала проверяем, есть ли этот считыватель уже в списке зарегистрированных?
        Optional<ReaderStatusInfo> readerInfoStatus = getReaderInfoByIpAddress(readerDto.getIpAddress());

        if(readerInfoStatus.isPresent()) {
            throw new ReaderException(String.format("Считыватель с таким IP адресом уже добавлен в систему: \n%s", readerInfoStatus.get()));
        }else{
            // Добавляем считыватель в базу и коллекцию

            Optional<ReaderStatus> readerStatus = readerStatusService.getReaderStatusByCodename(readerConfig.getSTATUS_UNAVAILABLE());
            if (readerStatus.isEmpty()) {
                throw new ReaderException("Не найден статус считывателя UNAVAILABLE");
            }

            Optional<ReaderModel> readerModel = readerModelService.getReaderModelByName(readerDto.getModel());
            if (readerModel.isEmpty()) {
                throw new ReaderException(String.format("Не найдена модель считывателя: %s", readerDto.getModel()));
            }

            List<Port> ports = new ArrayList<>();
            for(int i=1; i<=readerModel.get().getCountPorts(); i++){
                ports.add(i-1, new Port(i));
            }

            int tcpPort;
            try {
                tcpPort = Integer.parseInt(readerDto.getTcpPort());
            }
            catch (NumberFormatException e) {
                throw new ReaderException("Порт считывателя должен содержать только цифры!");
            }

            Reader reader = new Reader(readerDto.getIpAddress(),
                                        tcpPort,
                                        ports,
                                        readerModel.get().getId(),
                                        readerStatus.get().getId());
            Reader newReader = readerService.saveReader(reader);

            if (newReader != null){

                var staticReader = factoryStaticReaders.getStaticReader(
                                                        readerModel.get().getName(),
                                                        newReader,
                                                        readerConfig.getDelayVisibleTag());
                // Запускаем поток
                return staticReader.map(activeReadersService::runThreadReader)
                        .orElseThrow(() -> new ReaderException(String.format("Данной модели считывателя не существует: %s", readerModel.get().getName())));

            } else {
                throw new ReaderException("Ошибка добавления считывателя");
            }
        }
    }

    @Override
    public Optional<ReaderStatusInfo> getReaderInfoByIpAddress(String ipAddress) {

        // Сначала ищем считыватель в списке зарегистрированных
        Optional<Reader> existReader =  readerService.getReaderByIpAddress(ipAddress);

        return existReader.flatMap(activeReadersService::getReaderInfo);
    }

    @Override
    public Optional<ReaderStatusInfo> getReaderInfoByName(String nameCode) {
        // Сначала ищем считыватель в списке зарегистрированных
        Optional<Reader> existReader =  readerService.getReaderByName(nameCode);

        return existReader.flatMap(activeReadersService::getReaderInfo);
    }

    @Override
    public List<ReaderStatusInfo> getActiveReaders() {
        return activeReadersService.getActiveReaders();
    }

    @Override
    public List<ReaderStatusInfo> getDisabledReaders() {
        return readerService.getAllReaders().stream()
                .map(r->activeReadersService.getReaderInfo(r).orElse(null))
                .filter(Objects::nonNull)
                .filter(r->!r.getIsActive())
                .toList();
    }

    @Override
    public boolean setTagsCallbackByReaderName(String nameCode, TagCallback tagsCB) {
        Optional<ReaderThread> readerThread = activeReadersService.getThreadByReaderName(nameCode);

        if (readerThread.isPresent()){
            readerThread.get().setTagCallback(tagsCB);
            return true;
        }else return false;
    }

    @Override
    public void deleteReaderByName(String nameCode) {
        var reader = readerService.getReaderByName(nameCode.toUpperCase());
        if (reader.isPresent()) {
            readerService.deleteReader(reader.get());
            activeReadersService.deleteThreadByReaderName(reader.get().getName());
        } else {
            throw new ReaderException("Считывателя с таким именем не найдено!");
        }
    }

    @Override
    public void deleteReaderByIpAddress(String ipAddress) {
        var reader = readerService.getReaderByIpAddress(ipAddress.toUpperCase());
        if (reader.isPresent()) {
            readerService.deleteReader(reader.get());
            activeReadersService.deleteThreadByReaderName(reader.get().getName());
        } else {
            throw new ReaderException("Считывателя с таким IP адресом не найдено!");
        }
    }

    private Thread runThreadJournal() {

        JournalThread jThread = new JournalThread(journalService, readTagsPoolService);

        try{
            var thread = new Thread(jThread);
            thread.start();
            return thread;
        } catch(Exception ex){
            throw new ReaderException(String.format("Ошибка запуска потока добавления в журнал меток:\n%s", ex.getMessage()));
        }
    }

    private Thread runThreadCheckReaders() {

        CheckReadersThread checkReaders = new CheckReadersThread(activeReadersService,
                                                                readerService,
                                                                readerModelService,
                                                                journalService,
                                                                factoryStaticReaders,
                                                                readerConfig);

        try{
            var checkReadersThread = new Thread(checkReaders);
            checkReadersThread.start();
            return checkReadersThread;
        } catch(Exception ex){
            throw new ReaderException(String.format("Ошибка запуска потока соединения со считывателями:\n%s", ex.getMessage()));
        }
    }

    private Reader changeReaderStatus(Reader reader, String statusCodeName) {
        return reader.toBuilder().readerStatusId(
                        readerStatusService.getReaderStatusByCodename(statusCodeName)
                        .map(ReaderStatus::getId)
                        .orElseThrow(()->new ReaderException("Невозможно сменить состояние считывателя")))
               .build();
    }
}
