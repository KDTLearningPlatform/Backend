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

    // 비디오 시청 진행률을 업데이트하는 메소드
    public VideoProgressDTO updateVideoProgress(Long userId, Long videoId, int watchTime) {
        UserVideoProgress userVideoProgress = userVideoProgressRepository.findByUser_UserIdAndVideo_VideoId(userId, videoId)
                .orElse(new UserVideoProgress(userId, videoId));
        userVideoProgress.setWatchTime(watchTime);
        float progress = (float) watchTime / userVideoProgress.getVideo().getRunningTime() * 100;
        userVideoProgress.setProgress(progress);
        userVideoProgressRepository.save(userVideoProgress);

        VideoProgressDTO videoProgressDTO = new VideoProgressDTO();
        videoProgressDTO.setUserId(userId);
        videoProgressDTO.setLectureId(userVideoProgress.getVideo().getLecture().getLectureId());
        videoProgressDTO.setVideoId(videoId);
        videoProgressDTO.setTitle(userVideoProgress.getVideo().getTitle());
        videoProgressDTO.setWatchTime(watchTime);
        videoProgressDTO.setProgress(progress);

        return videoProgressDTO;
    }
}