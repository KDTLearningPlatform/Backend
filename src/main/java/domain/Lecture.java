package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lecture")
@Getter @Setter
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int attendanceCount;

    private String thumbnail = "default.img";

    @Column(nullable = false)
    private int del = 0;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}