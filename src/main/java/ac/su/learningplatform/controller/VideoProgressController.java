package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.VideoDTO.Response;
import ac.su.learningplatform.service.UserVideoProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
public class VideoProgressController {

    private final UserVideoProgressService userVideoProgressService;

    @Autowired
    public VideoProgressController(UserVideoProgressService userVideoProgressService) {
        this.userVideoProgressService = userVideoProgressService;
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<Response> getVideoById(@PathVariable Long videoId) {
        return userVideoProgressService.getVideoById(videoId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
