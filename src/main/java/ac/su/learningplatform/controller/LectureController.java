package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.LectureDetailsDTO;
import ac.su.learningplatform.dto.LectureListDTO;
import ac.su.learningplatform.dto.LectureRequestDTO;
import ac.su.learningplatform.service.LectureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping
    public ResponseEntity<List<LectureListDTO>> getAllLectures(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword) {
        List<LectureListDTO> lectures = lectureService.getAllFilteredLectures(tag, keyword);
        return new ResponseEntity<>(lectures, HttpStatus.OK);
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureDetailsDTO> getLectureById(@PathVariable Long lectureId) {
        LectureDetailsDTO lectureDTO = lectureService.getLectureById(lectureId);
        return new ResponseEntity<>(lectureDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LectureRequestDTO> createLecture(@RequestBody LectureRequestDTO lectureRequestDTO) {
        LectureRequestDTO createdLectureDTO = lectureService.createLecture(lectureRequestDTO);
        return new ResponseEntity<>(createdLectureDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{lectureId}")
    public ResponseEntity<LectureDetailsDTO> updateLecture(
            @PathVariable Long lectureId,
            @RequestBody LectureDetailsDTO lectureDetailsDTO) {
        LectureDetailsDTO updatedLecture = lectureService.updateLecture(lectureId, lectureDetailsDTO);
        return new ResponseEntity<>(updatedLecture, HttpStatus.OK);
    }

    @DeleteMapping("/{lectureId}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long lectureId) {
        lectureService.deleteLecture(lectureId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
