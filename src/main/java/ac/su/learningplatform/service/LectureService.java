package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.Lecture;
import ac.su.learningplatform.domain.Video;
import ac.su.learningplatform.dto.LectureDetailsDTO;
import ac.su.learningplatform.dto.LectureListDTO;
import ac.su.learningplatform.repository.LectureRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public List<LectureListDTO> getAllLectures() {
        return lectureRepository.findAll().stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }

    public LectureDetailsDTO getLectureById(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .map(this::convertToDetailsDTO)
                .orElse(null);
    }

    // Lecture -> LectureListDTO 변환
    private LectureListDTO convertToListDTO(Lecture lecture) {
        LectureListDTO lectureDTO = new LectureListDTO();
        lectureDTO.setId(lecture.getLectureId());
        lectureDTO.setTitle(lecture.getTitle());
        lectureDTO.setAttendanceCount(lecture.getAttendanceCount());
        lectureDTO.setThumbnail(lecture.getThumbnail());
        lectureDTO.setUserId(lecture.getUser().getUserId());
        return lectureDTO;
    }

    // Lecture -> LectureDetailsDTO 변환
    private LectureDetailsDTO convertToDetailsDTO(Lecture lecture) {
        LectureDetailsDTO lectureDTO = new LectureDetailsDTO();
        lectureDTO.setId(lecture.getLectureId());
        lectureDTO.setTitle(lecture.getTitle());
        lectureDTO.setAttendanceCount(lecture.getAttendanceCount());
        lectureDTO.setThumbnail(lecture.getThumbnail());
        lectureDTO.setUserId(lecture.getUser().getUserId());

        // 총 비디오 개수
        int totalVideoCount = lecture.getVideos() != null ? lecture.getVideos().size() : 0;
        lectureDTO.setTotalVideoCount(totalVideoCount);

        // 총 실행 시간 계산
        Duration totalRunningTime = lecture.getVideos() != null ? lecture.getVideos().stream()
                .map(video -> Duration.ofHours(video.getRunningTime().getHour())
                        .plusMinutes(video.getRunningTime().getMinute())
                        .plusSeconds(video.getRunningTime().getSecond()))
                .reduce(Duration.ZERO, Duration::plus) : Duration.ZERO;

        // Duration을 LocalTime으로 변환
        LocalTime totalRunningTimeAsLocalTime = LocalTime.ofSecondOfDay(totalRunningTime.getSeconds());

        lectureDTO.setTotalRunningTime(totalRunningTimeAsLocalTime);

        return lectureDTO;
    }
}
