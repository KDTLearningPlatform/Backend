package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.*;
import ac.su.learningplatform.dto.VideoProgressDTO.Response;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import ac.su.learningplatform.repository.UserRepository;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import ac.su.learningplatform.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserVideoProgressService {
    private final VideoRepository videoRepository;
    private final UserVideoProgressRepository userVideoProgressRepository;
    private final UserLectureProgressRepository userLectureProgressRepository;
    private final UserRepository userRepository;
    private final UserRankingService userRankingService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String RANKING_CACHE_KEY = "userRankingCache";

    @Autowired
    public UserVideoProgressService(VideoRepository videoRepository, UserVideoProgressRepository userVideoProgressRepository, UserLectureProgressRepository userLectureProgressRepository, UserRepository userRepository, UserRankingService userRankingService, RedisTemplate<String, Object> redisTemplate) {
        this.videoRepository = videoRepository;
        this.userVideoProgressRepository = userVideoProgressRepository;
        this.userLectureProgressRepository = userLectureProgressRepository;
        this.userRepository = userRepository;
        this.userRankingService = userRankingService;
        this.redisTemplate = redisTemplate;
    }

    public Optional<Response> getVideoById(Long videoId, Long userId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            Response videoResponse = new Response();
            videoResponse.setVideoId(video.getVideoId());
            videoResponse.setVideoOrder(video.getVideoOrder());
            videoResponse.setTitle(video.getTitle());
            videoResponse.setContent(video.getContent());
            videoResponse.setRunningTime(video.getRunningTime());

            // 사용자의 last_playback_position을 조회하여 응답에 추가
            UserVideoProgressId id = new UserVideoProgressId(userId, videoId);
            Optional<UserVideoProgress> progressOptional = userVideoProgressRepository.findById(id);
            progressOptional.ifPresent(progress -> videoResponse.setLastPlaybackPosition(progress.getLastPlaybackPosition()));

            return Optional.of(videoResponse);
        } else {
            return Optional.empty();
        }
    }

    public void updateUserVideoProgress(Long userId, Long videoId, int lastPlaybackPosition) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));
        Lecture lecture = video.getLecture();

        UserVideoProgressId id = new UserVideoProgressId(userId, videoId);
        Optional<UserVideoProgress> userVideoProgressOpt = userVideoProgressRepository.findById(id);

        float progress = (float) lastPlaybackPosition / video.getRunningTime();

        UserVideoProgress userVideoProgress;
        boolean isFirstCompletion = false;

        if (userVideoProgressOpt.isPresent()) {
            userVideoProgress = userVideoProgressOpt.get();

            // 진행도가 1(100%)이면 업데이트하지 않음
            if (userVideoProgress.getProgress() < 1.0f && progress == 1.0f) {
                isFirstCompletion = true;
            }
            userVideoProgress.setLastPlaybackPosition(lastPlaybackPosition);
            userVideoProgress.setProgress(progress);
            userVideoProgressRepository.save(userVideoProgress);
        } else {
            userVideoProgress = new UserVideoProgress();
            userVideoProgress.setId(id);
            userVideoProgress.setUser(user);
            userVideoProgress.setVideo(video);
            userVideoProgress.setLastPlaybackPosition(lastPlaybackPosition);
            userVideoProgress.setProgress(progress);
            userVideoProgressRepository.save(userVideoProgress);

            if (progress == 1.0f) {
                isFirstCompletion = true;
            }
        }

        // 최초로 비디오를 완료한 경우에만 카운트 증가
        if (isFirstCompletion) {
            incrementDailyVidCntAndTotalPoints(user, video.getRunningTime() * 10);
        }

        // 강의 진행도 업데이트
        if (progress == 1.0f) {
            updateUserLectureProgress(user, lecture, isFirstCompletion);
        }
    }

    private void incrementDailyVidCntAndTotalPoints(User user, int pointsToAdd) {
        user.setDailyVidCnt(user.getDailyVidCnt() + 1);
        user.setTotalPoint(user.getTotalPoint() + pointsToAdd);
        user.setUpdateDate(LocalDateTime.now());

        userRepository.save(user);

        updateRankingCache();
    }

    private void incrementTotalPoints(User user, int pointsToAdd) {
        user.setTotalPoint(user.getTotalPoint() + pointsToAdd);
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        updateRankingCache();
    }

    private void updateRankingCache() {
        // 기존 캐시 무효화
        redisTemplate.delete(RANKING_CACHE_KEY);

        // 새로운 데이터를 캐시 갱신
        userRankingService.fetchAndCacheUserRanking();
    }

    private void updateUserLectureProgress(User user, Lecture lecture, boolean isVideoCompletion) {
        UserLectureProgressId id = new UserLectureProgressId(user.getUserId(), lecture.getLectureId());
        UserLectureProgress userLectureProgress = userLectureProgressRepository.findById(id)
                .orElse(new UserLectureProgress());
        userLectureProgress.setId(id);
        userLectureProgress.setUser(user);
        userLectureProgress.setLecture(lecture);

        // 시청 완료된 비디오 수를 계산
        int watchedCount = userVideoProgressRepository.countByUserAndVideo_LectureAndProgress(user, lecture, 1.0f);
        int totalVideos = videoRepository.countByLectureAndDeleteDateIsNull(lecture);

        // 진행도를 계산하여 업데이트
        float lectureProgress = (float) watchedCount / totalVideos;
        userLectureProgress.setWatchedCount(watchedCount);
        userLectureProgress.setProgress(lectureProgress);

        // 강의가 완료된 경우 포인트 추가
        if (lectureProgress == 1.0f && isVideoCompletion) {
            incrementTotalPoints(user, totalVideos * 10);
        }

        userLectureProgressRepository.save(userLectureProgress);
    }
}
