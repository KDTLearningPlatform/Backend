package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.UserLectureProgressId;
import ac.su.learningplatform.dto.LectureProgressDTO;
import ac.su.learningplatform.domain.UserLectureProgress;
import ac.su.learningplatform.domain.UserVideoProgress;
import ac.su.learningplatform.dto.UserLectureProgressDTO;
import ac.su.learningplatform.repository.LectureRepository;
import ac.su.learningplatform.repository.UserLectureProgressRepository;
import ac.su.learningplatform.repository.UserVideoProgressRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLectureProgressService {
    @Autowired
    private UserLectureProgressRepository repository;

    @Transactional
    public void updateProgress(Long userId, Long lectureId, UserLectureProgressDTO dto) {
        UserLectureProgressId id = new UserLectureProgressId(userId, lectureId);
        UserLectureProgress progress = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserLectureProgress not found for the given id."));

        progress.setProgress(dto.getProgress());
        progress.setWatchedCount(dto.isCompleted() ? progress.getLecture().getVideos().size() : 0);

        repository.save(progress);
    }


}