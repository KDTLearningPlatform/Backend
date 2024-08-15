package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.dto.StudyDetailsDTO;
import ac.su.learningplatform.dto.StudyListDTO;
import ac.su.learningplatform.service.*;
import ac.su.learningplatform.dto.StudyDTO;
import ac.su.learningplatform.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/studies")
public class StudyController {

    private final StudyService studyService;
    private final JwtService jwtService;
    private final UserService userService;

    public StudyController(StudyService studyService,
                           JwtService jwtService,
                           UserService userService) {
        this.studyService = studyService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    // 스터디 목록 조회
    @GetMapping
    public ResponseEntity<List<StudyListDTO>> getAllStudies(HttpSession session) {

        Long userId = authenticateUser(session); // 사용자 인증
        List<StudyListDTO> studies = studyService.getAllStudies(userId);

        return new ResponseEntity<>(studies, HttpStatus.OK);
    }

    // 스터디 세부 정보
    @GetMapping("/{studyId}")
    public ResponseEntity<StudyDetailsDTO> getStudyInfo(@PathVariable Long studyId, HttpSession session) {

        Long userId = authenticateUser(session); // 사용자 인증
        StudyDetailsDTO studyDTO = studyService.getStudyDetailsById(studyId, userId);
        return new ResponseEntity<>(studyDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<StudyDTO.Request> createStudy(@RequestBody StudyDTO.Request studyDTO, HttpSession session) {

        Long userId = authenticateUser(session); // 사용자 인증

        studyDTO.setUserId(userId);
        StudyDTO.Request createdStudyDTO = studyService.createStudy(studyDTO);
        return new ResponseEntity<>(createdStudyDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{studyId}")
    public ResponseEntity<StudyDTO.Request> updateStudy(
            @PathVariable Long studyId,
            @RequestBody StudyDTO.Request studyDTO,
            HttpSession session) {

        Long userId = authenticateUser(session); // 사용자 인증

        verifyStudyOwner(studyId, userId); // 스터디 소유자 검증

        // 스터디모집 게시글 업데이트
        StudyDTO.Request updatedStudyDTO = studyService.updateStudy(studyId, studyDTO);
        return new ResponseEntity<>(updatedStudyDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{studyId}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long studyId, HttpSession session) {

        Long userId = authenticateUser(session); // 사용자 인증

        verifyStudyOwner(studyId, userId); // 스터디 소유자 검증

        studyService.deleteStudy(studyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Long authenticateUser(HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");

        // JWT 토큰 유효성 검사
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedException("Unauthorized access");
        }

        // 사용자 정보 추출
        String email = jwtService.extractUsername(token);
        User user = userService.findByEmail(email);
        return user.getUserId(); // 사용자 ID 반환
    }

    private void verifyStudyOwner(Long studyId, Long userId) {
        if (!studyService.isStudyOwner(studyId, userId)) {
            throw new UnauthorizedException("수정 권한이 없습니다");
        }
    }


}
