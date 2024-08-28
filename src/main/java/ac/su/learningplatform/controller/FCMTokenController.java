package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.FCMToken;
import ac.su.learningplatform.service.FCMTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fcm")
public class FCMTokenController {

    private final FCMTokenService fcmTokenService;

    public FCMTokenController(FCMTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }

    // FCM 토큰 저장
    @PostMapping("/save-fcm-token")
    public ResponseEntity<String> saveFCMToken(@RequestBody FCMToken tokenRequest) {
        if (tokenRequest.getFcmToken() == null || tokenRequest.getFcmToken().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FCM 토큰이 제공되지 않았습니다.");
        }

        fcmTokenService.saveToken(tokenRequest.getFcmToken());
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
