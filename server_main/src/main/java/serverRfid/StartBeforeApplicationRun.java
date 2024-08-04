package serverRfid;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import serverRfid.services.mainservice.MainReaderService;

@Component
@RequiredArgsConstructor
public class StartBeforeApplicationRun {
    private static final Logger log = LoggerFactory.getLogger(StartBeforeApplicationRun.class);
    private final MainReaderService mainReaderService;

    @EventListener(ApplicationStartedEvent.class)
    public void run() {
        log.info("Запуск потоков");
        try{
            mainReaderService.runAllReaders();
        }catch (Exception ex){
            log.info(ex.getMessage());
        }
    }
}
