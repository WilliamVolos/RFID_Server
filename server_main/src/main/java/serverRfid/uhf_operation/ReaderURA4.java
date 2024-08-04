package serverRfid.uhf_operation;

import com.rscja.deviceapi.ConnectionState;
import com.rscja.deviceapi.RFIDWithUHFNetworkA4;
import com.rscja.deviceapi.entity.AntennaNameEnum;
import com.rscja.deviceapi.entity.AntennaState;
import com.rscja.deviceapi.interfaces.IUHFA4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serverRfid.model.Reader;
import serverRfid.model.constant.AntennaEnum;
import serverRfid.model.constant.ReadTagBankEnum;
import serverRfid.model.dto.AntennaEnable;
import serverRfid.model.dto.EventReadTag;

import java.util.List;

public class ReaderURA4 implements StaticReader{
    private static final Logger log = LoggerFactory.getLogger(ReaderURA4.class);
    private final Reader reader;
    private final IUHFA4 ur4Network;
    private final CheckCurrentTags currentTags;
    private int delayVisibleTag;
    private TagCallback visibleTagCallback;
    private TagCallback unvisibleTagCallback;
    private ConnectionStateCallback connectStateCallback;
    private boolean isConnected = false;
    private boolean isInventory = false;

    public ReaderURA4(Reader reader, int delayVisibleTag) {
        this.reader = reader;
        this.currentTags = new CheckCurrentTags(delayVisibleTag);
        this.ur4Network = new RFIDWithUHFNetworkA4();
        this.delayVisibleTag = delayVisibleTag;
    }

    @Override
    public boolean connect(String ipAddress, String tcpPort) {
        if (ur4Network instanceof RFIDWithUHFNetworkA4) {
            ur4Network.setConnectionStateCallback((connectionState, o) -> {
                isConnected = (connectionState == ConnectionState.CONNECTED);
                if (connectStateCallback != null) {
                    connectStateCallback.getState(isConnected);
                }
            });

            return ((RFIDWithUHFNetworkA4) ur4Network).init(ipAddress, Integer.parseInt(tcpPort));
        } else {
            return false;
        }
    }

    @Override
    public boolean setReadBank(ReadTagBankEnum readBank) {
        return ur4Network.setFilter(readBank.getValue(), 0, 0, null);
    }

    @Override
    public boolean setWorkAntennas(List<AntennaEnable> antennas) {

        List<AntennaState> antList = antennas.stream()
                                    .map(a->new AntennaState(
                                            AntennaNameEnum.getValue(a.antName().getValue()),
                                            a.enable()))
                                    .toList();

        return ur4Network.setAntenna(antList);
    }

    @Override
    public boolean setAntennaPower(AntennaEnum antName, int powerDBM) {
        return ur4Network.setPower(AntennaNameEnum.getValue(antName.getValue()), powerDBM);
    }

    @Override
    public Reader getReader() {
        return reader;
    }

    @Override
    public boolean startInventory() {
        if (isConnected){
            ur4Network.setInventoryCallback(uhftagInfo -> {

                EventReadTag readTag = new EventReadTag(reader,
                                                uhftagInfo.getEPC(),
                                                uhftagInfo.getRssi(),
                                                uhftagInfo.getAnt(),
                                                System.currentTimeMillis(),
                                                true);

                if (currentTags.putTag(readTag) && (visibleTagCallback != null)){
                    visibleTagCallback.callback(readTag);
                }
            });

            // Стартуем чтение меток на считывателе
            isInventory = ur4Network.startInventoryTag();
            if (isInventory){

                // Запускаем поток по отлавливанию Unvisible меток
                Runnable runUnvisibleTags = ()->{
                    try{
                        while (isConnected && isInventory){

                            // Устанавливаем этим меткам свойство видимости в false и отправляем событие callback
                            currentTags.removeUnvisibleTags()
                                    .stream()
                                    .peek(v -> v.setVisible(false))
                                    .forEach(v -> unvisibleTagCallback.callback(v));

                            Thread.sleep(300);
                        }
                        // Если поток остановился тогда ждём и затем удаляем оставшиеся видимые метки
                        Thread.sleep(delayVisibleTag*1000L);

                        // Устанавливаем этим меткам свойство видимости в false и отправляем событие callback
                        currentTags.removeUnvisibleTags()
                                .stream()
                                .peek(v -> v.setVisible(false))
                                .forEach(v -> unvisibleTagCallback.callback(v));
                    }
                    catch(InterruptedException e){
                        log.info(String.format("Поток runUnvisibleTags на считывателе: %s прерван!",reader.getName()));
                    }
                    log.info(String.format("runUnvisibleTags поток перестал выполняться на считывателе: %s", reader.getName()));
                };

                Thread threadUnvisibleTags = new Thread(runUnvisibleTags);

                threadUnvisibleTags.start();
            };
            return isInventory;
        } else return false;
    }

    @Override
    public boolean stopInventory() {
        isInventory =  !ur4Network.stopInventory();
        return !isInventory;
    }

    @Override
    public void connectionStateCallback(ConnectionStateCallback connectState) {
        connectStateCallback = connectState;
    }

    @Override
    public void visibleTagCallback(TagCallback visibleTag) {
        visibleTagCallback = visibleTag;
    }

    @Override
    public void unvisibleTagCallback(TagCallback unvisibleTag) {
        unvisibleTagCallback = unvisibleTag;
    }

    @Override
    public void close() throws Exception {
        stopInventory();
        ur4Network.free();
    }
}
