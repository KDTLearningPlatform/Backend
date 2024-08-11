package ac.su.learningplatform.service;

import ac.su.learningplatform.constant.DeleteStatus;
import ac.su.learningplatform.domain.Comment;
import ac.su.learningplatform.domain.Study;
import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.dto.CommentDTO;
import ac.su.learningplatform.dto.StudyDetailsDTO;
import ac.su.learningplatform.dto.StudyListDTO;
import ac.su.learningplatform.dto.StudyDTO;
import ac.su.learningplatform.repository.CommentRepository;
import ac.su.learningplatform.repository.LoveRepository; // 추가
import ac.su.learningplatform.repository.StudyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final CommentRepository commentRepository;
    private final LoveRepository loveRepository; // 추가

    public StudyService(StudyRepository studyRepository, CommentRepository commentRepository, LoveRepository loveRepository) {
        this.studyRepository = studyRepository;
        this.commentRepository = commentRepository;
        this.loveRepository = loveRepository; // 추가
    }

    // 모든 게시물 조회
    public List<StudyListDTO> getAllStudies(Long userId) { // userId 추가
        List<Study> studies = studyRepository.findAllByDelOrderByCreateDateDesc(DeleteStatus.ACTIVE);

        // 생성일자를 기준으로 내림차순으로 정렬
        return studies.stream()
                .sorted(Comparator.comparing(Study::getCreateDate).reversed()) // 내림차순 정렬
                .map(study -> convertToDTO(study, userId)) // userId 추가
                .collect(Collectors.toList());
    }

    // 스터디 게시글 정보 조회
    public StudyDTO.Response getStudyById(Long studyId) {
        return studyRepository.findById(studyId)
                .map(this::convertToStudyResponseDTO)
                .orElse(null);
    }

    // 특정 스터디 게시글의 세부정보 조회 (댓글포함)
    public StudyDetailsDTO getStudyDetailsById(Long studyId) {
        return studyRepository.findById(studyId)
                .map(this::convertToDetailsDTO)
                .orElse(null);
    }

    // 스터디 게시글 생성
    public StudyDTO.Request createStudy(StudyDTO.Request studyDTO) {
        Study study = convertToStudy(studyDTO);
        studyRepository.save(study);

        return studyDTO;
    }

    // 스터디 게시글 수정
    public StudyDTO.Request updateStudy(Long studyId, StudyDTO.Request studyDTO) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("Study not found"));

        study.setTitle(studyDTO.getTitle());
        study.setField(studyDTO.getField());
        study.setUpdateDate(LocalDateTime.now());

        studyRepository.save(study);

        return studyDTO;
    }

    // Study -> StudyListDTO 변환
    private StudyListDTO convertToDTO(Study study, Long userId) { // userId 추가
        boolean liked = loveRepository.existsByUser_UserIdAndStudy_StudyId(userId, study.getStudyId()); // 좋아요 여부 체크
        return new StudyListDTO(
                study.getStudyId(),
                study.getTitle(),
                study.getField(),
                study.getComments().size(),
                study.getCreateDate(),
                study.getUser().getUserId(),
                liked // 좋아요 여부 추가
        );
    }

    // Study -> StudyDetailsDTO 변환
    private StudyDetailsDTO convertToDetailsDTO(Study study) {
        StudyDetailsDTO studyDTO = new StudyDetailsDTO();
        studyDTO.setStudyId(study.getStudyId());
        studyDTO.setTitle(study.getTitle());
        studyDTO.setField(study.getField());
        studyDTO.setCreateDate(study.getCreateDate());
        studyDTO.setUserId(study.getUser().getUserId());

        // 삭제되지 않은 댓글만 가져오기
        List<Comment> activeComments = study.getComments().stream()
                .filter(comment -> comment.getDel() == DeleteStatus.ACTIVE)
                .toList();

        // 댓글 목록을 createDate 에 따라 내림차순 정렬 후 반환
        studyDTO.setComments(activeComments.stream()
                .sorted(Comparator.comparing(Comment::getCreateDate).reversed()) // createDate로 내림차순 정렬
                .map(comment -> new CommentDTO.Response(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getCreateDate(),
                        comment.getUser().getUserId()
                        )
                )
                .collect(Collectors.toList())
        );

        return studyDTO;
    }

    // Study -> StudyResponseDTO 변환
    private StudyDTO.Response convertToStudyResponseDTO(Study study) {
        StudyDTO.Response studyDTO = new StudyDTO.Response();
        studyDTO.setStudyId(study.getStudyId());
        studyDTO.setTitle(study.getTitle());
        studyDTO.setField(study.getField());
        studyDTO.setUserId(study.getUser().getUserId());
        return studyDTO;
    }

    // StudyRequestDTO -> Study 변환
    private Study convertToStudy(StudyDTO.Request studyDTO) {
        Study study = new Study();
        study.setTitle(studyDTO.getTitle());
        study.setField(studyDTO.getField());

        User user = new User();
        user.setUserId(studyDTO.getUserId());
        study.setUser(user);

        return study;
    }

    // 스터디 게시글 삭제
    public void deleteStudy(Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("Study not found"));

        // 스터디
        study.setDel(DeleteStatus.DELETED);
        study.setDeleteDate(LocalDateTime.now());

        // 댓글 데이터 가져오기
        List<Comment> comments = commentRepository.findByStudy(study);

        // 댓글 상태 변경
        for (Comment comment : comments) {
            if (comment.getDel() == DeleteStatus.ACTIVE) {
                comment.setDel(DeleteStatus.DELETED);
                comment.setDeleteDate(LocalDateTime.now());
            }
        }

        // 변경사항저장
        studyRepository.save(study);
        commentRepository.saveAll(comments);
    }

    // 스터디 소유자 검증 메서드 추가
    public boolean isStudyOwner(Long studyId, Long userId) {
        // 스터디 ID로 스터디를 데이터베이스에서 조회
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("Study not found"));

        // 스터디의 작성자 ID와 사용자 ID가 일치하는지 확인 true or false 반환
        return study.getUser().getUserId().equals(userId);
    }
}
