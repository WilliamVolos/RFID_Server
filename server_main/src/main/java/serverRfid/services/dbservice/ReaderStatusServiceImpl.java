package serverRfid.services.dbservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import serverRfid.model.ReaderStatus;
import serverRfid.repository.ReaderStatusRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReaderStatusServiceImpl implements ReaderStatusService {
    // Попробуем закешировать, чтобы каждый раз в базу не бегать за сменой состояния
    private final Map<String, ReaderStatus> readerStatuses;

    private final ReaderStatusRepository readerStatusRepository;

    public ReaderStatusServiceImpl(ReaderStatusRepository readerStatusRepository) {
        this.readerStatusRepository = readerStatusRepository;
        this.readerStatuses = readerStatusRepository.getAll().stream()
                                .collect(Collectors.toMap(ReaderStatus::getCodename, Function.identity(),
                                (o1, o2) -> o1, ConcurrentHashMap::new));
    }

    @Override
    public Optional<ReaderStatus> getReaderStatusById(Long id) {
        return readerStatusRepository.getReaderStatusById(id);
    }

    @Override
    public String getReaderStatusNameById(Long id) {
        return readerStatusRepository.getReaderStatusById(id)
                .map(ReaderStatus::getName).orElse("");
    }

    @Override
    public Optional<ReaderStatus> getReaderStatusByCodename(String codeName) {
        //readerStatusRepository.getReaderStatusByCodename(codeName);
        return Optional.ofNullable(readerStatuses.get(codeName));
    }
}
