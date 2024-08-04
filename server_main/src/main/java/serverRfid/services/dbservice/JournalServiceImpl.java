package serverRfid.services.dbservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import serverRfid.exception.ReaderException;
import serverRfid.model.Journal;
import serverRfid.model.Reader;
import serverRfid.model.dto.EventReadTag;
import serverRfid.model.dto.JournalTagInfo;
import serverRfid.repository.JournalRepository;
import serverRfid.sessionmanager.TransactionManager;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalServiceImpl implements JournalService {

    private final TransactionManager transactionManager;

    private final JournalRepository journalRepository;

    @Override
    public void closeVisibleTagsByReader(Reader reader, int delayVisibleTag) {
        if ((reader != null)&&(reader.getId() != null)) {
            journalRepository.closeVisibleTagsByReaderId(reader.getId(), delayVisibleTag);
        } else {
          throw new ReaderException("Ошибка в closeVisibleTagsByReader. ID Считывателя не может быть пустым!");
        }
    }

    @Override
    public void saveTag(EventReadTag eventReadTag) {
        Journal journalTag = eventReadTagToJournal(eventReadTag);

        transactionManager.doInTransaction(() -> {
            journalRepository.save(journalTag);
            return null;
        });
    }

    @Override
    public List<JournalTagInfo> findLastByReaderAndLimit(Reader reader, int limit) {
        if ((reader != null)&&(reader.getId() != null)) {
            return journalRepository.findLastByReaderIdAndLimit(reader.getId(), limit).stream()
                    .map(j->journalToTagInfo(reader, j))
                    .toList();
        } else {
            throw new ReaderException("Ошибка в findLastByReaderAndLimit. ID Считывателя не может быть пустым!");
        }
    }

    private JournalTagInfo journalToTagInfo(Reader reader, Journal journal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return new JournalTagInfo(reader.getName(),
                                sdf.format(journal.getDatetime()),
                                journal.getEpc(),
                                journal.getRssi(),
                                reader.getPorts().stream()
                                        .filter(p->p.getId().equals(journal.getPortId()))
                                        .map(p->Integer.toString(p.getNumber()))
                                        .findFirst().orElse(""),
                                journal.getVisible());
    }

    private Journal eventReadTagToJournal(EventReadTag eventReadTag) {
        if (eventReadTag != null) {

            try {
                return new Journal(new Timestamp(eventReadTag.getDateTimeLastRead()),
                        eventReadTag.getVisible(),
                        eventReadTag.getReader().getPorts().get(Integer.parseInt(eventReadTag.getAnt())-1).getId(),
                        eventReadTag.getEpc(),
                        eventReadTag.getRssi());
            }
            catch (Exception e) {
                throw new ReaderException(String.format("Ошибка получения считанной RFID метки!\n%s", e.getMessage()));
            }
        } else {
            throw new ReaderException("Значение считанной метки EventReadTag не может быть пустым!");
        }
    }
}
