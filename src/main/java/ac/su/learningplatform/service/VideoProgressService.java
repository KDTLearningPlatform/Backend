package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.UserVideoProgressId;
import ac.su.learningplatform.domain.Video;
import ac.su.learningplatform.dto.VideoDetailDTO;
import ac.su.learningplatform.dto.VideoProgressDTO;
import ac.su.learningplatform.domain.UserVideoProgress;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import ac.su.learningplatform.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoProgressService {

    private final VideoRepository videoRepository;
    private final UserVideoProgressRepository userVideoProgressRepository;

    @Autowired
    public VideoProgressService(UserVideoProgressRepository userVideoProgressRepository,
                                VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
        this.userVideoProgressRepository = userVideoProgressRepository;
    }

    // // 사용자의 동영상 시청 진행률을 업데이트하는 메서드
    public VideoProgressDTO updateVideoProgress(Long userId, Long videoId, int watchTime) {
        UserVideoProgress userVideoProgress = userVideoProgressRepository
                .findByUser_UserIdAndVideo_VideoId(userId, videoId)
                .orElse(new UserVideoProgress(userId, videoId));
        // 시청 시간 업데이트
        userVideoProgress.setWatchTime(watchTime);
        // 시청 진행률을 계산: 시청 시간 / 동영상의 총 길이
        float progress = (float) watchTime / userVideoProgress.getVideo().getRunningTime();
        userVideoProgress.setProgress(progress);
        // 업데이트된 진행 상태를 데이터베이스에 저장
        userVideoProgressRepository.save(userVideoProgress);
        // DTO 객체를 생성하고 데이터를 설정
        VideoProgressDTO videoProgressDTO = new VideoProgressDTO();
        videoProgressDTO.setUserId(userId);
        videoProgressDTO.setLectureId(userVideoProgress.getVideo().getLecture().getLectureId());
        videoProgressDTO.setVideoId(videoId);
        videoProgressDTO.setTitle(userVideoProgress.getVideo().getTitle());
        videoProgressDTO.setWatchTime(watchTime);
        videoProgressDTO.setProgress(progress);

        return videoProgressDTO;
    }

    //08.14추가
    public List<VideoDetailDTO> getLectureVideos(Long userId, Long lectureId) {
        List<Video> videos = videoRepository.findByLecture_LectureId(lectureId);
        return videos.stream()
                .map(video -> {
                    UserVideoProgressId id = new UserVideoProgressId(userId, video.getVideoId());
                    UserVideoProgress userVideoProgress = userVideoProgressRepository.findById(id).orElse(null);

                    int watchedDuration = (userVideoProgress != null) ? userVideoProgress.getWatchTime() : 0;
                    float progress = (userVideoProgress != null) ? userVideoProgress.getProgress() : 0;

                    return new VideoDetailDTO(
                            video.getVideoId(),
                            video.getTitle(),
                            video.getRunningTime(),
                            watchedDuration,
                            progress
                    );
                })
                .collect(Collectors.toList());
    }
}