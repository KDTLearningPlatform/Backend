package ac.su.learningplatform.service;

import ac.su.learningplatform.constant.DeleteStatus;
import ac.su.learningplatform.domain.Lecture;
import ac.su.learningplatform.domain.Video;
import ac.su.learningplatform.dto.*;
import ac.su.learningplatform.repository.LectureRepository;
import ac.su.learningplatform.repository.UserRepository;
import ac.su.learningplatform.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public LectureService(LectureRepository lectureRepository, VideoRepository videoRepository, UserRepository userRepository, S3Service s3Service) {
        this.lectureRepository = lectureRepository;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    // 모든 강의 목록 조회
    public List<LectureListDTO> getAllFilteredLectures(String tag, String keyword) {

        List<Lecture> lectures;

        // 최소 2글자 이상인지 확인
        if (keyword != null && keyword.length() < 2) {
            keyword = null;
        }

        if (tag != null && !tag.isEmpty()) {
            // tag가 제공되면 tag로 검색, 삭제되지 않은 강의만
            lectures = lectureRepository.findByTagAndDelOrderByAttendanceCountDesc(tag, DeleteStatus.ACTIVE);
        } else if (keyword != null) {
            // keyword가 제공되면 keyword로 검색, 삭제되지 않은 강의만
            lectures = lectureRepository.findByTagContainingOrTitleContainingAndDelOrderByAttendanceCountDesc(keyword, keyword, DeleteStatus.ACTIVE);
        } else {
            // 둘 다 없으면 모든 강의 조회, 삭제되지 않은 강의만
            lectures = lectureRepository.findAllByDelOrderByAttendanceCountDesc(DeleteStatus.ACTIVE);
        }

        return lectures.stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }

    // 특정 강의의 세부정보 조회
    public LectureDetailsDTO getLectureById(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .map(this::convertToDetailsDTO)
                .orElse(null);
    }

    // 강의 생성
    public LectureRequestDTO createLecture(LectureRequestDTO lectureRequestDTO, List<MultipartFile> videoFiles) {
        Lecture lecture = convertToLecture(lectureRequestDTO);
        Lecture savedLecture = lectureRepository.save(lecture);

        List<Video> videos = IntStream.range(0, videoFiles.size())
                .mapToObj(index -> {
                    MultipartFile videoFile = videoFiles.get(index);
                    String videoUrl = s3Service.uploadFile(videoFile);
                    int duration = s3Service.getVideoDuration(videoFile);
                    VideoMetadataDTO videoMetadata = lectureRequestDTO.getVideos().get(index);
                    Video video = new Video();
                    video.setVideoOrder(index + 1);
                    video.setTitle(videoMetadata.getTitle());
                    video.setContent(videoUrl);
                    video.setUploadDate(LocalDateTime.now());
                    video.setRunningTime(duration);
                    video.setLecture(savedLecture);
                    return video;
                })
                .collect(Collectors.toList());

        videoRepository.saveAll(videos);

        return lectureRequestDTO;
    }

    // 강의 수정
    public LectureDetailsDTO updateLecture(Long lectureId, LectureDetailsDTO lectureDetailsDTO) {
        // 현재 강의 가져오기
        Lecture currentLecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException("Lecture not found"));

        // 강의 정보 업데이트
        currentLecture.setTitle(lectureDetailsDTO.getTitle());
        currentLecture.setComment(lectureDetailsDTO.getComment());
        currentLecture.setUpdateDate(LocalDateTime.now());

        // 기존 비디오 가져오기
        List<Video> existingVideos = currentLecture.getVideos();

        // 비디오 업데이트 및 삭제 처리
        for (Video existingVideo : existingVideos) {
            boolean found = false;

            for (VideoDTO.Response requestVideo : lectureDetailsDTO.getVideos()) {
                if (existingVideo.getVideoOrder() == requestVideo.getVideoOrder()) {
                    // 비디오가 요청에 있으면 업데이트
                    existingVideo.setTitle(requestVideo.getTitle());
                    existingVideo.setContent(requestVideo.getContent());
                    existingVideo.setRunningTime(requestVideo.getRunningTime());
                    found = true;
                    break;
                }
            }

            // 요청에 없는 비디오는 삭제 처리
            if (!found) {
                existingVideo.setVideoOrder(-1); // 삭제로 표시
                existingVideo.setDel(DeleteStatus.DELETED); // 상태 변경
                existingVideo.setDeleteDate(LocalDateTime.now()); // 삭제 날짜 설정
                videoRepository.save(existingVideo); // 변경 사항 저장
            }
        }

        // 새 비디오 추가
        List<Video> newVideos = lectureDetailsDTO.getVideos().stream()
                .filter(requestVideo -> existingVideos.stream()
                        .noneMatch(existingVideo -> existingVideo.getVideoOrder() == requestVideo.getVideoOrder()))
                .map(requestVideo -> {
                    Video video = new Video();
                    video.setVideoOrder(requestVideo.getVideoOrder());
                    video.setTitle(requestVideo.getTitle());
                    video.setContent(requestVideo.getContent());
                    video.setRunningTime(requestVideo.getRunningTime());
                    video.setUploadDate(LocalDateTime.now()); // 업로드 날짜 설정
                    video.setLecture(currentLecture); // 강의에 연결
                    return video;
                })
                .collect(Collectors.toList());

        videoRepository.saveAll(newVideos); // 새 비디오 저장

        // 강의 업데이트
        lectureRepository.save(currentLecture); // 강의 업데이트

        return convertToDetailsDTO(currentLecture); // LectureInfoDTO 반환
    }

    // Lecture -> LectureListDTO 변환
    private LectureListDTO convertToListDTO(Lecture lecture) {

        LectureListDTO lectureDTO = new LectureListDTO();
        lectureDTO.setLectureId(lecture.getLectureId());
        lectureDTO.setTag(lecture.getTag());
        lectureDTO.setTitle(lecture.getTitle());

        // 삭제되지 않은 비디오만 가져오기
        List<Video> activeVideos = lecture.getVideos().stream()
                .filter(video -> video.getDel() == DeleteStatus.ACTIVE)
                .toList();

        lectureDTO.setTotalVideoCount(activeVideos.size());
        lectureDTO.setAttendanceCount(lecture.getAttendanceCount());
        lectureDTO.setUserId(lecture.getUser().getUserId());
        return lectureDTO;
    }

    // Lecture -> LectureDetailsDTO 변환
    private LectureDetailsDTO convertToDetailsDTO(Lecture lecture) {

        LectureDetailsDTO lectureDTO = new LectureDetailsDTO();
        lectureDTO.setLectureId(lecture.getLectureId());
        lectureDTO.setTag(lecture.getTag());
        lectureDTO.setTitle(lecture.getTitle());
        lectureDTO.setComment(lecture.getComment());

        // 삭제되지 않은 비디오만 가져오기
        List<Video> activeVideos = lecture.getVideos().stream()
                .filter(video -> video.getDel() == DeleteStatus.ACTIVE)
                .toList();

        lectureDTO.setTotalVideoCount(activeVideos.size());
        // 각 비디오의 runningTime을 합산
        int totalSeconds = activeVideos.stream()
                .mapToInt(Video::getRunningTime)
                .sum();
        // 초 단위로 합산된 시간을 시, 분, 초로 변환
        LocalTime totalRunningTime = LocalTime.ofSecondOfDay(totalSeconds);
        lectureDTO.setTotalRunningTime(totalRunningTime);

        lectureDTO.setUserId(lecture.getUser().getUserId());

        // 비디오 목록을 videoOrder에 따라 오름차순 정렬 후 변환
        lectureDTO.setVideos(activeVideos.stream()
                .sorted(Comparator.comparingInt(Video::getVideoOrder)) // videoOrder로 정렬
                .map(video -> new VideoDTO.Response(
                                video.getVideoId(),
                                video.getVideoOrder(),
                                video.getTitle(),
                                video.getContent(),
                                video.getRunningTime()
                        )
                )
                .collect(Collectors.toList())
        );

        return lectureDTO;
    }

    // LectureRequestDTO -> Lecture 변환
    private Lecture convertToLecture(LectureRequestDTO requestDTO) {
        Lecture lecture = new Lecture();
        lecture.setTag(requestDTO.getTag());
        lecture.setTitle(requestDTO.getTitle());
        lecture.setComment(requestDTO.getComment());
        lecture.setUser(userRepository.findById(requestDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found")));
        lecture.setAttendanceCount(0);
        lecture.setCreateDate(LocalDateTime.now());

        return lecture;
    }

    // 강의 삭제
    public void deleteLecture(Long lectureId) {
        // 현재 강의 가져오기
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException("Lecture not found"));

        // 소프트 삭제를 위해 상태 변경
        lecture.setDel(DeleteStatus.DELETED);
        lecture.setDeleteDate(LocalDateTime.now());

        // 강의에 관련된 비디오들 가져오기
        List<Video> videos = videoRepository.findByLecture(lecture);

        // 비디오 상태 변경
        for (Video video : videos) {
            if (video.getDel() == DeleteStatus.ACTIVE) {
                video.setDel(DeleteStatus.DELETED);
                video.setDeleteDate(LocalDateTime.now());
            }
        }

        // 변경 사항 저장
        lectureRepository.save(lecture);
        videoRepository.saveAll(videos);
    }

    //

}