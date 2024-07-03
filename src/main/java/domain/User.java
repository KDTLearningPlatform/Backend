package domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "User")
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private int role = 0;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String profileImage;

    @Column(nullable = false)
    private int totalPoint = 0;

    @Column(nullable = false)
    private LocalDate signupDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDate updateDate = LocalDate.now();

    private LocalDate deleteDate;

    @Column(nullable = false)
    private String socialType;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private int goalVidCnt = 0;

    @Column(nullable = false)
    private int dailyVidCnt = 0;
}