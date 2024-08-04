package serverRfid.services.mainservice;

import serverRfid.model.dto.ReaderStatusInfo;
import serverRfid.model.dto.ReaderRequestDto;
import serverRfid.uhf_operation.TagCallback;

import java.util.List;
import java.util.Optional;

// Сервис управления стационарными считывателями в сети
public interface MainReaderService {

    // Запустить все считыватели зарегестрированные в базе (автоматом при старте программы)
    int runAllReaders();

    // Добавить и запустить новый считыватель
    ReaderStatusInfo addAndRunNewReader(ReaderRequestDto readerDto);

    // Получить информацию о считывателе по его IP адресу
    Optional<ReaderStatusInfo> getReaderInfoByIpAddress(String ipAddress);

    // Получить информацию о считывателе по его уникальному имени
    Optional<ReaderStatusInfo> getReaderInfoByName(String nameCode);

    // Получить список активных считывателей, работающих в данный момент (статус "В работе")
    List<ReaderStatusInfo> getActiveReaders();

    // Получить список неработающих считывателей, (остановленных, незапущенных или разрыв связи)
    List<ReaderStatusInfo> getDisabledReaders();

    // Подписаться или отписаться на событие получения меток по имени считывателя
    boolean setTagsCallbackByReaderName(String nameCode, TagCallback tagsCB);

    // Остановить и удалить считыватель по его уникальному имени
    void deleteReaderByName(String nameCode);

    // Остановить и удалить считыватель по его IP адресу
    void deleteReaderByIpAddress(String ipAddress);
}
