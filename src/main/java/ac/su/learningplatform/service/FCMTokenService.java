package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.FCMToken;
import ac.su.learningplatform.repository.FCMRepository;
import org.springframework.stereotype.Service;

@Service
public class FCMTokenService {

    private final FCMRepository fcmRepository;

    public FCMTokenService(FCMRepository fcmRepository) {
        this.fcmRepository = fcmRepository;
    }

    // FCM 토큰 저장
    public void saveToken(String token) {
        FCMToken fcmToken = new FCMToken();
        fcmToken.setFcmToken(token);
        fcmRepository.save(fcmToken);
        System.out.println("Received token: " + token);
    }

    // FCM 토큰 삭제
    public boolean deleteToken(String token) {
        try {
            FCMToken fcmToken = fcmRepository.findByFcmToken(token);
            if (fcmToken != null) {
                fcmRepository.delete(fcmToken);
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
