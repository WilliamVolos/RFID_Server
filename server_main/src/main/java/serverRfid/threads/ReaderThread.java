package serverRfid.threads;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serverRfid.model.Reader;
import serverRfid.model.constant.AntennaEnum;
import serverRfid.model.constant.ReadTagBankEnum;
import serverRfid.model.dto.AntennaEnable;
import serverRfid.services.poolservice.ReadTagsPoolService;
import serverRfid.uhf_operation.StaticReader;
import serverRfid.uhf_operation.TagCallback;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
// Поток работы со считывателем
public class ReaderThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ReaderThread.class);

    private Boolean connected;
    private final Reader reader;
    private TagCallback tagCallback;
    private ConnectionReaderCallback connectionReaderCallback;
    private final StaticReader staticReader;
    private final ReadTagsPoolService poolTags;
    private boolean isDisconnected;

    public ReaderThread(StaticReader staticReader, ReadTagsPoolService poolTags) {
        this.isDisconnected = false;
        this.reader = staticReader.getReader();
        this.staticReader = staticReader;
        this.poolTags = poolTags;
    }

    @Override
    public void run() {
        try (staticReader) {
            staticReader.visibleTagCallback(readTag -> {
                poolTags.addEventTag(readTag);

                if (tagCallback != null){
                    tagCallback.callback(readTag);
                }
            });

            staticReader.unvisibleTagCallback(readTag -> {
                poolTags.addEventTag(readTag);

                if (tagCallback != null){
                    tagCallback.callback(readTag);
                }
            });

            if (connected){
                if (!staticReader.setReadBank(ReadTagBankEnum.BANK_EPC)) {
                    log.info(String.format("Не удалось установить фильтр чтения BANK_EPC на считывателе: %s",reader.getName()));
                }

                // Устанавливаем рабочими все антенны
                List<AntennaEnable> antList = new ArrayList<>();
                for (int i=1; i<=reader.getPorts().size(); i++) {
                    antList.add(new AntennaEnable(AntennaEnum.getValue(i), true));
                }
                if (!staticReader.setWorkAntennas(antList)) {
                    log.info(String.format("Не удалось включить антенны на считывателе: %s",reader.getName()));
                }

                // Устанавливаем мощность всех антенн
                for (int i=1; i<=reader.getPorts().size(); i++) {
                    if (!staticReader.setAntennaPower(AntennaEnum.getValue(i), 30)){
                        log.info(String.format("Не удалось установить мощность антенны %s на считывателе: %s", AntennaEnum.getValue(i), reader.getName()));
                    }
                }

                // Запускаем чтение меток
                staticReader.startInventory();
            }

            while (connected){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // Пишем в лог о проблеме
                    log.info(String.format("Прервался поток на считывателе: %s \n Ошибка: %s", reader.getName(), e.getMessage()));
                    throw new RuntimeException(e);
                }
            }

        } catch (Exception e) {
            log.info(String.format("Ошибка выполнения потока на считывателе: %s \n Ошибка: %s", reader.getName(), e.getMessage()));
            throw new RuntimeException(e);
        } finally {
            // Узнаем, что поток остановили вручную, и это не разрыв сети (isDisconnected = false)
            if (connectionReaderCallback != null && !isDisconnected){
                connectionReaderCallback.stateCallback(reader, false);
            }
            log.info(String.format("Инвентаризация остановлена на считывателе: %s", reader.getName()));
        }
    }

    public boolean connect(){
        staticReader.connectionStateCallback(isConnection -> {
            connected = isConnection;
            isDisconnected = !isConnection;

            if (connectionReaderCallback != null){
                connectionReaderCallback.stateCallback(reader, isConnection);
            }

            if (!isConnection){
                log.info(String.format("Разрыв соединения на считывателе: %s", reader.getName()));
            }
        });

        connected = staticReader.connect(reader.getIpAddress(), Integer.toString(reader.getTcpPort()));

        if (!connected){
            // Сохранить информацию в лог, что не удалось законнектиться
            log.info(String.format("Не удается установить соединение со считывателем: %s", reader.getName()));
        }
        return connected;
    }
    public void setTagCallback(TagCallback tagsCB){
        this.tagCallback = tagsCB;
    }

    public void connectionReaderCallback(ConnectionReaderCallback connectionReaderCallback){
        this.connectionReaderCallback = connectionReaderCallback;
    }
}
