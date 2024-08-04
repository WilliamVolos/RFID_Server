package serverRfid.services.dbservice;

import org.springframework.data.repository.query.Param;
import serverRfid.model.Reader;

import java.util.List;
import java.util.Optional;

public interface ReaderService {
    List<Reader> getAllReaders();
    Reader saveReader(Reader reader);
    Optional<Reader> getReaderByIpAddress(String ipAddress);
    Optional<Reader> getReaderByName(String name);
    void deleteReader(Reader reader);
}
