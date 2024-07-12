package zerobase.weather.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController

public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping("/create/diary")
    void createDiary(@RequestParam(name = "date")
                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.createDiary(date, text);
    }

    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam("date")
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return diaryService.readDiary(date);
    }

    @GetMapping("/read/diaries")
    public ResponseEntity<List<Diary>> readDiaries(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Diary> diaries = diaryService.readDiaries(startDate, endDate);
        return ResponseEntity.ok(diaries);
    }

 @PutMapping("/update/diary")
   void updateDiary(@RequestParam("date")
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text){
                  diaryService.updateDiary(date, text);
    }
//    @DeleteMapping("/delete/diary")
//    void deleteDiary(@RequestParam("date")
//                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date , @RequestBody String text){
//                   diaryService.deleteDiary(date);
 @DeleteMapping("/delete/diary")
void deleteDiary(@RequestParam("date")
                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    diaryService.deleteDiary(date);
}
}
