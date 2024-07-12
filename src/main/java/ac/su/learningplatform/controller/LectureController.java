package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.LectureDetailsDTO;
import ac.su.learningplatform.dto.LectureListDTO;
import ac.su.learningplatform.service.LectureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping
    public ResponseEntity<List<LectureListDTO>> getAllLectures() {
        List<LectureListDTO> lectures = lectureService.getAllLectures();
        return new ResponseEntity<>(lectures, HttpStatus.OK);
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureDetailsDTO> getLectureById(@PathVariable Long lectureId) {
        LectureDetailsDTO lectureDTO = lectureService.getLectureById(lectureId);
        return new ResponseEntity<>(lectureDTO, HttpStatus.OK);
    }

}
