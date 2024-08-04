package serverRfid.services.dbservice;

import serverRfid.model.ReaderModel;

import java.util.Optional;

public interface ReaderModelService {
    Optional<ReaderModel> getReaderModelById(Long id);
    String getReaderModelNameById(Long id);
    Optional<ReaderModel> getReaderModelByName(String name);
}
