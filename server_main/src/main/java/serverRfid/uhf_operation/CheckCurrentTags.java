package serverRfid.uhf_operation;

import serverRfid.model.dto.EventReadTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Список текущих видимых меток считывателем по номерам антенн и удаление в случае пропажи видимости
public class CheckCurrentTags {

    // Время задержки видимости метки в секундах
    private final int delayVisibleTag;
    private final Map<String, EventReadTag> currentTags;

    public CheckCurrentTags(int delayVisibleTag) {
        this.currentTags = new HashMap<>();
        this.delayVisibleTag = delayVisibleTag;
    }

    //Добавляем метки, которые видны в данный момент антеннами считывателя
    public boolean putTag(EventReadTag readTag) {
        String keyTag = readTag.getEpc()+"#"+readTag.getAnt();
        // Ищем, а не находится ли она в зоне видимости этой же антенной
        EventReadTag findTag = currentTags.get(keyTag);
        if (findTag == null) {
            currentTags.put(keyTag, readTag);
            return true;
        }else{
            // Если есть, обновляем время последнего чтения этой метки
            findTag.setDateTimeLastRead(readTag.getDateTimeLastRead());
            return false;
        }
    }

    // Удаляем метки, по которым прошел таймаут задержки видимости и она больше не была видна своей антенной
    public List<EventReadTag> removeUnvisibleTags() {
        long currentEvent = System.currentTimeMillis();

        List<EventReadTag> readTags = currentTags.values().stream()
                .filter(v -> (currentEvent - v.getDateTimeLastRead())/1000 > delayVisibleTag)
                .peek(v -> v.setDateTimeLastRead(v.getDateTimeLastRead()+(delayVisibleTag*1000L)))
                .toList();

        readTags.forEach(v -> currentTags.remove(v.getEpc()+"#"+v.getAnt()));

        return readTags;
    }
}
