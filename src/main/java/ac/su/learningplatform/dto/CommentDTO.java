package ac.su.learningplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class CommentDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long userId;
        private Long studyId;
        private String content;
        private Long parentId;
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
        private Long ancestorCommentId; // 조상 댓글 ID
        private int depth; // 댓글 깊이
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PutRequest {
        private String content;
        private Long userId;
        private Long studyId;
    }
}
