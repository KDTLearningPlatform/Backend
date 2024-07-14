package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureRequestDTO {
    private String thumbnail;
    private String tag;
    private String title;
    private String comment;
    private Long userId;
    private List<VideoDTO.Request> videos;

}
