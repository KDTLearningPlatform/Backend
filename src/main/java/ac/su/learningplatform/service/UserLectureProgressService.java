package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.*;
import ac.su.learningplatform.dto.UserLectureDTO;
import ac.su.learningplatform.dto.UserLectureRegisterDTO;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import ac.su.learningplatform.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLectureProgressService {

    private final UserLectureProgressRepository userLectureProgressRepository;
    //08.14추가
    private final VideoRepository videoRepository;
    private final UserVideoProgressRepository userVideoProgressRepository;


    @Autowired
    public UserLectureProgressService(UserLectureProgressRepository userLectureProgressRepository,
                                      VideoRepository videoRepository,
                                      UserVideoProgressRepository userVideoProgressRepository) {
        this.userLectureProgressRepository = userLectureProgressRepository;
        this.videoRepository = videoRepository;
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

        //08.14추가
        // 강의 등록 시 해당 강의의 모든 비디오에 대한 진행률 초기화
        List<Video> videos = videoRepository.findByLecture_LectureId(dto.getLectureId());
        for (Video video : videos) {
            UserVideoProgress userVideoProgress = new UserVideoProgress(user.getUserId(), video.getVideoId());
            userVideoProgress.setUser(user);
            userVideoProgress.setVideo(video);
            userVideoProgress.setProgress(0);
            userVideoProgress.setWatchTime(0);
            userVideoProgressRepository.save(userVideoProgress);
        }

        return userLectureProgressRepository.save(userLectureProgress);
    }

    public boolean isLectureRegistered(Long userId, Long lectureId) {
        return userLectureProgressRepository.existsById(new UserLectureProgressId(userId, lectureId));
    }

    public void unregisterLecture(Long userId, Long lectureId) {
        // 강의에 속한 모든 비디오에 대한 UserVideoProgress 기록 삭제, 08.14추가
        List<Video> videos = videoRepository.findByLecture_LectureId(lectureId);
        for (Video video : videos) {
            UserVideoProgressId id = new UserVideoProgressId(userId, video.getVideoId());
            userVideoProgressRepository.deleteById(id);
        }

        // 강의에 대한 UserLectureProgress 기록 삭제
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

        // 비디오들의 총 러닝타임 계산
        int totalRunningTime = lecture.getVideos().stream()
                .mapToInt(Video::getRunningTime)
                .sum();

        return new UserLectureDTO(
                lecture.getLectureId(),
                lecture.getTag(),
                lecture.getTitle(),
                totalRunningTime
        );
    }
}
