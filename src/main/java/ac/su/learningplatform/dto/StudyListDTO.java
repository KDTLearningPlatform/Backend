package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudyListDTO {
    private Long studyId;
    private String title;
    private String field;
    private int commentCount;
    private LocalDateTime createDate;
    private Long userId;
    private boolean liked; // 좋아요 여부 추가
}
