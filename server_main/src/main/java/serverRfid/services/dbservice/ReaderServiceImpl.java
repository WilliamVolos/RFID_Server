package serverRfid.services.dbservice;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import serverRfid.model.Reader;
import serverRfid.repository.ReaderRepository;
import serverRfid.sessionmanager.TransactionManager;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService{
    private static final Logger log = LoggerFactory.getLogger(ReaderServiceImpl.class);

    private final TransactionManager transactionManager;

    private final ReaderRepository readerRepository;

    private final ReaderModelService readerModelService;

    @Override
    public List<Reader> getAllReaders() {
        return readerRepository.getAll();
    }

    @Override
    public Optional<Reader> getReaderByIpAddress(String ipAddress) {
        return readerRepository.getReaderByIpAddress(ipAddress);
    }

    @Override
    public Optional<Reader> getReaderByName(String name) {
        return readerRepository.getReaderByName(name);
    }

    @Override
    public Reader saveReader(Reader reader) {
        return transactionManager.doInTransaction(() -> {

            var savedReader = readerRepository.save(reader);

            // Если добавляем новый, то формируем уникальное имя считывателю
            if (reader.getId() == null) {
                String modelName = readerModelService.getReaderModelNameById(reader.getReaderModelId());
                String newModelName = "000";
                String newReaderName = modelName + newModelName.substring(0, newModelName.length()-Long.toString(savedReader.getId()).length()) + savedReader.getId();
                readerRepository.updateNameAndDescriptionById(newReaderName, newReaderName, savedReader.getId());
                var newReader = readerRepository.getReaderById(savedReader.getId());
                savedReader = newReader.orElse(null);
            }
            return savedReader;
        });
    }

    @Override
    public void deleteReader(Reader reader) {
        if (reader != null) {
            transactionManager.doInTransaction(() -> {
                readerRepository.deleteReaderById(reader.getId());
                return null;
            });
        }
    }
}
