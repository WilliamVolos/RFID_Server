package serverRfid.model;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Getter
@Table("journal")
public class Journal {
    @Id
    private final Long id;

    @Nonnull
    private final Timestamp datetime;

    @Nonnull
    private final Boolean visible;

    @Nonnull
    private final Long portId;

    @Nonnull
    private final String epc;

    private final String rssi;

    public Journal(Timestamp datetime, Boolean visible, Long portId, String epc, String rssi) {
        this.id = null;
        this.datetime = datetime;
        this.visible = visible;
        this.portId = portId;
        this.epc = epc;
        this.rssi = rssi;
    }

    @PersistenceCreator
    public Journal(Long id, Timestamp datetime, Boolean visible, Long portId, String epc, String rssi) {
        this.id = id;
        this.datetime = datetime;
        this.visible = visible;
        this.portId = portId;
        this.epc = epc;
        this.rssi = rssi;
    }
}
