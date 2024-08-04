package serverRfid.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReaderRequestDto {
    private final String model;
    private final String ipAddress;
    private final String tcpPort;
}
