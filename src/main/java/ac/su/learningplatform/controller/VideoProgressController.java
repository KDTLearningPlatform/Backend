package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.VideoProgressDTO;
import ac.su.learningplatform.service.VideoProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videoProgress")
public class VideoProgressController {

    @Autowired
    private VideoProgressService videoProgressService;

    // 비디오 시청 진행률을 업데이트하는 엔드포인트
    @GetMapping("/{userId}/{videoId}")
    public VideoProgressDTO updateVideoProgress(@PathVariable Long userId, @PathVariable Long videoId, @RequestParam int watchTime) {
        return videoProgressService.updateVideoProgress(userId, videoId, watchTime);
    }
}
