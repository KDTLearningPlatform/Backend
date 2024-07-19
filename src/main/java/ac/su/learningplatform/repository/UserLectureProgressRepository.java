package ac.su.learningplatform.repository;
import ac.su.learningplatform.domain.UserLectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLectureProgressRepository extends JpaRepository<UserLectureProgress, Long> {
    /**
     * 특정 사용자의 모든 강의 진도율 목록을 조회
     * @param userId 사용자 ID
     * @return 특정 사용자의 강의 진도율 목록
     */
    List<UserLectureProgress> findByUserUserId(Long userId);

    /**
     * 특정 강의의 모든 사용자 진도율 목록을 조회
     * @param lectureId 강의 ID
     * @return 특정 강의의 사용자 진도율 목록
     */
    List<UserLectureProgress> findByLectureLectureId(Long lectureId);

    /**
     * 특정 사용자의 특정 강의에 대한 진도율을 조회
     * @param userId 사용자 ID
     * @param lectureId 강의 ID
     * @return 특정 사용자의 특정 강의에 대한 진도율
     */
    Optional<UserLectureProgress> findByUserUserIdAndLectureLectureId(Long userId, Long lectureId);

    org.apache.el.stream.Optional findByUserIdAndLectureId(Long userId, Long lectureId);
}
