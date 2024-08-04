package serverRfid.sessionmanager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionManagerSpring implements TransactionManager {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T> T doInTransaction(TransactionAction<T> action) {
        return action.get();
    }
}
