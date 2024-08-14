package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.CommentDTO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/studies")
public class StudyController {

    private final StudyService studyService;
    private final CommentService commentService;
    private final JwtService jwtService;
    private final UserService userService;

    public StudyController(StudyService studyService,
                           JwtService jwtService,
                           UserService userService,
                           CommentService commentService) {
        this.studyService = studyService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.commentService = commentService;
    }

//    @GetMapping
//    public ResponseEntity<List<StudyListDTO>> getAllStudies() {
//        List<StudyListDTO> studies = studyService.getAllStudies();
//        return new ResponseEntity<>(studies, HttpStatus.OK);
//    }

    // 스터디 목록 조회 (좋아요 여부 반환)
    @GetMapping
    public ResponseEntity<List<StudyListDTO>> getAllStudies(HttpSession session) {
        // JWT 토큰 추출
        String token = (String) session.getAttribute("jwtToken");

        // 토큰이 없거나 비어있으면 Unauthorized 에러 반환
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // JWT 토큰에서 사용자 이메일 추출
        String email = jwtService.extractUsername(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 이메일을 통해 사용자 정보를 조회
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 사용자 ID를 얻어서 스터디 목록 조회
        Long userId = user.getUserId();
        List<StudyListDTO> studies = studyService.getAllStudies(userId);

        return new ResponseEntity<>(studies, HttpStatus.OK);
    }

    @GetMapping("/details/{studyId}")
    public ResponseEntity<Map<String, Object>> studyDetailsPage(@PathVariable Long studyId, HttpSession session) {

        authenticateUser(session);

        Map<String, Object> response = new HashMap<>();
        StudyDetailsDTO studyDetails = studyService.getStudyDetailsById(studyId);
        response.put("studyDetails", studyDetails);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/editStudy/{studyId}")
    public ResponseEntity<Map<String, Object>> editStudyPage(@PathVariable Long studyId, HttpSession session) {

        // 인증하는부분 start
        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("redirect", "/auth/chooseSocialLogin"));
        }

        String email = jwtService.extractUsername(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("errorMessage", "사용자 정보를 불러오는 중 오류가 발생했습니다."));
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("errorMessage", "사용자 정보를 불러오는 중 오류가 발생했습니다."));
        }

        StudyDTO.Response study = studyService.getStudyById(studyId);
        if (study == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errorMessage", "강의를 찾을 수 없습니다."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("study", study);
        // 인증하는 부분 end

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{studyId}")
    public ResponseEntity<StudyDetailsDTO> getStudyById(@PathVariable Long studyId) {
        StudyDetailsDTO studyDTO = studyService.getStudyDetailsById(studyId);
        return new ResponseEntity<>(studyDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<StudyDTO.Request> createStudy(@RequestBody StudyDTO.Request studyDTO, HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String userEmail = jwtService.extractUsername(token);
        Long userId = userService.findByEmail(userEmail).getUserId();

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

    // 새로운 댓글 목록 가져오기 엔드포인트 추가
    @GetMapping("/{studyId}/comments")
    public ResponseEntity<List<CommentDTO.Response>> getCommentsByStudyId(@PathVariable Long studyId) {
        List<CommentDTO.Response> comments = commentService.getCommentsByStudyId(studyId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
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


    // dto로 변경으로 인한 Test용 Controller / 삭제예정
    @GetMapping("/studyInfo/{studyId}")
    public ResponseEntity<StudyDTO.Response> getStudyInfo(@PathVariable Long studyId) {
        StudyDTO.Response studyDTO = studyService.getStudyById(studyId);
        return new ResponseEntity<>(studyDTO, HttpStatus.OK);
    }


}
