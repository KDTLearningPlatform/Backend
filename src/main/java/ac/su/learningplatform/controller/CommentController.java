package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.dto.CommentCreateRequestDTO;
import ac.su.learningplatform.dto.CommentDTO;
import ac.su.learningplatform.exception.UnauthorizedException;
import ac.su.learningplatform.service.CommentService;
import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;
    private final UserService userService;

    public CommentController(CommentService commentService,
                             JwtService jwtService,
                             UserService userService) {
        this.commentService = commentService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO.Response> createComment(
            @RequestParam Long studyId,
            @RequestBody CommentCreateRequestDTO commentCreateRequest,
            HttpSession session) {

        Long userId = authenticateUser(session); // 사용자 인증

        commentCreateRequest.setUserId(userId);
        CommentDTO.Response response = commentService.createComment(studyId, commentCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO.Response> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDTO.Request commentRequest,
            HttpSession session) {

        Long userId = authenticateUser(session); // 사용자 인증
        verifyCommentOwner(commentId, userId); // 스터디 소유자 검증

        commentRequest.setUserId(userId);
        CommentDTO.Response updatedCommentDTO = commentService.updateComment(commentId, commentRequest);
        return new ResponseEntity<>(updatedCommentDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              HttpSession session) {

        Long userId = authenticateUser(session); // 사용자 인증
        verifyCommentOwner(commentId, userId); // 스터디 소유자 검증

        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 특정 스터디 게시글의 댓글만 불러오는 API 추가
    @GetMapping("/study/{studyId}")
    public ResponseEntity<List<CommentDTO.Response>> getCommentsByStudyId(@PathVariable Long studyId) {
        List<CommentDTO.Response> comments = commentService.getCommentsByStudyId(studyId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    private Long authenticateUser(HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");

        // JWT 토큰 유효성 검사
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedException("Unauthorized access");
        }

        // 사용자 정보 추출
        String email = jwtService.extractUsername(token);
        User user = userService.findByEmail(email);
        return user.getUserId(); // 사용자 ID 반환
    }

    private void verifyCommentOwner(Long commentId, Long userId) {
        if (!commentService.isCommentOwner(commentId, userId)) {
            throw new UnauthorizedException("수정 권한이 없습니다");
        }
    }

}
