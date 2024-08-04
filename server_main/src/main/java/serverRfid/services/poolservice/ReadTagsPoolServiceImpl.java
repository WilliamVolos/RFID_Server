package serverRfid.services.poolservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import serverRfid.model.dto.EventReadTag;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class ReadTagsPoolServiceImpl implements ReadTagsPoolService{
    private final Queue<EventReadTag> poolTags = new ConcurrentLinkedQueue<>();

    @Override
    public boolean addEventTag(EventReadTag readTag) {
        return poolTags.offer(readTag);
    }

    @Override
    public Optional<EventReadTag> getEventTag() {
        return Optional.ofNullable(poolTags.peek());
    }

    @Override
    public void removeEventTag() {
        poolTags.poll();
    }
}
