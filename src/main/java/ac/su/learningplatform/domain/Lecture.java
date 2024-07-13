package ac.su.learningplatform.domain;

import ac.su.learningplatform.constant.DeleteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lecture")
@Getter @Setter
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")    // 강의 ID
    private Long lectureId;

    @Column(name = "title", nullable = false)   // 제목
    private String title;

    @Column(name = "thumbnail") // 썸네일
    private String thumbnail = "default.img";

    @Column(name = "create_date", nullable = false) // 생성일자
    private LocalDateTime createDate = LocalDateTime.now(); // 기본값은 현재시간

    @Column(name = "update_date") // 수정일자
    private LocalDateTime updateDate;

    @Column(name = "delete_date") // 삭제일자
    private LocalDateTime deleteDate;

    @Enumerated(EnumType.STRING)
    @Column(name="del", nullable = false)   // 삭제여부
    private DeleteStatus del = DeleteStatus.ACTIVE; // 기본값은 ACTIVE

    @Column(name="tag") // 태그(주제)
    private String tag;

    @Column(name = "attendance_count", nullable = false)    // 수강자 수 (조회수)
    private int attendanceCount;    // UserLectureProgress 테이블의 특정 lecture_id에 해당하는 userI를 count

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