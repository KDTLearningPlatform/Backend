package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "lecture")
@Getter @Setter
public class Lecture {
    @Id
    @Column(name = "lecture_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "attendance_count", nullable = false)
    private int attendanceCount;

    @Column(name = "thumbnail")
    private String thumbnail = "default.img";

    @Column(name="del", nullable = false)
    private int del = 0;

    // N:1 매핑
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 1:N 매핑
    @OneToMany(mappedBy = "lecture")
    private List<Video> videos;

    @OneToMany(mappedBy = "lecture")
    private List<UserLectureProgress> userLectureProgresses;


}