package ac.su.learningplatform.service;

import ac.su.learningplatform.dto.VideoProgressDTO;
import ac.su.learningplatform.domain.UserVideoProgress;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoProgressService {

    @Autowired
    private UserVideoProgressRepository userVideoProgressRepository;

    public VideoProgressDTO updateVideoProgress(Long userId, Long videoId, int watchTime) {
        UserVideoProgress userVideoProgress = userVideoProgressRepository.findByUserIdAndVideoId(userId, videoId)
                .orElse(new UserVideoProgress(userId, videoId));
        userVideoProgress.setWatchTime(watchTime);
        float progress = (float) watchTime / userVideoProgress.getVideo().getRunningTime() * 100;
        userVideoProgress.setProgress(progress);
        userVideoProgressRepository.save(userVideoProgress);

        VideoProgressDTO videoProgressDTO = new VideoProgressDTO();
        videoProgressDTO.setVideoId(videoId);
        videoProgressDTO.setWatchTime(watchTime);
        videoProgressDTO.setProgress(progress);

        return videoProgressDTO;
    }
}