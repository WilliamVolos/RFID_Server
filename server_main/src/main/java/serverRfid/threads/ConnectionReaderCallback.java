package serverRfid.threads;

import serverRfid.model.Reader;

public interface ConnectionReaderCallback {
    void stateCallback(Reader reader, boolean isConnection);
}
