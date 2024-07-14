package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.StudyDetailsDTO;
import ac.su.learningplatform.dto.StudyListDTO;
import ac.su.learningplatform.dto.StudyRequestDTO;
import ac.su.learningplatform.service.StudyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
public class StudyController {

    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
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

}
