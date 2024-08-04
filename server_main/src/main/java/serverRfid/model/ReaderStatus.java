package serverRfid.model;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("reader_status")
public class ReaderStatus {
    @Id
    private final Long id;

    @Nonnull
    private final String codename;

    @Nonnull
    private final String name;

    public ReaderStatus(String codename, String name) {
        this.id = null;
        this.codename = codename;
        this.name = name;
    }

    @PersistenceCreator
    public ReaderStatus(Long id, String codename, String name) {
        this.id = id;
        this.codename = codename;
        this.name = name;
    }
}
