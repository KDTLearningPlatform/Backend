package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.*;
import ac.su.learningplatform.dto.LectureCompletedDTO;
import ac.su.learningplatform.dto.LectureProgressDTO;
import ac.su.learningplatform.repository.LectureRepository;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import ac.su.learningplatform.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureProgressService {

    @Autowired
    private UserLectureProgressRepository userLectureProgressRepository;

    @Autowired
    private UserVideoProgressRepository userVideoProgressRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    public LectureProgressService(UserLectureProgressRepository userLectureProgressRepository,
                                  UserVideoProgressRepository userVideoProgressRepository,
                                  LectureRepository lectureRepository,
                                  VideoRepository videoRepository) {
        this.userLectureProgressRepository = userLectureProgressRepository;
        this.userVideoProgressRepository = userVideoProgressRepository;
        this.lectureRepository = lectureRepository;
        this.videoRepository = videoRepository;
    }


    public LectureProgressDTO calculateLectureProgress(Long userId, Long lectureId) {
        List<UserVideoProgress> videoProgressList = userVideoProgressRepository.findByUser_UserIdAndVideo_Lecture_LectureId(userId, lectureId);
        int totalWatchTime = videoProgressList.stream().mapToInt(UserVideoProgress::getLastPlaybackPosition).sum();
        int totalRunningTime = videoProgressList.stream().mapToInt(v -> v.getVideo().getRunningTime()).sum();
        float progress = (float) totalWatchTime / totalRunningTime * 100;

        UserLectureProgress userLectureProgress = userLectureProgressRepository.findById_UserIdAndId_LectureId(userId, lectureId)
                .orElse(new UserLectureProgress(new UserLectureProgressId(userId, lectureId), new User(), new Lecture(), 0, 0));
        userLectureProgress.setProgress(progress);
        userLectureProgressRepository.save(userLectureProgress);

        LectureProgressDTO lectureProgressDTO = new LectureProgressDTO();
        lectureProgressDTO.setUserId(userId);
        lectureProgressDTO.setLectureId(lectureId);
        lectureProgressDTO.setProgress(progress);

        return lectureProgressDTO;
    }

    // 진행중인 강의 목록을 반환하는 메소드, 완
    public List<LectureProgressDTO> getInProgressLectures(Long userId) {
        // 사용자가 진행중인 강의를 조회
        List<UserLectureProgress> inProgressLectures = userLectureProgressRepository.findByUser_UserIdAndProgressLessThan(userId, 100);
        return inProgressLectures.stream().map(userLectureProgress -> {
            Lecture lecture = userLectureProgress.getLecture();
            List<UserVideoProgress> progressList = userVideoProgressRepository.findByUser_UserIdAndVideo_Lecture_LectureId(userId, lecture.getLectureId());
            int watchedVideos = progressList.size();
            int totalVideos = videoRepository.findByLecture(lecture).size();
            String totalDuration = lecture.getTotalDuration(); // 강의 총 시간을 계산하는 로직 필요

            return new LectureProgressDTO(
                    lecture.getLectureId(),
                    userId,
                    lecture.getTag(),
                    lecture.getTitle(),
                    totalDuration,
                    watchedVideos,
                    totalVideos
            );
        }).collect(Collectors.toList());
    }

    // 완료된 강의 목록을 반환하는 메소드
    public List<LectureCompletedDTO> getCompletedLectures(Long userId) {
        List<UserLectureProgress> completedLectures = userLectureProgressRepository.findByUser_UserIdAndProgressGreaterThanEqual(userId, 100);
        return completedLectures.stream().map(userLectureProgress -> {
            Lecture lecture = userLectureProgress.getLecture();
            // 강의에 포함된 비디오들의 총 시간을 계산
            int totalDuration = videoRepository.findByLecture(lecture).stream()
                    .mapToInt(Video::getRunningTime)
                    .sum();

            return LectureCompletedDTO.builder()
                    .lectureId(lecture.getLectureId())
                    .title(lecture.getTitle())
                    .tag(lecture.getTag())
                    .totalDuration(totalDuration)
                    .build();
        }).collect(Collectors.toList());
    }

}