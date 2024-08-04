package serverRfid.services.dbservice;

import serverRfid.model.Reader;
import serverRfid.model.dto.EventReadTag;
import serverRfid.model.dto.JournalTagInfo;

import java.util.List;

public interface JournalService {
    void closeVisibleTagsByReader(Reader reader, int delayVisibleTag);
    void saveTag(EventReadTag eventReadTag);
    List<JournalTagInfo> findLastByReaderAndLimit(Reader reader, int limit);
}
