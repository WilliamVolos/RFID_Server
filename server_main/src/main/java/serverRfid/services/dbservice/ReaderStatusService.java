package serverRfid.services.dbservice;

import serverRfid.model.ReaderStatus;

import java.util.Optional;

public interface ReaderStatusService {
    Optional<ReaderStatus> getReaderStatusById(Long id);
    Optional<ReaderStatus> getReaderStatusByCodename(String codeName);
    String getReaderStatusNameById(Long id);
}
