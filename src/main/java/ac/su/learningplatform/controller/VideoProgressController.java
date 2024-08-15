package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.UserVideoProgressDTO;
import ac.su.learningplatform.dto.VideoProgressDTO.Response;
import ac.su.learningplatform.service.UserVideoProgressService;
import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.exception.UnauthorizedException;
import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/videos")
public class VideoProgressController {

    private final UserVideoProgressService userVideoProgressService;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public VideoProgressController(UserVideoProgressService userVideoProgressService, UserService userService, JwtService jwtService) {
        this.userVideoProgressService = userVideoProgressService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<Response> getVideoById(@PathVariable Long videoId, HttpSession session) {
        Long userId = authenticateUser(session);
        return userVideoProgressService.getVideoById(videoId, userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> updateVideoProgress(@RequestBody UserVideoProgressDTO request, HttpSession session) {
        Long userId = authenticateUser(session);
        userVideoProgressService.updateUserVideoProgress(userId, request.getVideoId(), request.getLastPlaybackPosition());
        return ResponseEntity.ok().build();
    }

    private Long authenticateUser(HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");

        if (token == null || token.isEmpty()) {
            throw new UnauthorizedException("Unauthorized access");
        }

        String email = jwtService.extractUsername(token);
        if (email == null) {
            throw new UnauthorizedException("Invalid token");
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        return user.getUserId();
    }
}
