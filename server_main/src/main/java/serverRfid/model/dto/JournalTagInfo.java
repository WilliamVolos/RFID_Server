package serverRfid.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import serverRfid.model.Reader;

@Data
@AllArgsConstructor
public class JournalTagInfo {
    private final String readerName;
    private final String dateTime;
    private final String epc;
    private final String rssi;
    private final String ant;
    private final Boolean visible;
}
