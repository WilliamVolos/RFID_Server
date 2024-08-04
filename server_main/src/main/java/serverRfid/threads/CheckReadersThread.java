package serverRfid.threads;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serverRfid.config.FactoryStaticReaders;
import serverRfid.config.ReaderConfig;
import serverRfid.exception.ReaderException;
import serverRfid.model.Reader;
import serverRfid.model.dto.ReaderStatusInfo;
import serverRfid.services.dbservice.JournalService;
import serverRfid.services.dbservice.ReaderModelService;
import serverRfid.services.dbservice.ReaderService;
import serverRfid.services.poolservice.ActiveReadersService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Getter
@Setter
@RequiredArgsConstructor
// Поток попытки новых соединений со считывателями, в случае разрыва соединения, а также запуск всех необходимых
public class CheckReadersThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(CheckReadersThread.class);

    private final ActiveReadersService activeReaders;
    private final ReaderService readerService;
    private final ReaderModelService readerModelService;
    private final JournalService journalService;
    private final FactoryStaticReaders factoryStaticReaders;
    private final ReaderConfig readerConfig;

    private Boolean isRunning = true;

    @Override
    public void run() {
        try{
            log.info("Запускаем поток соединений к считывателям");

            while (isRunning){

                List<Callable<Object>> tasks = new ArrayList<>();

                try (ExecutorService pool = Executors.newFixedThreadPool(10)) {
                    // Запускаем все потоки считывателей
                    for (Reader reader : readerService.getAllReaders()) {

                        // Если такого нет в списке активных или он уже не активен
                        if (!activeReaders.getReaderInfo(reader).map(ReaderStatusInfo::getIsActive).orElse(false)){
                            tasks.add(new Callable<Object>() {
                                public Object call() throws Exception {
                                    try {
                                        String readerModelName = readerModelService.getReaderModelNameById(reader.getReaderModelId());

                                        var staticReader = factoryStaticReaders.getStaticReader(
                                                readerModelName,
                                                reader,
                                                readerConfig.getDelayVisibleTag());

                                        try {
                                            // Закрываем видимые метки в случае аварийного завершения с прошлого раза
                                            journalService.closeVisibleTagsByReader(reader, readerConfig.getDelayVisibleTag());
                                        } catch(Exception ex){
                                            log.info(ex.getMessage());
                                        }

                                        // Запускаем поток
                                        staticReader.map(activeReaders::runThreadReader)
                                                .orElseThrow(() -> new ReaderException(String.format("Данной модели считывателя не существует: %s", readerModelName)));
                                    } catch(Exception ex){
                                        log.info(String.format("Нет соединения со считывателем: %s\n%s", reader.getName(), ex.getMessage()));
                                    }
                                    return null;
                                }
                            });
                        }
                    }

                    // Запускаем пул потоков и ДОЖИДАЕМСЯ!
                    List<Future<Object>> invokeAll = pool.invokeAll(tasks);

                } catch (InterruptedException e) {
                    log.info(String.format("Ошибка запуска потоков соединений к считывателям: %s", e.getMessage()));
                }
                Thread.sleep(15000);
            }
        }
        catch(InterruptedException e) {
            log.info(String.format("Поток запуска соединений к считывателям был прерван!\n%s", e.getMessage()));
        }
    }
}
