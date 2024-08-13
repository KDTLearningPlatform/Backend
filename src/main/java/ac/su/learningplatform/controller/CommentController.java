package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.CommentDTO;
import ac.su.learningplatform.service.CommentDepthService;
import ac.su.learningplatform.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentDepthService commentDepthService;

    public CommentController(CommentService commentService, CommentDepthService commentDepthService) {
        this.commentService = commentService;
        this.commentDepthService = commentDepthService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO.Response> createComment(@RequestBody CommentDTO.Request commentRequest) {
        CommentDTO.Response createdCommentDTO = commentDepthService.createComment(commentRequest.getStudyId(), commentRequest);
        return new ResponseEntity<>(createdCommentDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO.Response> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO.Request commentRequest) {
        CommentDTO.Response updatedCommentDTO = commentDepthService.updateComment(commentId, commentRequest);
        return new ResponseEntity<>(updatedCommentDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentDepthService.deleteComment(commentId);
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/study/{studyId}")
    public ResponseEntity<List<CommentDTO.Response>> getCommentsByStudyId(@PathVariable Long studyId) {
        List<CommentDTO.Response> comments = commentDepthService.getCommentsByStudyId(studyId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
