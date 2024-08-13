package ac.su.learningplatform.controller;

import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.LoveService;
import ac.su.learningplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/loves")
public class LoveController {

    private final LoveService loveService;
    private final JwtService jwtService;
    private final UserService userService;

    public LoveController(LoveService loveService, JwtService jwtService, UserService userService) {
        this.loveService = loveService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    // 좋아요 추가 또는 삭제
    @PostMapping("/{studyId}")
    public ResponseEntity<String> toggleLove(@PathVariable Long studyId, HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String userEmail = jwtService.extractUsername(token);
        Long userId = userService.findByEmail(userEmail).getUserId();

        boolean isLoved = loveService.toggleLove(studyId, userId);
        String message = isLoved ? "좋아요 추가됨." : "좋아요 삭제됨.";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }


}
