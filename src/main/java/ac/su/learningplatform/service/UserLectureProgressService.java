package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.*;
import ac.su.learningplatform.dto.UserLectureDTO;
import ac.su.learningplatform.dto.UserLectureRegisterDTO;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ac.su.learningplatform.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLectureProgressService {

    private final UserLectureProgressRepository userLectureProgressRepository;
    private final VideoRepository videoRepository;

    @Autowired
    public UserLectureProgressService(UserLectureProgressRepository userLectureProgressRepository, VideoRepository videoRepository) {
        this.userLectureProgressRepository = userLectureProgressRepository;
        this.videoRepository = videoRepository;
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

        return userLectureProgressRepository.save(userLectureProgress);
    }

    public boolean isLectureRegistered(Long userId, Long lectureId) {
        return userLectureProgressRepository.existsById(new UserLectureProgressId(userId, lectureId));
    }

    public void unregisterLecture(Long userId, Long lectureId) {
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
