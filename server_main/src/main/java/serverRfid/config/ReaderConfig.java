package serverRfid.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application")
@Data
@RequiredArgsConstructor
public class ReaderConfig {
    private final String STATUS_UNAVAILABLE = "UNAVAILABLE";  // Недоступен
    private final String STATUS_RUNNING     = "RUNNING";      // В работе
    private final String STATUS_STOPPED     = "STOPPED";      // Остановлен
    private final String STATUS_DISCONNECTED = "DISCONNECTED";// Нет соединения

    private int delayVisibleTag;
}

