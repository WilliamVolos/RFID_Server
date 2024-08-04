package serverRfid.model;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("reader_models")
public class ReaderModel {
    @Id
    private final Long id;

    @Nonnull
    private final String name;

    private final String producer;

    private final String login;

    private final String password;

    @Nonnull
    private final int countPorts;

    private final String description;

    public ReaderModel(String name, String producer, String login, String password, int countPorts, String description) {
        this.id = null;
        this.name = name;
        this.producer = producer;
        this.login = login;
        this.password = password;
        this.countPorts = countPorts;
        this.description = description;
    }

    @PersistenceCreator
    public ReaderModel(Long id, String name, String producer, String login, String password, int countPorts, String description) {
        this.id = id;
        this.name = name;
        this.producer = producer;
        this.login = login;
        this.password = password;
        this.countPorts = countPorts;
        this.description = description;
    }
}
