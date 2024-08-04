package serverRfid.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import serverRfid.model.ReaderModel;
import serverRfid.model.ReaderStatus;

import java.util.List;
import java.util.Optional;

public interface ReaderStatusRepository extends ListCrudRepository<ReaderStatus, Long> {
    Optional<ReaderStatus> getReaderStatusById(Long id);
    Optional<ReaderStatus> getReaderStatusByCodename(String codeName);
    @Query("select id, codename, name from reader_status")
    List<ReaderStatus> getAll();
}
