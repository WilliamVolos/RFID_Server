package serverRfid.config;

import serverRfid.model.Reader;
import serverRfid.uhf_operation.StaticReader;

import java.util.Optional;

public interface FactoryStaticReaders {
    Optional<StaticReader> getStaticReader(String modelName, Reader reader, int delayVisibleTag);
}
