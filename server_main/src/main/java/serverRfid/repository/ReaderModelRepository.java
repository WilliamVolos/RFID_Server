package serverRfid.repository;

import org.springframework.data.repository.ListCrudRepository;
import serverRfid.model.ReaderModel;

import java.util.Optional;

public interface ReaderModelRepository extends ListCrudRepository<ReaderModel, Long> {
    Optional<ReaderModel> getReaderModelById(Long id);
    Optional<ReaderModel> getReaderModelByName(String name);
}
