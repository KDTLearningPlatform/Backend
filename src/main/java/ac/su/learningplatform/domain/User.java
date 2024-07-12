package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(name="role", nullable = false)
    private int role = 0;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="profile_image", nullable = false)
    private String profileImage;

    @Column(name="total_point", nullable = false)
    private int totalPoint = 0;

    @Column(name="signup_date", nullable = false)
    private LocalDate signupDate = LocalDate.now();

    @Column(name="update_date")
    private LocalDate updateDate;

    @Column(name="delete_date")
    private LocalDate deleteDate;

    @Column(name="social_type", nullable = false)
    private String socialType;

    @Column(name="goal_vid_cnt", nullable = false)
    private int goalVidCnt = 0;

    @Column(name="daily_vid_cnt", nullable = false)
    private int dailyVidCnt = 0;

    // 1:N 매핑
    @OneToMany(mappedBy = "user")
    private List<Video> videos;

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