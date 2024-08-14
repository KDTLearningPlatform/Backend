package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.dto.CommentDTO;
import ac.su.learningplatform.exception.UnauthorizedException;
import ac.su.learningplatform.service.CommentService;
import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping // URL 이 차이가 나므로 개별선언합니다.
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

    // 댓글 생성
    @PostMapping("/api/comments")
    public ResponseEntity<CommentDTO.Response> createComment(
            @RequestBody CommentDTO.Request commentRequest,
            @RequestParam Long studyId,
            HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String userEmail = jwtService.extractUsername(token);
        Long userId = userService.findByEmail(userEmail).getUserId();

        commentRequest.setUserId(userId);

        CommentDTO.Response createdCommentDTO = commentService.createComment(studyId, commentRequest);
        return new ResponseEntity<>(createdCommentDTO, HttpStatus.CREATED);
    }

    // 댓글 수정
    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDTO.Response> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDTO.Request commentRequest,
            HttpSession session)
    {
        Long userId = authenticateUser(session); // 사용자 인증

        verifyStudyOwner(commentId, userId); // 스터디 소유자 검증

        CommentDTO.Response updatedCommentDTO = commentService.updateComment(commentId, commentRequest);
        return new ResponseEntity<>(updatedCommentDTO, HttpStatus.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            HttpSession session)
    {
        Long userId = authenticateUser(session); // 사용자 인증

        verifyStudyOwner(commentId, userId); // 스터디 소유자 검증

        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    private void verifyStudyOwner(Long commentId, Long userId) {
        if (!commentService.isCommentOwner(commentId, userId)) {
            throw new UnauthorizedException("수정 권한이 없습니다");
        }
    }
}
