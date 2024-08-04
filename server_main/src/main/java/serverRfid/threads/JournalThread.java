package serverRfid.threads;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serverRfid.model.dto.EventReadTag;
import serverRfid.services.dbservice.JournalService;
import serverRfid.services.poolservice.ReadTagsPoolService;

@Getter
@Setter
@RequiredArgsConstructor
// Поток добавления считанных меток в базу Journal
public class JournalThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(JournalThread.class);

    private final JournalService journalService;

    private final ReadTagsPoolService poolTags;

    private Boolean isRunning = true;

    @Override
    public void run() {
        try{
            log.info("Запускаем поток обработки пула считанных меток");
            EventReadTag readTag;
            while (isRunning){

                readTag = poolTags.getEventTag().orElse(null);

                while (readTag != null){

                    try {
                        journalService.saveTag(readTag);
                    }catch(Exception ex){
                        log.info(String.format("Ошибка добавления считанной метки в базу:\n%s\n%s", ex.getMessage(), readTag));
                    }

                    poolTags.removeEventTag();
                    readTag = poolTags.getEventTag().orElse(null);
                }

                Thread.sleep(300);
            }
        }
        catch(InterruptedException e){
            // Пишем лог о прерывании
            log.info("Прерван поток добавления считанных меток в базу!");
        }
        log.info(String.format("Завершён поток добавления считанных меток в базу: %s", Thread.currentThread().getName()));
    }
}
