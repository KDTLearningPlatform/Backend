package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.*;
import ac.su.learningplatform.dto.UserLectureDTO;
import ac.su.learningplatform.dto.UserLectureRegisterDTO;
import ac.su.learningplatform.repository.LectureRepository;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ac.su.learningplatform.repository.VideoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLectureProgressService {

    private final UserLectureProgressRepository userLectureProgressRepository;
    private final VideoRepository videoRepository;
    private final LectureRepository lectureRepository;
    private final UserVideoProgressRepository userVideoProgressRepository;

    @Autowired
    public UserLectureProgressService(UserLectureProgressRepository userLectureProgressRepository, VideoRepository videoRepository, LectureRepository lectureRepository, UserVideoProgressRepository userVideoProgressRepository) {
        this.userLectureProgressRepository = userLectureProgressRepository;
        this.videoRepository = videoRepository;
        this.lectureRepository = lectureRepository;
        this.userVideoProgressRepository = userVideoProgressRepository;
    }

    public UserLectureProgress registerLecture(UserLectureRegisterDTO dto) {
        // User와 Lecture 객체를 생성하여 연결
        User user = new User();
        user.setUserId(dto.getUserId());

        Lecture lecture = new Lecture();
        lecture.setLectureId(dto.getLectureId());

        // ID 설정 및 UserLectureProgress 객체 생성
        UserLectureProgressId id = new UserLectureProgressId(dto.getUserId(), dto.getLectureId());
        UserLectureProgress userLectureProgress = new UserLectureProgress();
        userLectureProgress.setId(id);
        userLectureProgress.setUser(user);
        userLectureProgress.setLecture(lecture);
        userLectureProgress.setProgress(0);
        userLectureProgress.setWatchedCount(0);

        // 수강생 인원 증가
        Lecture lectureEntity = lectureRepository.findById(dto.getLectureId()).orElseThrow(() -> new RuntimeException("Lecture not found"));
        lectureEntity.setAttendanceCount(lectureEntity.getAttendanceCount() + 1);
        lectureRepository.save(lectureEntity);

        // 해당 강의의 모든 비디오에 대해 UserVideoProgress 생성
        List<Video> activeVideos = videoRepository.findByLectureAndDeleteDateIsNull(lectureEntity);
        for (Video video : activeVideos) {
            UserVideoProgressId videoProgressId = new UserVideoProgressId(user.getUserId(), video.getVideoId());
            UserVideoProgress userVideoProgress = new UserVideoProgress();
            userVideoProgress.setId(videoProgressId);
            userVideoProgress.setUser(user);
            userVideoProgress.setVideo(video);
            userVideoProgress.setLastPlaybackPosition(0);
            userVideoProgress.setProgress(0);
            userVideoProgressRepository.save(userVideoProgress);
        }

        return userLectureProgressRepository.save(userLectureProgress);
    }

    public boolean isLectureRegistered(Long userId, Long lectureId) {
        return userLectureProgressRepository.existsById(new UserLectureProgressId(userId, lectureId));
    }

    public void unregisterLecture(Long userId, Long lectureId) {
        // 수강생 인원 감소
        Lecture lectureEntity = lectureRepository.findById(lectureId).orElseThrow(() -> new RuntimeException("Lecture not found"));
        lectureEntity.setAttendanceCount(lectureEntity.getAttendanceCount() - 1);
        lectureRepository.save(lectureEntity);

        // 해당 강의의 모든 비디오에 대한 UserVideoProgress 삭제
        List<UserVideoProgress> videoProgressList = userVideoProgressRepository.findByUserUserIdAndVideoLectureLectureId(userId, lectureId);
        userVideoProgressRepository.deleteAll(videoProgressList);

        userLectureProgressRepository.deleteById(new UserLectureProgressId(userId, lectureId));
    }

    // 진행 중인 강의 목록 조회
    public List<UserLectureDTO> getInProgressLectures(Long userId) {
        List<UserLectureProgress> progressList = userLectureProgressRepository.findByUserUserIdAndProgressLessThan(userId, 1.0f);
        return progressList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 완료된 강의 목록 조회
    public List<UserLectureDTO> getCompletedLectures(Long userId) {
        List<UserLectureProgress> progressList = userLectureProgressRepository.findByUserUserIdAndProgressEquals(userId, 1.0f);
        return progressList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // UserLectureProgress -> UserLectureDTO 변환
    private UserLectureDTO convertToDTO(UserLectureProgress userLectureProgress) {
        Lecture lecture = userLectureProgress.getLecture();

        // 삭제되지 않은 비디오만 필터링
        List<Video> activeVideos = videoRepository.findByLectureAndDeleteDateIsNull(lecture);

        // 비디오들의 총 러닝타임 계산
        int totalRunningTime = activeVideos.stream()
                .mapToInt(Video::getRunningTime)
                .sum();

        int totalVideoCount = activeVideos.size();
        int watchedCount = userLectureProgress.getWatchedCount();

        return new UserLectureDTO(
                lecture.getLectureId(),
                lecture.getTag(),
                lecture.getTitle(),
                totalRunningTime,
                watchedCount,
                totalVideoCount
        );
    }
}
