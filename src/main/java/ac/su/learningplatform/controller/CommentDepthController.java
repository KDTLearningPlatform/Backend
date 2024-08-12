package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.CommentDTO;
import ac.su.learningplatform.service.CommentDepthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentDepthController {


    private final CommentDepthService commentDepthService;


    public CommentDepthController(CommentDepthService commentDepthService) {
        this.commentDepthService = commentDepthService;
    }

    /**
     * 새로운 댓글을 생성하는 엔드포인트
     *
     * @param commentRequest 댓글 생성 요청 DTO
     * @param studyId 스터디 게시글 ID
     * @return 생성된 댓글의 응답 DTO와 HTTP 상태 코드 201(CREATED)
     */
    @PostMapping
    public ResponseEntity<CommentDTO.Response> createComment(@RequestBody CommentDTO.Request commentRequest, @RequestParam Long studyId) {
        // CommentDepthService를 이용해 새로운 댓글 생성
        CommentDTO.Response createdComment = commentDepthService.createComment(studyId, commentRequest);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    /**
     * 특정 스터디 게시글에 대한 모든 댓글을 조회하는 엔드포인트
     *
     * @param studyId 스터디 게시글 ID
     * @return 댓글 목록의 응답 DTO와 HTTP 상태 코드 200(OK)
     */
    @GetMapping("/study/{studyId}")
    public ResponseEntity<List<CommentDTO.Response>> getCommentsByStudyId(@PathVariable Long studyId) {
        // CommentDepthService를 이용해 특정 스터디 게시글의 모든 댓글 조회
        List<CommentDTO.Response> comments = commentDepthService.getCommentsByStudyId(studyId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
