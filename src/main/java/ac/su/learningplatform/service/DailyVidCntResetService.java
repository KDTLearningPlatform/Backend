package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DailyVidCntResetService {

    private final UserRepository userRepository;

    @Autowired
    public DailyVidCntResetService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul") // 매일 자정에 실행
    @Transactional
    public void resetDailyVidCnt() {
        List<User> users = userRepository.findAll();  // 모든 사용자를 가져옴

        for (User user : users) {
            user.setDailyVidCnt(0);  // daily_vid_cnt를 0으로 설정
        }

        userRepository.saveAll(users);  // 업데이트된 사용자 정보 저장
    }
}
