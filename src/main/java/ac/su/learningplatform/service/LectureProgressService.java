package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.*;
import ac.su.learningplatform.dto.LectureProgressDTO;
import ac.su.learningplatform.repository.LectureRepository;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LectureProgressService {

    @Autowired
    private UserLectureProgressRepository userLectureProgressRepository;

    @Autowired
    private UserVideoProgressRepository userVideoProgressRepository;

    @Autowired
    private LectureRepository lectureRepository;

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
}