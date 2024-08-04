package serverRfid.services.dbservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import serverRfid.model.ReaderModel;
import serverRfid.repository.ReaderModelRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderModelServiceImpl implements ReaderModelService{

    private final ReaderModelRepository readerModelRepository;

    @Override
    public Optional<ReaderModel> getReaderModelById(Long id) {

        return readerModelRepository.getReaderModelById(id);
    }

    @Override
    public String getReaderModelNameById(Long id) {
        return readerModelRepository.getReaderModelById(id)
                        .map(ReaderModel::getName).orElse("");
    }

    @Override
    public Optional<ReaderModel> getReaderModelByName(String name) {
        return readerModelRepository.getReaderModelByName(name);
    }
}
