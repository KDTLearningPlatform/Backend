package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.domain.UserLectureProgress;
import ac.su.learningplatform.dto.UserLectureDTO;
import ac.su.learningplatform.dto.UserLectureRegisterDTO;
import ac.su.learningplatform.exception.UnauthorizedException;
import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.UserLectureProgressService;
import ac.su.learningplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/lectureProgress")
public class LectureProgressController {

    private final UserLectureProgressService userLectureProgressService;
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public LectureProgressController(UserLectureProgressService userLectureProgressService, JwtService jwtService, UserService userService) {
        this.userLectureProgressService = userLectureProgressService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserLectureProgress> registerLecture(@RequestBody UserLectureRegisterDTO dto, HttpSession session) {
        Long userId = authenticateUser(session);
        dto.setUserId(userId);
        UserLectureProgress userLectureProgress = userLectureProgressService.registerLecture(dto);
        return ResponseEntity.ok(userLectureProgress);
    }

    @GetMapping("/is-registered/{lectureId}")
    public ResponseEntity<Boolean> isLectureRegistered(@PathVariable Long lectureId, HttpSession session) {
        Long userId = authenticateUser(session);
        boolean isRegistered = userLectureProgressService.isLectureRegistered(userId, lectureId);
        return ResponseEntity.ok(isRegistered);
    }

    @DeleteMapping("/unregister/{lectureId}")
    public ResponseEntity<Void> unregisterLecture(@PathVariable Long lectureId, HttpSession session) {
        Long userId = authenticateUser(session);
        userLectureProgressService.unregisterLecture(userId, lectureId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<UserLectureDTO>> getInProgressLectures(HttpSession session) {
        Long userId = authenticateUser(session);
        List<UserLectureDTO> lectures = userLectureProgressService.getInProgressLectures(userId);
        return ResponseEntity.ok(lectures);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<UserLectureDTO>> getCompletedLectures(HttpSession session) {
        Long userId = authenticateUser(session);
        List<UserLectureDTO> lectures = userLectureProgressService.getCompletedLectures(userId);
        return ResponseEntity.ok(lectures);
    }

    // 강의 완료 여부 확인 API 추가
    @GetMapping("/check-complete/{lectureId}")
    public ResponseEntity<Boolean> checkLectureComplete(@PathVariable Long lectureId, HttpSession session) {
        Long userId = authenticateUser(session);
        boolean isComplete = userLectureProgressService.isLectureComplete(userId, lectureId);
        return ResponseEntity.ok(isComplete);
    }

    private Long authenticateUser(HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");

        // JWT 토큰 유효성 검사
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedException("Unauthorized access");
        }

        // 사용자 정보 추출
        String email = jwtService.extractUsername(token);
        if (email == null) {
            throw new UnauthorizedException("Invalid token");
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        return user.getUserId(); // 사용자 ID 반환
    }
}
