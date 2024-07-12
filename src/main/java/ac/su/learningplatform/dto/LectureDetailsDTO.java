package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureDetailsDTO {
    private Long id;
    private String title;
    private int attendanceCount;
    private String thumbnail;
    private Long userId;
    private Integer totalVideoCount;
    private LocalTime totalRunningTime;
}
