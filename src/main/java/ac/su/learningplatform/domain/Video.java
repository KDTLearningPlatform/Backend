package ac.su.learningplatform.domain;

import ac.su.learningplatform.constant.DeleteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Video")
@Getter @Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")   // 비디오 ID
    private Long videoId;

    @Column(name="title", nullable = false)   // 제목
    private String title;

    @Column(name="comment") //설명
    private String comment;

    @Column(name="upload_date",nullable = false)   // 업로드 일자
    private LocalDateTime uploadDate;

    @Column(name="delete_date") // 삭제 일자
    private LocalDateTime deleteDate;

    @Enumerated(EnumType.STRING)
    @Column(name="del", nullable = false)   // 삭제여부
    private DeleteStatus del = DeleteStatus.ACTIVE; // 기본값은 ACTIVE

    @Column(name="running_time", nullable = false)  // 영상 길이
    private int runningTime;

    @Column(name="thumbnail", nullable = false)
    private String thumbnail = "default.img";   //썸네일

    @Column(name="view_count", nullable = false)  // 조회수
    private int viewCount;  // UserVideoProgress 테이블의 특정 video_id에 해당하는 user_id를 count

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