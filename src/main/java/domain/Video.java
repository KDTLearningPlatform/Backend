package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "Video")
@Getter @Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String comment;

    @Column(nullable = false)
    private LocalDate uploadDate;

    @Column(nullable = false)
    private int runningTime;

    @Column(nullable = false)
    private String thumbnail = "default.img";

    @Column(nullable = false)
    private int totalView = 0;

    @Column(nullable = false)
    private int del = 0;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;
}