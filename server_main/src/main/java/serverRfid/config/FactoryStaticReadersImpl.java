package serverRfid.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import serverRfid.model.Reader;
import serverRfid.uhf_operation.ReaderURA4;
import serverRfid.uhf_operation.StaticReader;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FactoryStaticReadersImpl implements FactoryStaticReaders {
    private static final String URA4 = "URA4";
    private static final String VIRTUAL_GENERATOR = "VIRTUAL_READER";
    @Override
    public Optional<StaticReader> getStaticReader(String modelName, Reader reader, int delayVisibleTag) {

        switch (modelName.toUpperCase()){
            case URA4 -> {
                if (reader != null) {
                    return Optional.of(new ReaderURA4(reader, delayVisibleTag));
                } else {
                    return Optional.empty();
                }
            }
            default -> {
                return Optional.empty();
            }
        }
    }
}
