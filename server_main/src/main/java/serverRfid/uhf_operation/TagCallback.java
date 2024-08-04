package serverRfid.uhf_operation;

import serverRfid.model.dto.EventReadTag;

public interface TagCallback {
    void callback(EventReadTag readTag);
}
