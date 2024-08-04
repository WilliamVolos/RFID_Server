package serverRfid.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import serverRfid.exception.ReaderException;
import serverRfid.model.dto.JournalTagInfo;
import serverRfid.model.dto.ReaderStatusInfo;
import serverRfid.model.dto.ReaderRequestDto;
import serverRfid.services.dbservice.JournalService;
import serverRfid.services.dbservice.ReaderService;
import serverRfid.services.mainservice.MainReaderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReaderRestController {

    private final MainReaderService mainReaderService;
    private final ReaderService readerService;
    private final JournalService journalService;

    // Добавить и запустить новый считыватель: model, ipAddress, port
    @PostMapping("/api/reader")
    public ReaderStatusInfo addAndRunNewReader(@RequestBody ReaderRequestDto readerRequestDto) {
        return mainReaderService.addAndRunNewReader(readerRequestDto);
    }

    // Получить информацию о считывателе по его IP адресу
    @GetMapping("/api/reader/{ipaddress}")
    public ReaderStatusInfo getReaderInfoByIpAddress(@PathVariable(name = "ipaddress") String ipAddress) {
        return mainReaderService.getReaderInfoByIpAddress(ipAddress).orElse(null);
    }

    // Получить информацию о считывателе по его уникальному имени
    @GetMapping("/api/reader/{name}")
    public ReaderStatusInfo getReaderInfoByName(@PathVariable(name = "name") String name) {
        return mainReaderService.getReaderInfoByName(name).orElse(null);
    }

    // Получить список активных считывателей
    @GetMapping("/api/reader/active")
    public List<ReaderStatusInfo> getActiveReaders() {
        return mainReaderService.getActiveReaders();
    }

    // Получить список отключенных считывателей
    @GetMapping("/api/reader/disable")
    public List<ReaderStatusInfo> getDisabledReaders() {
        return mainReaderService.getDisabledReaders();
    }

    // Получить последние N считанных меток по имени считывателя
    @GetMapping("/api/reader/{name},{limit}")
    public List<JournalTagInfo> getTagsInfoByReaderNameAndLimit(@PathVariable(name = "name") String name, @PathVariable String limit) {
        var reader = readerService.getReaderByName(name);
        if (reader.isPresent()) {

            return journalService.findLastByReaderAndLimit(reader.get(), Integer.parseInt(limit));
        } else throw new ReaderException("Не найден считыватель с таким именем");
    }

    // Удалить считыватель (по имени устройства)
    @DeleteMapping("/api/reader/{name}")
    public void deleteReaderByName(@PathVariable(name = "name") String name){
        mainReaderService.deleteReaderByName(name);
    }

    // Удалить считыватель (по IP адресу)
    @DeleteMapping("/api/reader/{ipaddress}")
    public void deleteReaderByIpAddress(@PathVariable(name = "ipaddress") String ipAddress){
        mainReaderService.deleteReaderByIpAddress(ipAddress);
    }
}
