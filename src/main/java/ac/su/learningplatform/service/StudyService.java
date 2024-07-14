package ac.su.learningplatform.service;

import ac.su.learningplatform.constant.DeleteStatus;
import ac.su.learningplatform.domain.Comment;
import ac.su.learningplatform.domain.Study;
import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.dto.CommentDTO;
import ac.su.learningplatform.dto.StudyDetailsDTO;
import ac.su.learningplatform.dto.StudyListDTO;
import ac.su.learningplatform.dto.StudyRequestDTO;
import ac.su.learningplatform.repository.CommentRepository;
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

    public StudyService(StudyRepository studyRepository, CommentRepository commentRepository) {
        this.studyRepository = studyRepository;
        this.commentRepository = commentRepository;
    }

    // 모든 게시물 조회
    public List<StudyListDTO> getAllStudies() {
        List<Study> studies = studyRepository.findAllByDelOrderByCreateDateDesc(DeleteStatus.ACTIVE);

        return studies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 스터디 게시글의 세부정보 조회
    public StudyDetailsDTO getStudyById(Long studyId) {
        return studyRepository.findById(studyId)
                .map(this::convertToDetailsDTO)
                .orElse(null);
    }

    // 스터디 게시글 생성
    public StudyRequestDTO createStudy(StudyRequestDTO studyRequestDTO) {
        Study study = convertToStudy(studyRequestDTO);
        studyRepository.save(study);

        return studyRequestDTO;
    }

    // 스터디 게시글 수정
    public StudyRequestDTO updateStudy(Long studyId, StudyRequestDTO studyRequestDTO) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("Study not found"));

        study.setTitle(studyRequestDTO.getTitle());
        study.setField(studyRequestDTO.getField());
        study.setUpdateDate(LocalDateTime.now());

        studyRepository.save(study);

        return studyRequestDTO;
    }

    // Study -> StudyListDTO 변환
    private StudyListDTO convertToDTO(Study study) {
        return new StudyListDTO(
                study.getTitle(),
                study.getField(),
                study.getComments().size(),
                study.getCreateDate(),
                study.getUser().getUserId()
        );
    }

    // Study -> StudyDetailsDTO 변환
    private StudyDetailsDTO convertToDetailsDTO(Study study) {

        StudyDetailsDTO studyDTO = new StudyDetailsDTO();
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

    // StudyRequestDTO -> Study 변환
    private Study convertToStudy(StudyRequestDTO studyRequestDTO) {
        Study study = new Study();
        study.setTitle(studyRequestDTO.getTitle());
        study.setField(studyRequestDTO.getField());

        User user = new User();
        user.setUserId(studyRequestDTO.getUserId());
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
}
