package ac.su.learningplatform.service;

import ac.su.learningplatform.Exception.LectureAlreadyDeletedException;
import ac.su.learningplatform.domain.Lecture;
import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.domain.Video;
import ac.su.learningplatform.dto.LectureCompletedDto;
import ac.su.learningplatform.repository.LectureRepository;
import ac.su.learningplatform.repository.UserRepository;
import ac.su.learningplatform.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {
    @Autowired
    private final LectureRepository lectureRepository;
    @Autowired
    private final VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;
    private static final int COMPLETION_ATTENDANCE_THRESHOLD = 10;

    @Transactional
    public List<LectureCompletedDto> getCompletedLectures(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Lecture> lectures = lectureRepository.findByUserAndAttendanceCountGreaterThan(user, COMPLETION_ATTENDANCE_THRESHOLD);

        return lectures.stream()
                .map(lecture -> new LectureCompletedDto(lecture.getId(), lecture.getTitle(), lecture.getThumbnail()))
                .collect(Collectors.toList());
    }

    public boolean deleteLecture(Long id) {
        Optional<Lecture> lectureOptional = lectureRepository.findById(id);
        if (lectureOptional.isPresent()) {
            Lecture lecture = lectureOptional.get();
            if (lecture.getDel() == 1) {
                throw new LectureAlreadyDeletedException("Lecture is already deleted");
            }
            lecture.setDel(1);
            lectureRepository.save(lecture);
            return true;
        }
        return false;
    }
}