package serverRfid.services.poolservice;

import serverRfid.model.Reader;
import serverRfid.model.dto.ReaderStatusInfo;
import serverRfid.threads.ConnectionReaderCallback;
import serverRfid.threads.ReaderThread;
import serverRfid.uhf_operation.StaticReader;

import java.util.List;
import java.util.Optional;

// Сервис работы потоков со считывателями
public interface ActiveReadersService {
    // Получить количество активных потоков
    int getActiveCount();

    // Остановка и удаление потока по имени считывателя
    boolean deleteThreadByReaderName(String nameCode);

    // Получить список активных считывателей (работающих в данный момент)
    List<ReaderStatusInfo> getActiveReaders();

    // Получить информацию о состоянии считывателя
    Optional<ReaderStatusInfo> getReaderInfo(Reader reader);

    // Запустить новый поток и установить соединение со считывателем
    ReaderStatusInfo runThreadReader(StaticReader staticReader);

    // Получить поток по имени считывателя
    Optional<ReaderThread> getThreadByReaderName(String nameCode);

    // CallBack разрыва/восстановления соединения со считывателем
    void connectionReaderCallback(ConnectionReaderCallback connReaderCallback);
}
