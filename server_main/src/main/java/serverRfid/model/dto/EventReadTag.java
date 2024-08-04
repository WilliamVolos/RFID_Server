package serverRfid.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import serverRfid.model.Reader;

@Data
@AllArgsConstructor
public class EventReadTag {
    private final Reader reader;
    private final String epc;
    private final String rssi;
    private final String ant;
    private long dateTimeLastRead;
    private Boolean visible;
}
