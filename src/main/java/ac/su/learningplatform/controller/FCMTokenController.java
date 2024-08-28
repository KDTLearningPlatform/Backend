package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.FCMToken;
import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.dto.FCMRequestDTO;
import ac.su.learningplatform.exception.UnauthorizedException;
import ac.su.learningplatform.service.FCMTokenService;
import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fcm")
public class FCMTokenController {

    private final FCMTokenService fcmTokenService;
    private final JwtService jwtService;
    private final UserService userService;

    public FCMTokenController(FCMTokenService fcmTokenService, JwtService jwtService, UserService userService) {
        this.fcmTokenService = fcmTokenService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    // FCM 토큰 저장
    @PostMapping("/save-fcm-token")
    public ResponseEntity<String> saveFCMToken(
            @RequestBody FCMRequestDTO tokenRequest,
            HttpSession session) {

        String jwtToken = (String) session.getAttribute("jwtToken");
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new UnauthorizedException("Unauthorized access");
        }
        String email = jwtService.extractUsername(jwtToken);
        User user = userService.findByEmail(email);
        Long userId = user.getUserId();
        System.out.println("유저: " + user);
        System.out.println("유저 아이디: " + userId);

        if (tokenRequest.getFcmToken() == null || tokenRequest.getFcmToken().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FCM 토큰이 제공되지 않았습니다.");
        }
        tokenRequest.setUserId(userId);
        fcmTokenService.saveToken(tokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body("FCM 토큰이 성공적으로 저장되었습니다.");
    }

    // FCM 토큰 삭제
    @PostMapping("/delete-fcm-token")
    public ResponseEntity<String> deleteFCMToken(@RequestBody FCMToken tokenRequest) {
        if (tokenRequest.getFcmToken() == null || tokenRequest.getFcmToken().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FCM 토큰이 제공되지 않았습니다.");
        }

        boolean success = fcmTokenService.deleteToken(tokenRequest.getFcmToken());
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).body("FCM 토큰이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FCM 토큰을 찾을 수 없습니다.");
        }
    }
}
