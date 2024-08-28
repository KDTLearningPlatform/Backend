package ac.su.learningplatform.scheduler;

import ac.su.learningplatform.domain.FCMToken;
import ac.su.learningplatform.repository.FCMTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.api.core.ApiFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class DailyNotificationScheduler {

    @Autowired
    private FCMTokenRepository fcmTokenRepository;

    // 매 5초마다 실행
    @Scheduled(fixedRate = 10000)   // 10초마다 실행
//    @Scheduled(cron = "0 0 19 * * *") // 매일 오후 7시에 실행
    public void sendDailyNotifications() {
        // 모든 FCM 토큰을 조회
        List<FCMToken> tokens = fcmTokenRepository.findAll();

        for (FCMToken token : tokens) {
            String userName = token.getUser().getName(); // 사용자 이름
            int goalVidCnt = token.getUser().getGoalVidCnt(); // 목표 비디오 수
            int dailyVidCnt = token.getUser().getDailyVidCnt(); // 오늘 본 비디오 수

            // 메시지 제목과 내용 설정
            String title = userName + "님, 오늘의 학습 현황입니다!";
            String body = String.format("목표 비디오 수: %d개\n오늘 시청한 비디오 수: %d개", goalVidCnt, dailyVidCnt);

            // FCM 메시지 전송
            sendNotification(title, body, token.getFcmToken());
        }
    }

    private void sendNotification(String title, String body, String token) {
        // Notification 객체 생성
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // 데이터 페이로드 추가
        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);

        // FCM 메시지 생성
        Message message = Message.builder()
                .setNotification(notification)
                .putAllData(data)  // 데이터 페이로드 추가
                .setToken(token)
                .build();

        // 비동기로 메시지 전송
        ApiFuture<String> responseFuture = FirebaseMessaging.getInstance().sendAsync(message);

        try {
            // 전송 결과 확인
            String messageId = responseFuture.get();
            System.out.println("Notification sent to token: " + token + ". Message ID: " + messageId);
        } catch (InterruptedException | ExecutionException e) {
            // 예외 처리
            System.err.println("Failed to send notification to token: " + token);
            e.printStackTrace();
        }
    }
}
