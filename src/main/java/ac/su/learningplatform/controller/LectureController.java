package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.dto.LectureDetailsDTO;
import ac.su.learningplatform.dto.LectureListDTO;
import ac.su.learningplatform.dto.LectureRequestDTO;
import ac.su.learningplatform.exception.UnauthorizedException;
import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.LectureService;
import ac.su.learningplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping
    public ResponseEntity<List<LectureListDTO>> getAllLectures(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword) {
        List<LectureListDTO> lectures = lectureService.getAllFilteredLectures(tag, keyword);
        return new ResponseEntity<>(lectures, HttpStatus.OK);
    }

    @GetMapping("/details/{lectureId}")
    public ResponseEntity<Map<String, Object>> lectureDetailsPage(@PathVariable Long lectureId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        LectureDetailsDTO lectureDetails = lectureService.getLectureById(lectureId);
        response.put("lectureDetails", lectureDetails);

        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            response.put("redirect", "/auth/chooseSocialLogin");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String email = jwtService.extractUsername(token);
        if (email == null) {
            response.put("errorMessage", "사용자 정보를 불러오는 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            response.put("errorMessage", "사용자 정보를 불러오는 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("currentUserId", user.getUserId());
        response.put("lectureUserId", lectureDetails.getUserId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/editLecture/{lectureId}")
    public ResponseEntity<Map<String, Object>> editLecturePage(@PathVariable Long lectureId, HttpSession session) {
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

        LectureDetailsDTO lecture = lectureService.getLectureById(lectureId);
        if (lecture == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errorMessage", "강의를 찾을 수 없습니다."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("lecture", lecture);
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("goalVidCnt", user.getGoalVidCnt());
        response.put("dailyVidCnt", user.getDailyVidCnt());

        return ResponseEntity.ok(response);
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
            @RequestPart("lecture") LectureDetailsDTO lectureDetailsDTO,
            @RequestPart("files") List<MultipartFile> files,
            HttpSession session) {

        Long userId = authenticateUser(session); // 사용자 인증

        verifyLectureOwner(lectureId, userId); // 강의 소유자 검증

        // 강의 업데이트
        LectureDetailsDTO updatedLecture = lectureService.updateLecture(lectureId, lectureDetailsDTO, files, userId);
        return new ResponseEntity<>(updatedLecture, HttpStatus.OK);
    }

    @DeleteMapping("/{lectureId}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long lectureId, HttpSession session) {
        Long userId = authenticateUser(session); // 사용자 인증

        verifyLectureOwner(lectureId, userId); // 강의 소유자 검증

        // 강의 삭제
        lectureService.deleteLecture(lectureId);
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

    private void verifyLectureOwner(Long lectureId, Long userId) {
        if (!lectureService.isLectureOwner(lectureId, userId)) {
            throw new UnauthorizedException("수정 권한이 없습니다");
        }
    }

}
