package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Video")
@Getter @Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long videoId;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="comment")
    private String comment;

    @Column(name="upload_date",nullable = false)
    private LocalDate uploadDate;

    @Column(name="running_time", nullable = false)
    private LocalTime runningTime;

    @Column(name="thumbnail", nullable = false)
    private String thumbnail = "default.img";

    @Column(name="total_view", nullable = false)
    private int totalView = 0;

    // N:1 매핑
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    // 1:N 매핑
    @OneToMany(mappedBy = "video")
    private List<UserVideoProgress> userVideoProgresses;
}