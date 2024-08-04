package serverRfid.model;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@Table("readers")
public class Reader {
    @Id
    private final Long id;

    @Nonnull
    private final String name;

    private final String description;

    private final String ipAddress;

    private final int tcpPort;

    @Nonnull
    @MappedCollection(idColumn = "reader_id", keyColumn = "order_column")
    private final List<Port> ports;

    @Nonnull
    private final Long readerModelId;

    @Nonnull
    private final Long readerStatusId;

    @Nonnull
    private final Boolean isDeleted;

    public Reader(String ipAddress, int tcpPort, List<Port> ports, Long readerModelId, Long readerStatusId) {
        this.id = null;
        this.name = "EMPTY_NAME";
        this.description = "EMPTY_NAME";
        this.ipAddress = ipAddress;
        this.tcpPort = tcpPort;
        this.ports = ports;
        this.readerModelId = readerModelId;
        this.readerStatusId = readerStatusId;
        this.isDeleted = false;
    }

    @PersistenceCreator
    public Reader(Long id, String name, String description, String ipAddress, int tcpPort, List<Port> ports, Long readerModelId, Long readerStatusId, Boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ipAddress = ipAddress;
        this.tcpPort = tcpPort;
        this.ports = ports;
        this.readerModelId = readerModelId;
        this.readerStatusId = readerStatusId;
        this.isDeleted = isDeleted;
    }
}
