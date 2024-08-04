package serverRfid;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import serverRfid.model.Port;
import serverRfid.model.Reader;
import serverRfid.model.dto.EventReadTag;
import serverRfid.services.dbservice.JournalService;
import serverRfid.services.dbservice.ReaderModelService;
import serverRfid.services.dbservice.ReaderService;
import serverRfid.services.dbservice.ReaderStatusService;

import java.util.ArrayList;
import java.util.List;

@Component("startServer")
@RequiredArgsConstructor
public class StartServer implements CommandLineRunner {

    private final ReaderStatusService readerStatusService;
    private final ReaderModelService readerModelService;
    private final ReaderService readerService;
    private final JournalService journalService;

    @Override
    public void run(String... args) throws Exception {

        var readerStatus = readerStatusService.getReaderStatusByCodename("RUNNING");
        var readerModel = readerModelService.getReaderModelByName("URA4");

        if (readerStatus.isPresent() && readerModel.isPresent()) {
            List<Port> ports = new ArrayList<>();
            for(int i=1; i<=readerModel.get().getCountPorts(); i++){
                ports.add(i-1, new Port(i));
            }

            Reader reader = new Reader("127.0.0.1",80, ports, readerModel.get().getId(), readerStatus.get().getId());
            var newReader = readerService.saveReader(reader);
            var newReader2 = readerService.getReaderByName(newReader.getName());

            EventReadTag readTag1 = new EventReadTag(newReader,
                    "436742F47324297884278",
                    "-73",
                    "3",
                    System.currentTimeMillis(),
                    true);

            Thread.sleep(1000);

            EventReadTag readTag2 = new EventReadTag(newReader,
                    "436742F473242978842990",
                    "-73",
                    "1",
                    System.currentTimeMillis(),
                    true);

            Thread.sleep(1000);

            EventReadTag readTag3 = new EventReadTag(newReader,
                    "436742F47324297884278",
                    "-73",
                    "3",
                    System.currentTimeMillis(),
                    true);

            journalService.saveTag(readTag1);
            journalService.saveTag(readTag2);
            journalService.saveTag(readTag3);
        }
    }
}
