package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "Study")
@Getter @Setter
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String field;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private LocalDate createDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDate updateDate = LocalDate.now();

    private LocalDate deleteDate;

    @Column(nullable = false)
    private int del = 0;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}