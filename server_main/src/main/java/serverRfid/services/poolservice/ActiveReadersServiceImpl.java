package serverRfid.services.poolservice;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import serverRfid.exception.ReaderException;
import serverRfid.model.Reader;
import serverRfid.model.dto.ReaderStatusInfo;
import serverRfid.services.dbservice.ReaderModelService;
import serverRfid.services.dbservice.ReaderStatusService;
import serverRfid.threads.ConnectionReaderCallback;
import serverRfid.threads.ReaderThread;
import serverRfid.uhf_operation.StaticReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActiveReadersServiceImpl implements ActiveReadersService {
    private final Map<String, Thread> activeThreadReaders = new HashMap<>();
    private final Map<String, ReaderThread> activeStartedReaders = new HashMap<>();

    private final ReadTagsPoolService poolTags;

    private final ReaderModelService readerModelService;

    private final ReaderStatusService readerStatusService;
    private ConnectionReaderCallback connectReaderCallback;

    @Override
    public int getActiveCount() {
        return activeStartedReaders.values().stream()
                .filter(ReaderThread::getConnected)
                .toList().size();
    }

    @Override
    synchronized public boolean deleteThreadByReaderName(String nameCode) {

        String nCode = nameCode.toUpperCase();

        ReaderThread readerThread = activeStartedReaders.get(nCode);
        Thread thread = activeThreadReaders.get(nCode);

        if (readerThread != null && thread != null) {
            // Сначала прерываем поток
            readerThread.setConnected(false);
            // Затем удаляем из списка
            activeStartedReaders.remove(nCode);
            activeThreadReaders.remove(nCode);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<ReaderStatusInfo> getActiveReaders() {
        return activeStartedReaders.values().stream()
                .filter(ReaderThread::getConnected)
                .map(v -> readerToInfoStatus(v.getReader(),
                                            true))
                .toList();
    }

    @Override
    public Optional<ReaderStatusInfo> getReaderInfo(Reader reader) {

        if (reader != null) {

            ReaderThread readerThread = activeStartedReaders.get(reader.getName());

            if (readerThread != null) {
                return Optional.of(readerToInfoStatus(readerThread.getReader(),
                        readerThread.getConnected()));
            } else {
                return Optional.of(readerToInfoStatus(reader, false));
            }

        } else {
            return Optional.empty();
        }
    }

    @Override
    public ReaderStatusInfo runThreadReader(StaticReader staticReader) {

        if (staticReader != null){
            ReaderThread readerThread = new ReaderThread(staticReader, poolTags);
            boolean connect = readerThread.connect();

            if (connect){
                // Если соединение успешно
                // Регистрируем событие разъединения считывателя
                readerThread.connectionReaderCallback((rd, isConnection) -> {
                    if(connectReaderCallback != null){
                        connectReaderCallback.stateCallback(rd, isConnection);
                    }
                });

                // Запускаем поток
                try{
                    Thread newThreadReader = new Thread(readerThread);

                    newThreadReader.start();

                    // Добавляем в список активных считывателей
                    addActiveThread(newThreadReader, readerThread);

                    return readerToInfoStatus(staticReader.getReader(), readerThread.getConnected());
                } catch(Exception ex){
                    throw new ReaderException("Ошибка запуска потока считывателя: \n"+ex.getMessage());
                }
            }else {
                throw new ReaderException(String.format("Невозможно подсоединиться к считывателю \n"+
                                "ip адрес: %s, порт: %s \n"+
                                "проверьте параметры подключения",
                        staticReader.getReader().getIpAddress(),
                        staticReader.getReader().getTcpPort()));
            }
        }else{
            throw new ReaderException("Ошибка добавления считывателя");
        }
    }

    @Override
    public Optional<ReaderThread> getThreadByReaderName(String nameCode) {

        ReaderThread readerThread = activeStartedReaders.get(nameCode.toUpperCase());
        Thread thread = activeThreadReaders.get(nameCode.toUpperCase());

        if((readerThread != null)&&(thread != null)&&(thread.isAlive())&&(readerThread.getConnected())){
            return Optional.of(readerThread);
        } else return Optional.empty();
    }

    @Override
    public void connectionReaderCallback(ConnectionReaderCallback connReaderCallback) {
        connectReaderCallback = connReaderCallback;
    }

    synchronized private void addActiveThread(Thread thread, ReaderThread readerThread) {

        if ((thread != null) && (readerThread != null)) {

            String nameCode = readerThread.getReader().getName();
            // Добавляем в список активных считывателей
            activeThreadReaders.put(nameCode, thread);

            // Добавляем в список классов потоков
            activeStartedReaders.put(nameCode, readerThread);
        }
    }

    private ReaderStatusInfo readerToInfoStatus(@Nonnull Reader reader, boolean isConnected) {
        return new ReaderStatusInfo(reader.getId(),
                reader.getName(),
                reader.getIpAddress(),
                Integer.toString(reader.getTcpPort()),
                readerModelService.getReaderModelNameById(reader.getReaderModelId()),
                isConnected,
                readerStatusService.getReaderStatusNameById(reader.getReaderModelId()));
    }
}
