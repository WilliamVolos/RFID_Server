package serverRfid.services.poolservice;

import serverRfid.model.dto.EventReadTag;

import java.util.Optional;

public interface ReadTagsPoolService {
    boolean addEventTag(EventReadTag readTag);
    Optional<EventReadTag> getEventTag();
    void removeEventTag();
    
}
