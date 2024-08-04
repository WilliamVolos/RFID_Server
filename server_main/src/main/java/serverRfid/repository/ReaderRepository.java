package serverRfid.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import serverRfid.model.Reader;

import java.util.List;
import java.util.Optional;

public interface ReaderRepository extends ListCrudRepository<Reader, Long> {
    @Query("select r.id, r.name, r.description, r.ip_address, r.tcp_port, r.reader_model_id, r.reader_status_id, r.is_deleted "+
           "  from readers r "+
           " where r.is_deleted = false")
    List<Reader> getAll();

    @Query("select r.id, r.name, r.description, r.ip_address, r.tcp_port, r.reader_model_id, r.reader_status_id, r.is_deleted "+
            "  from readers r "+
            " where r.is_deleted = false"+
            "   and r.ip_address = :ipAddress")
    Optional<Reader> getReaderByIpAddress(@Param("ipAddress") String ipAddress);

    @Query("select r.id, r.name, r.description, r.ip_address, r.tcp_port, r.reader_model_id, r.reader_status_id, r.is_deleted "+
            "  from readers r "+
            " where r.is_deleted = false"+
            "   and r.name = Upper(:name)")
    Optional<Reader> getReaderByName(@Param("name") String name);

    @Modifying
    @Query("update readers set name = :name, description = :description where id = :id")
    void updateNameAndDescriptionById(@Param("name") String name, @Param("description") String description, @Param("id") Long id);

    @Modifying
    @Query("update readers set is_deleted = true where id = :id")
    void deleteReaderById(@Param("id") Long id);

    Optional<Reader> getReaderById(Long id);
}
