package serverRfid.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReaderStatusInfo {
    private final long readerID;
    private final String nameCode;
    private final String ipAddress;
    private final String tcpPort;
    private final String model;
    private final Boolean isActive;
    private final String statusName;
}
