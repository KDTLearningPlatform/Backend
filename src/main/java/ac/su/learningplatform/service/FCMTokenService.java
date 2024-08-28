package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.FCMToken;
import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.dto.FCMRequestDTO;
import ac.su.learningplatform.repository.FCMTokenRepository;
import ac.su.learningplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FCMTokenService {

    private final FCMTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    public FCMTokenService(FCMTokenRepository fcmTokenRepository, UserRepository userRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
        this.userRepository = userRepository;
    }

    public void saveToken(FCMRequestDTO tokenRequest) {
        // User 엔티티 조회
        User user = userRepository.findById(tokenRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // FCMToken 엔티티 생성 및 설정
        FCMToken fcmToken = new FCMToken();
        fcmToken.setFcmToken(tokenRequest.getFcmToken());
        fcmToken.setUser(user);

        // FCMToken 엔티티 저장
        fcmTokenRepository.save(fcmToken);

        System.out.println("Received token: " + tokenRequest.getFcmToken());
    }

    // FCM 토큰 삭제
    public boolean deleteToken(String token) {
        try {
            FCMToken fcmToken = fcmTokenRepository.findByFcmToken(token);
            if (fcmToken != null) {
                fcmTokenRepository.delete(fcmToken);
                System.out.println("Deleted token: " + token);
                return true;
            } else {
                System.out.println("Token not found: " + token);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting token: " + e.getMessage());
            return false;
        }
    }

}
