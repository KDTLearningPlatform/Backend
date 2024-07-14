package ac.su.learningplatform.domain;

import ac.su.learningplatform.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id") // 사용자 ID
    private Long userId;    // 소셜 회원가입 시 설정

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)  // 사용자 권한
    private Role role = Role.USER;  // 기본값은 User

    @Column(name="name", nullable = false)  // 이름
    private String name;    // 소셜 회원가입 시 설정

    @Column(name="email", nullable = false) // 이메일
    private String email;   // 소셜 회원가입 시 설정

    @Column(name="profile_image", nullable = false) // 프로필 이미지
    private String profileImage;    // 소셜 회원가입 시 설정

    @Column(name="total_point", nullable = false)   // 총 포인트
    private int totalPoint = 0; // 기본값은 0

    @Column(name="signup_date", nullable = false)   // 가입일자
    private LocalDateTime signupDate = LocalDateTime.now(); // 기본값은 현재시간

    @Column(name="update_date") // 수정일자
    private LocalDateTime updateDate;

    // user 는 delete flag 없게 한다고함.
    /*
    @Column(name="delete_date") // 탈퇴일자
    private LocalDateTime deleteDate;

    @Column(name="del", nullable = false)  // 삭제여부
    private DeleteStatus del = DeleteStatus.ACTIVE; // 기본값은 ACTIVE
    */

    // UUID, refreshToken 컬럼 제거

    // 유저 탈퇴 시 (1안 예정)
    /*
        1. 연관된 테이블 정보 그대로 남겨둠 (deleteStatus = ACTIVE)
        2. 연관된 테이블 정보를 soft delete (deleteStatus = DELETE)
        3. 연관된 테이블 정보를 hard delete (tuple deletion)
     */

    @Column(name="social_type", nullable = false)   // 소셜 로그인 타입
    private String socialType;  // 소셜 회원가입 시 설정     // Enum 타입으로 변경해도 됨

    @Column(name="goal_vid_cnt", nullable = false)  // 하루 목표 영상 수
    private int goalVidCnt = 0; // 기본값은 0

    @Column(name="daily_vid_cnt", nullable = false) // 하루 시청한 영상 수
    private int dailyVidCnt = 0;    // 기본값은 0

    // 1:N 매핑

    @OneToMany(mappedBy = "user")
    private List<Lecture> lectures;

    @OneToMany(mappedBy = "user")
    private List<Study> studies;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    private List<UserLectureProgress> userLectureProgresses;

    @OneToMany(mappedBy = "user")
    private List<UserVideoProgress> userStudyProgresses;



}