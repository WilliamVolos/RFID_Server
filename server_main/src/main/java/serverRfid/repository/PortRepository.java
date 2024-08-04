package serverRfid.repository;

import org.springframework.data.repository.ListCrudRepository;
import serverRfid.model.Port;

public interface PortRepository extends ListCrudRepository<Port, Long> {
}
