package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.VideoDetailDTO;
import ac.su.learningplatform.dto.VideoProgressDTO;
import ac.su.learningplatform.service.VideoProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videoProgress")
public class VideoProgressController {

    @Autowired
    private VideoProgressService videoProgressService;

    // 비디오 시청 진행률을 업데이트하는 엔드포인트, api테스터시 로그인 화면나옴
    @PutMapping("/{userId}/{videoId}")
    public VideoProgressDTO updateVideoProgress(@PathVariable Long userId, @PathVariable Long videoId, @RequestParam int watchTime) {
        return videoProgressService.updateVideoProgress(userId, videoId, watchTime);
    }

    // 강의의 비디오 목록을 반환하는 엔드포인트
    @GetMapping("/{userId}/lecture/{lectureId}/videos")
    public List<VideoDetailDTO> getLectureVideos(@PathVariable Long userId, @PathVariable Long lectureId) {
        return videoProgressService.getLectureVideos(userId, lectureId);
    }
}
