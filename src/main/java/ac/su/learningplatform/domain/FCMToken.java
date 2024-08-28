package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fcm_token")
@Getter  @Setter
public class FCMToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_id")
    private Long fcmTokenId;

    @Column(name = "fcm_token")
    private String fcmToken;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

/*

// 전체적으로 아래와 같은 엔티티들이 필요할 수 있지만
// 가장 간단한 구조로 작성하면서, 기존의 엔티티를 변경하지 않는 방향으로 구현합니다.
// FCMToken entity, NotificationSetting entity 만 구현하도록 합니다.


// [ 0. FCMToken 엔티티 ]
// FCM 토큰을 저장하는 테이블
public class FCMToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_id")
    private Long fcmTokenId;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "user_id")
    private Long userId; // 사용자 ID

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 마지막 업데이트일

    @Column(name = "is_active")
    private Boolean isActive; // 토큰 활성 상태

    @Column(name = "platform")
    private String platform; // 플랫폼 정보

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate; // 만료일

// [ 1. Notification 엔티티 ]
// 발송된 알림에 대한 정보를 저장하는 테이블
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;    // 알림 ID

    @Column(name = "title")
    private String title;   // 알림 제목

    @Column(name = "message")
    private String message; // 알림 내용

    @Column(name = "sent_at")
    private LocalDateTime sentAt;   // 발송 시간

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 알림을 받은 사용자
}

// [ 2. Device 엔티티 ]
// 사용자가 사용하는 장치에 대한 정보를 저장하는 테이블

public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long deviceId;  // 장치 ID

    @Column(name = "device_type")
    private String deviceType; // (e.g. Web, Android, iOS)

    @Column(name = "model")
    private String model;

    @Column(name = "os_version")
    private String osVersion;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 장치를 소유한 사용자
}

// [ 3. NotificationSetting 엔티티 ]
// 사용자의 알림 설정 정보를 저장하는 테이블
public class NotificationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_settings_id")
    private Long notificationSettingsId;

    @Column(name = "receive_push_notifications")
    private Boolean receivePushNotifications; // 푸시 알림 수신 여부

    @Column(name = "receive_email_notifications")
    private Boolean receiveEmailNotifications; // 이메일 알림 수신 여부

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 설정을 가진 사용자
}

// [ 4. TokenBlacklist 엔티티 ]
// 블랙리스트에 추가된 토큰 정보를 저장하는 테이블
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklist_id")
    private Long blacklistId;   // 블랙리스트 ID

    @ManyToOne(name = "fcm_token_id")
    private FCMToken fcmToken;   // 블랙리스트에 추가된 토큰
}

*/