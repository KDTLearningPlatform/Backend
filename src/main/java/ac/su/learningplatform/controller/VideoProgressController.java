package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.VideoProgressDTO;
import ac.su.learningplatform.service.VideoProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video-progress")
public class VideoProgressController {

    @Autowired
    private VideoProgressService videoProgressService;

    @GetMapping("/{userId}}/{videoId}")
    public VideoProgressDTO getVideoProgress(@PathVariable Long userId, @PathVariable Long videoId) {
        return videoProgressService.getVideoProgress(userId, videoId);
    }

    @PostMapping("/{userId}/{videoId}")
    public VideoProgressDTO updateVideoProgress(@PathVariable Long userId, @PathVariable Long videoId, @RequestParam int watchTime) {
        return videoProgressService.updateVideoProgress(userId, videoId, watchTime);
    }
}
