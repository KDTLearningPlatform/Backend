package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.Video;
import ac.su.learningplatform.dto.VideoProgressDTO;
import ac.su.learningplatform.domain.UserVideoProgress;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public VideoProgressDTO getVideoProgress(Long userId, Long videoId) {
        Optional<UserVideoProgress> userVideoProgressOpt = userVideoProgressRepository.findByUserIdAndVideoId(userId, videoId);
        if (!userVideoProgressOpt.isPresent()) {
            return null;
        }

        UserVideoProgress userVideoProgress = userVideoProgressOpt.get();
        Video video = userVideoProgress.getVideo();

        float videoProgress = ((float) userVideoProgress.getWatchTime() / video.getRunningTime()) * 100;

        // Calculate lecture progress
        List<UserVideoProgress> userVideoProgressList = userVideoProgressRepository.findByUserIdAndVideoLectureId(userId, video.getLecture().getId());
        int totalWatchTime = userVideoProgressList.stream().mapToInt(UserVideoProgress::getWatchTime).sum();

        int totalRunningTime = userVideoProgressList.stream().mapToInt(vp -> vp.getVideo().getRunningTime()).sum();

        float lectureProgress = ((float) totalWatchTime / totalRunningTime) * 100;

        VideoProgressDTO dto = new VideoProgressDTO();
        dto.setUserId(userId);
        dto.setVideoId(videoId);
        dto.setWatchTime(userVideoProgress.getWatchTime());
        dto.setProgress(userVideoProgress.getProgress());
        dto.setVideoProgress(videoProgress);
        dto.setLectureProgress(lectureProgress);

        return dto;
    }
}