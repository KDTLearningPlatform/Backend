package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.LectureCompletedDto;
import ac.su.learningplatform.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long id) {
        boolean isDeleted = lectureService.deleteLecture(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/completed/{userId}")
    public List<LectureCompletedDto> getCompletedLectures(@PathVariable Long userId) {
        return lectureService.getCompletedLectures(userId);
    }
}