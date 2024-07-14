package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.CommentDTO;
import ac.su.learningplatform.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping // URL 이 차이가 나므로 개별선언합니다.
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/api/studies/{studyId}/comments")
    public ResponseEntity<CommentDTO.Response> createComment(
            @PathVariable Long studyId,
            @RequestBody CommentDTO.Request commentRequest)
    {
        CommentDTO.Response createdCommentDTO = commentService.createComment(studyId, commentRequest);
        return new ResponseEntity<>(createdCommentDTO, HttpStatus.CREATED);
    }

    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDTO.Response> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDTO.Request commentRequest)
    {
        CommentDTO.Response updatedCommentDTO = commentService.updateComment(commentId, commentRequest);
        return new ResponseEntity<>(updatedCommentDTO, HttpStatus.OK);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId)
    {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
