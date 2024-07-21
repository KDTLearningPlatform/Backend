package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.*;
import ac.su.learningplatform.dto.LectureProgressDTO;
import ac.su.learningplatform.repository.LectureRepository;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import ac.su.learningplatform.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
                                  LectureRepository lectureRepository) {
        this.userLectureProgressRepository = userLectureProgressRepository;
        this.userVideoProgressRepository = userVideoProgressRepository;
        this.lectureRepository = lectureRepository;
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

    // 진행중인 강의 목록을 반환하는 메소드
    public List<LectureProgressDTO> getInProgressLectures(Long userId) {
        List<Lecture> lectures = lectureRepository.findAll();
        return lectures.stream().map(lecture -> {
            List<UserVideoProgress> progressList = userVideoProgressRepository.findByUser_UserIdAndVideo_Lecture_LectureId(userId, lecture.getLectureId());
            int watchedVideos = progressList.size();
            int totalVideos = videoRepository.findByLecture(lecture).size();
            String totalDuration = lecture.getTotalDuration(); // 강의 총 시간을 계산하는 로직 필요

            return new LectureProgressDTO(lecture.getTag(), lecture.getTitle(), totalDuration, watchedVideos, totalVideos);
        }).collect(Collectors.toList());
    }

}