package ac.su.learningplatform.service;

import ac.su.learningplatform.dto.LectureProgressDTO;
import ac.su.learningplatform.domain.UserLectureProgress;
import ac.su.learningplatform.domain.UserVideoProgress;
import ac.su.learningplatform.repository.LectureRepository;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLectureProgressService {

    @Autowired
    private UserLectureProgressRepository userLectureProgressRepository;

    @Autowired
    private UserVideoProgressRepository userVideoProgressRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    public UserLectureProgressService(UserLectureProgressRepository userLectureProgressRepository,
                                      UserVideoProgressRepository userVideoProgressRepository,
                                      LectureRepository lectureRepository) {
        this.userLectureProgressRepository = userLectureProgressRepository;
        this.userVideoProgressRepository = userVideoProgressRepository;
        this.lectureRepository = lectureRepository;
    }



    public LectureProgressDTO calculateLectureProgress(Long userId, Long lectureId) {
        List<UserVideoProgress> videoProgressList = userVideoProgressRepository.findByUserIdAndLectureId(userId, lectureId);
        int totalWatchTime = videoProgressList.stream().mapToInt(UserVideoProgress::getWatchTime).sum();
        int totalRunningTime = videoProgressList.stream().mapToInt(v -> v.getVideo().getRunningTime()).sum();
        float progress = (float) totalWatchTime / totalRunningTime * 100;

        UserLectureProgress userLectureProgress = (UserLectureProgress) userLectureProgressRepository.findByUserIdAndLectureId(userId, lectureId)
                .orElse(new UserLectureProgress(userId, lectureId));
        userLectureProgress.setProgress(progress);
        userLectureProgressRepository.save(userLectureProgress);

        LectureProgressDTO lectureProgressDTO = new LectureProgressDTO();
        lectureProgressDTO.setLectureId(lectureId);
        lectureProgressDTO.setProgress(progress);

        return lectureProgressDTO;
    }
}