package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.LectureDetailsDTO;
import ac.su.learningplatform.dto.LectureListDTO;
import ac.su.learningplatform.dto.LectureRequestDTO;
import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.LectureService;
import ac.su.learningplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;
    private final JwtService jwtService;
    private final UserService userService;

    public LectureController(LectureService lectureService, JwtService jwtService, UserService userService) {
        this.lectureService = lectureService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @GetMapping("/createLecture")
    public String createLecturePage() {
        return "createLecture";
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
    public ResponseEntity<LectureRequestDTO> createLecture(
            @RequestPart("lecture") LectureRequestDTO lectureRequestDTO,
            @RequestPart("files") List<MultipartFile> files, HttpSession session) {

        String jwtToken = (String) session.getAttribute("jwtToken");

        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String userEmail = jwtService.extractUsername(jwtToken);
        Long userId = userService.findByEmail(userEmail).getUserId();

        lectureRequestDTO.setUserId(userId);
        LectureRequestDTO createdLectureDTO = lectureService.createLecture(lectureRequestDTO, files);
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
