package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.CommentDTO;
import ac.su.learningplatform.dto.StudyDetailsDTO;
import ac.su.learningplatform.dto.StudyListDTO;
import ac.su.learningplatform.dto.StudyRequestDTO;
import ac.su.learningplatform.service.CommentService;
import ac.su.learningplatform.service.StudyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
public class StudyController {

    private final StudyService studyService;
    private final CommentService commentService;

    public StudyController(StudyService studyService, CommentService commentService) {
        this.studyService = studyService;
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<StudyListDTO>> getAllStudies() {
        List<StudyListDTO> studies = studyService.getAllStudies();
        return new ResponseEntity<>(studies, HttpStatus.OK);
    }

    @GetMapping("/{studyId}")
    public ResponseEntity<StudyDetailsDTO> getStudyById(@PathVariable Long studyId) {
        StudyDetailsDTO studyDTO = studyService.getStudyById(studyId);
        return new ResponseEntity<>(studyDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<StudyRequestDTO> createStudy(@RequestBody StudyRequestDTO studyRequestDTO) {
        StudyRequestDTO createdStudyDTO = studyService.createStudy(studyRequestDTO);
        return new ResponseEntity<>(createdStudyDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{studyId}")
    public ResponseEntity<StudyRequestDTO> updateStudy(
            @PathVariable Long studyId,
            @RequestBody StudyRequestDTO studyRequestDTO) {
        StudyRequestDTO updatedStudyDTO = studyService.updateStudy(studyId, studyRequestDTO);
        return new ResponseEntity<>(updatedStudyDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{studyId}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long studyId) {
        studyService.deleteStudy(studyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 새로운 댓글 목록 가져오기 엔드포인트 추가
    @GetMapping("/{studyId}/comments")
    public ResponseEntity<List<CommentDTO.Response>> getCommentsByStudyId(@PathVariable Long studyId) {
        List<CommentDTO.Response> comments = commentService.getCommentsByStudyId(studyId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
