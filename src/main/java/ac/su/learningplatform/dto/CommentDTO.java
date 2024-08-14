package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class CommentDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String content;
        private Long userId;
        private Long parentCommentId; // 부모 댓글 ID 추가
        private Long studyId; // 스터디 ID 추가
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long commentId;
        private String content;
        private LocalDateTime createDate;
        private Long userId;
        private Long parentCommentId; // 부모 댓글 ID 추가
    }
}
