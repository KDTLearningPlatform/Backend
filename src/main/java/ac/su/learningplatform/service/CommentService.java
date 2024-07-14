package ac.su.learningplatform.service;

import ac.su.learningplatform.constant.DeleteStatus;
import ac.su.learningplatform.domain.Comment;
import ac.su.learningplatform.domain.Study;
import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.dto.CommentDTO;
import ac.su.learningplatform.repository.CommentRepository;
import ac.su.learningplatform.repository.StudyRepository;
import ac.su.learningplatform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          StudyRepository studyRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.studyRepository = studyRepository;
        this.userRepository = userRepository;
    }

    // 댓글 생성
    @Transactional
    public CommentDTO.Response createComment(Long studyId, CommentDTO.Request commentRequest) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 스터디를 찾을 수 없습니다: " + studyId));

        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: " + commentRequest.getUserId()));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setStudy(study);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);

        return convertToResponse(savedComment);
    }

    // 댓글 수정 (임시로직 / 로직 수정 목표)
    public CommentDTO.Response updateComment(Long commentId, CommentDTO.Request commentRequest) {

        // 댓글 찾기
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 댓글을 찾을 수 없습니다: " + commentId));

        // 댓글 내용 수정
        comment.setContent(commentRequest.getContent());
        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: " + commentRequest.getUserId()));
        comment.setUser(user);

        commentRepository.save(comment);

        return convertToResponse(comment); // 댓글을 DTO 로 변환하여 반환

    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 댓글을 찾을 수 없습니다: " + commentId));

        comment.setDel(DeleteStatus.DELETED);
        comment.setDeleteDate(LocalDateTime.now());

        commentRepository.save(comment);

    }

    // CommentDTO.Request -> Comment 변환
    private CommentDTO.Response convertToResponse(Comment comment) {
        return new CommentDTO.Response(
                comment.getCommentId(),
                comment.getContent(),
                comment.getCreateDate(),
                comment.getUser().getUserId()
        );
    }
}
