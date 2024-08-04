package serverRfid.model;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("ports")
public class Port {
    @Id
    private final Long id;

    private final int number;

    @Nonnull
    private final Long readerId;

    private final int orderColumn;

    public Port(int number) {
        this.id = null;
        this.number = number;
        this.readerId = null;
        this.orderColumn = 0;
    }

    @PersistenceCreator
    public Port(Long id, int number, Long readerId, int orderColumn) {
        this.id = id;
        this.number = number;
        this.readerId = readerId;
        this.orderColumn = orderColumn;
    }
}
