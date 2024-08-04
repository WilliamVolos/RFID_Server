package serverRfid.uhf_operation;

import serverRfid.model.Reader;
import serverRfid.model.constant.AntennaEnum;
import serverRfid.model.constant.ReadTagBankEnum;
import serverRfid.model.dto.AntennaEnable;

import java.util.List;

// Общий интерфейс стационарного считывателя, предназначенный для работы с разными типами считывателей
// и разными библиотеками от производителей
public interface StaticReader extends AutoCloseable {
    // Установить соединение с устройством в сети
    boolean connect(String ipAddress, String tcpPort);

    // Указать банк памяти в метке, который необходимо считывать
    boolean setReadBank(ReadTagBankEnum readBank);

    // Установить какие номера портов антенн будут работать на считывателе, а какие будут выключены
    boolean setWorkAntennas(List<AntennaEnable> antennas);

    // Установить мощность излучения для антенны в DBM
    boolean setAntennaPower(AntennaEnum antName, int powerDBM);

    // Получить информацию о считывателе
    Reader getReader();

    // Запустить чтение меток устройством
    boolean startInventory();

    // Остановить чтение меток устройством
    boolean stopInventory();

    // Событие смены соединения со считывателем (разрыв связи и т.д.)
    void connectionStateCallback(ConnectionStateCallback connectState);

    // Событие появления новой метки в зоне видимости антенны считывателя
    void visibleTagCallback(TagCallback visibleTag);

    // Событие потери видимости метки в зоне видимости антенны считывателя
    void unvisibleTagCallback(TagCallback unvisibleTag);
}
