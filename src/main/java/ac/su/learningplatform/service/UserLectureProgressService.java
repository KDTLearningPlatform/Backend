package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.*;
import ac.su.learningplatform.dto.LectureSummaryDTO;
import ac.su.learningplatform.dto.UserLectureProgressDTO;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLectureProgressService {

    private final UserLectureProgressRepository userLectureProgressRepository;

    @Autowired
    public UserLectureProgressService(UserLectureProgressRepository userLectureProgressRepository) {
        this.userLectureProgressRepository = userLectureProgressRepository;
    }

    public UserLectureProgress registerLecture(UserLectureProgressDTO dto) {
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
    public List<LectureSummaryDTO> getInProgressLectures(Long userId) {
        List<UserLectureProgress> progressList = userLectureProgressRepository.findByUserUserIdAndProgressLessThan(userId, 1.0f);
        return progressList.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    // 완료된 강의 목록 조회
    public List<LectureSummaryDTO> getCompletedLectures(Long userId) {
        List<UserLectureProgress> progressList = userLectureProgressRepository.findByUserUserIdAndProgressEquals(userId, 1.0f);
        return progressList.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    // UserLectureProgress -> LectureSummaryDTO 변환
    private LectureSummaryDTO convertToSummaryDTO(UserLectureProgress userLectureProgress) {
        Lecture lecture = userLectureProgress.getLecture();

        // 비디오들의 총 러닝타임 계산
        int totalRunningTime = lecture.getVideos().stream()
                .mapToInt(Video::getRunningTime)
                .sum();

        return new LectureSummaryDTO(
                lecture.getLectureId(),
                lecture.getTag(),
                lecture.getTitle(),
                totalRunningTime
        );
    }
}
