package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.Love;
import ac.su.learningplatform.domain.Study;
import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.repository.LoveRepository;
import ac.su.learningplatform.repository.StudyRepository;
import ac.su.learningplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoveService {

    private final LoveRepository loveRepository;
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;

    public LoveService(LoveRepository loveRepository, UserRepository userRepository, StudyRepository studyRepository) {
        this.loveRepository = loveRepository;
        this.userRepository = userRepository;
        this.studyRepository = studyRepository;
    }

    @Transactional
    public boolean toggleLove(Long studyId, Long userId) {
        // studyId와 userId를 기반으로 Love 엔티티를 조회
        Love existingLove = loveRepository.findByUser_UserIdAndStudy_StudyId(userId, studyId);

        if (existingLove != null) {
            // 이미 좋아요가 존재하면 삭제
            loveRepository.delete(existingLove);
            return false; // 좋아요 삭제됨
        } else {
            // 좋아요가 존재하지 않으면 추가
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
            Study study = studyRepository.findById(studyId).orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

            Love newLove = new Love();
            newLove.setUser(user);
            newLove.setStudy(study);

            loveRepository.save(newLove);
            return true; // 좋아요 추가됨
        }
    }


}
