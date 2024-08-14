//package ac.su.learningplatform.controller;
//
//import ac.su.learningplatform.domain.User;
//import ac.su.learningplatform.dto.CommentDTO;
//import ac.su.learningplatform.exception.UnauthorizedException;
//import ac.su.learningplatform.service.CommentDepthService;
//import ac.su.learningplatform.service.JwtService;
//import ac.su.learningplatform.service.UserService;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/comments")
//public class CommentDepthController {
//
//    private final CommentDepthService commentDepthService;
//    private final JwtService jwtService;
//    private final UserService userService;
//
//    public CommentDepthController(CommentDepthService commentDepthService, JwtService jwtService, UserService userService) {
//        this.commentDepthService = commentDepthService;
//        this.jwtService = jwtService;
//        this.userService = userService;
//    }
//
//    @PostMapping
//    public ResponseEntity<CommentDTO.Response> createComment(
//            @RequestBody CommentDTO.Request commentRequest,
//            @RequestParam Long studyId,
//            HttpSession session) {
//
//        // 사용자 인증
//        Long userId = authenticateUser(session);
//        commentRequest.setUserId(userId); // 인증된 사용자의 ID 설정
//
//        // CommentDepthService를 이용해 새로운 댓글 생성
//        CommentDTO.Response createdComment = commentDepthService.createComment(studyId, commentRequest);
//        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/study/{studyId}")
//    public ResponseEntity<List<CommentDTO.Response>> getCommentsByStudyId(
//            @PathVariable Long studyId,
//            HttpSession session) {
//
//        // 사용자 인증 (댓글 조회에 권한이 있는지 확인하기 위해 필요)
//        authenticateUser(session);
//
//        // CommentDepthService를 이용해 특정 스터디 게시글의 모든 댓글 조회
//        List<CommentDTO.Response> comments = commentDepthService.getCommentsByStudyId(studyId);
//        return new ResponseEntity<>(comments, HttpStatus.OK);
//    }
//
//    // 사용자 인증 메소드
//    private Long authenticateUser(HttpSession session) {
//        String token = (String) session.getAttribute("jwtToken");
//
//        // JWT 토큰 유효성 검사
//        if (token == null || token.isEmpty()) {
//            throw new UnauthorizedException("Unauthorized access");
//        }
//
//        // 사용자 정보 추출
//        String email = jwtService.extractUsername(token);
//        User user = userService.findByEmail(email);
//        return user.getUserId(); // 사용자 ID 반환
//    }
//}
