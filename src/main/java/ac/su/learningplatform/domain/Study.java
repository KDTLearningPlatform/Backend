package ac.su.learningplatform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Study")
@Getter @Setter
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="study_id")
    private Long study_id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="field", nullable = false)
    private String field;

    @Column(name="view_count", nullable = false)
    private int viewCount;

    @Column(name="create_date", nullable = false)
    private LocalDate createDate = LocalDate.now();

    @Column(name="update_date", nullable = false)
    private LocalDate updateDate = LocalDate.now();

    @Column(name="delete_date")
    private LocalDate deleteDate;

    @Column(name="del", nullable = false)
    private int del = 0;

    // N:1 매핑
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 1:N 매핑
    @OneToMany(mappedBy = "study")
    private List<Comment> comments;
}