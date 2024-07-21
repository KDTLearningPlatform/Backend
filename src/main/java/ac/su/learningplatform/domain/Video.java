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
    @Column(name = "video_Id")   // 비디오 ID, video_id->Id
    private Long videoId;

    @Column(name="video_order")   // 순서(강의 내 순서)
    private int videoOrder;

    @Column(name="title", nullable = false)   // 제목
    private String title;

    @Column(name="content", nullable = false) // 비디오 내용 (URL) or S3 링크
    private String content; // 예: https://my-video-bucket.s3.amazonaws.com/video.mp4

    @Column(name="upload_date",nullable = false)   // 업로드 일자
    private LocalDateTime uploadDate;

    @Column(name="delete_date") // 삭제 일자
    private LocalDateTime deleteDate;

    @Enumerated(EnumType.STRING)
    @Column(name="del", nullable = false)   // 삭제여부
    private DeleteStatus del = DeleteStatus.ACTIVE; // 기본값은 ACTIVE

    @Column(name="running_time", nullable = false)  // 영상 길이
    private int runningTime;

    /* 비디오의 썸네일과 조회수제거 (2024-07-14_10:42)
    @Column(name="thumbnail", nullable = false)
    private String thumbnail = "default.img";   //썸네일

    @Column(name="view_count", nullable = false)  // 조회수
    private int viewCount;  // UserVideoProgress 테이블의 특정 video_id에 해당하는 user_id를 count
     */

    // N:1 매핑

    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    // 1:N 매핑
    @OneToMany(mappedBy = "video")
    private List<UserVideoProgress> userVideoProgresses;
}