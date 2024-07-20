package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.*;
import ac.su.learningplatform.dto.VideoProgressDTO;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import ac.su.learningplatform.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideoProgressService {

    @Autowired
    private UserVideoProgressRepository userVideoProgressRepository;

    @Autowired
    private UserLectureProgressRepository userLectureProgressRepository

    @Autowired
    private VideoRepository videoRepository;

    public VideoProgressDTO getVideoProgress(Long userId, Long videoId) {
        Optional<UserVideoProgress> userVideoProgressOpt = userVideoProgressRepository.findByUserIdAndVideoId(userId, videoId);
        if (!userVideoProgressOpt.isPresent()) {
            return null;
        }

        UserVideoProgress userVideoProgress = userVideoProgressOpt.get();
        Video video = userVideoProgress.getVideo();

        float videoProgress = ((float) userVideoProgress.getLastPlaybackPosition() / video.getRunningTime()) * 100;

        // Calculate lecture progress
        List<UserVideoProgress> userVideoProgressList = userVideoProgressRepository.findByUserIdAndVideo_LectureId(userId, video.getLecture().getLectureId());
        int totalWatchTime = userVideoProgressList.stream().mapToInt(UserVideoProgress::getLastPlaybackPosition).sum();

        int totalRunningTime = userVideoProgressList.stream().mapToInt(vp -> vp.getVideo().getRunningTime()).sum();

        float lectureProgress = ((float) totalWatchTime / totalRunningTime) * 100;

        int watchCount = (int) userVideoProgressList.stream().filter(vp -> vp.getLastPlaybackPosition() > 0).count();

        //Update UserLectureProgress
        UserLectureProgressId userLectureProgressId = new UserLectureProgressId(userId, video.getLecture().getLectureId());
        UserLectureProgress userLectureProgress = userLectureProgressRepository.findById(userLectureProgressId)
                .orElse(new UserLectureProgress(userLectureProgressId, new User(), video.getLecture(), 0, 0));
        userLectureProgress.setProgress(lectureProgress);
        userLectureProgress.setWatchedCount(watchedCount);
        userLectureProgressRepository.save(userLectureProgress);

        VideoProgressDTO dto = new VideoProgressDTO();
        dto.setUserId(userId);
        dto.setVideoId(videoId);
        dto.setWatchTime(userVideoProgress.getLastPlaybackPosition());
        dto.setVideoProgress(videoProgress);
        dto.setLectureProgress(lectureProgress);
        return dto;
    }

    public VideoProgressDTO updateVideoProgress(Long userId, Long videoId, int watchTime) {
        UserVideoProgress userVideoProgress = userVideoProgressRepository.findByUserIdAndVideoId(userId, videoId)
                .orElse(new UserVideoProgress(new UserVideoProgressRepository(new UserVideoProgressId(userId, videoId), new User(), new Video(), 0, 0));

        userVideoProgress.setLastPlaybackPosition(watchTime);
        float progress = (float) watchTime / userVideoProgress.getVideo().getRunningTime() * 100;
        userVideoProgress.setProgress(progress);
        userVideoProgressRepository.save(userVideoProgress);

        // Update lecture progress
        Video video = userVideoProgress.getVideo();
        List<UserVideoProgress> userVideoProgressList = userVideoProgressRepository.findByUserIdAndVideo_LectureId(userId, video.getLecture().getVideoId());
        int totalWatchTime = userVideoProgressList.stream().mapToInt(UserVideoProgress::getLastPlaybackPosition).sum();
        int totalRunningTime = userVideoProgressList.stream().mapToInt(vp -> vp.getVideo().getRunningTime()).sum();
        float lectureProgress = ((float) totalWatchTime / totalRunningTime) * 100;
        int watchedCount = (int) userVideoProgressList.stream().filter(vp -> vp.getLastPlaybackPosition() > 0).count();

        UserLectureProgressId userLectureProgressId = new UserLectureProgressId(userId, video.getLecture().getLId());
        UserLectureProgress userLectureProgress = userLectureProgressRepository.findById(userLectureProgressId)
                .orElse(new UserLectureProgress(userLectureProgressId, new User(), video.getLecture(), 0, 0));
        userLectureProgress.setProgress(lectureProgress);
        userLectureProgress.setWatchedCount(watchedCount);
        userLectureProgressRepository.save(userLectureProgress);

        VideoProgressDTO videoProgressDTO = new VideoProgressDTO();
        videoProgressDTO.setUserId(userId);
        videoProgressDTO.setVideoId(videoId);
        videoProgressDTO.setWatchTime(watchTime);
        videoProgressDTO.setVideoProgress(progress);
        videoProgressDTO.setLectureProgress(lectureProgress);

        return videoProgressDTO;
    }
}