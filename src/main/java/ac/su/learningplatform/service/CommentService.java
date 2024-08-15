package ac.su.learningplatform.service;

import ac.su.learningplatform.constant.DeleteStatus;
import ac.su.learningplatform.domain.*;
import ac.su.learningplatform.dto.CommentCreateRequestDTO;
import ac.su.learningplatform.dto.CommentDTO;
import ac.su.learningplatform.repository.CommentDepthRepository;
import ac.su.learningplatform.repository.CommentRepository;
import ac.su.learningplatform.repository.StudyRepository;
import ac.su.learningplatform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentDepthRepository commentDepthRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, CommentDepthRepository commentDepthRepository,
                          StudyRepository studyRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.commentDepthRepository = commentDepthRepository;
        this.studyRepository = studyRepository;
        this.userRepository = userRepository;
    }

    // 댓글 생성
    @Transactional
    public CommentDTO.Response createComment(Long studyId, CommentCreateRequestDTO commentCreateRequest) {
        if (studyId == null) {
            throw new IllegalArgumentException("studyId는 null일 수 없습니다.");
        }

        if (commentCreateRequest.getUserId() == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 스터디를 찾을 수 없습니다: " + studyId));

        User user = userRepository.findById(commentCreateRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: " + commentCreateRequest.getUserId()));

        // 댓글 생성 및 저장
        Comment comment = new Comment();
        comment.setContent(commentCreateRequest.getContent());
        comment.setStudy(study);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);

        // CommentDepth 생성 및 저장
        CommentDepth commentDepth = new CommentDepth();
        CommentDepthId commentDepthId;

        if (commentCreateRequest.getParentId() == null) {
            // 일반 댓글의 경우 조상 댓글과 자손 댓글 모두 자기 자신
            commentDepthId = new CommentDepthId(savedComment.getCommentId(), savedComment.getCommentId());

            commentDepth.setId(commentDepthId);
            commentDepth.setAncestorComment(savedComment); // 조상 댓글은 자신
            commentDepth.setDescendantComment(savedComment); // 자손 댓글도 자신
            commentDepth.setDepth(0); // 최상위 댓글
        } else {
            // parentId에 해당하는 Comment 를 조회
            Comment parentComment = commentRepository.findById(commentCreateRequest.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 ID의 부모 댓글을 찾을 수 없습니다: " + commentCreateRequest.getParentId()));

            CommentDepth parentCommentDepth = commentDepthRepository.findFirstByDescendantComment(parentComment);

            commentDepthId = new CommentDepthId(parentCommentDepth.getAncestorComment().getCommentId(), savedComment.getCommentId());

            commentDepth.setId(commentDepthId);
            commentDepth.setAncestorComment(parentCommentDepth.getAncestorComment()); // 부모 댓글의 조상 댓글과 동일하게 설정
            commentDepth.setDescendantComment(savedComment); // 자손 댓글은 자신
            commentDepth.setDepth(parentCommentDepth.getDepth() + 1); // 부모 댓글의 깊이 + 1
        }

        commentDepthRepository.save(commentDepth);

        return convertToResponse(savedComment);
    }

    // 댓글 수정
    @Transactional
    public CommentDTO.Response updateComment(Long commentId, CommentDTO.Request commentRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 댓글을 찾을 수 없습니다: " + commentId));

        comment.setContent(commentRequest.getContent());

        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: " + commentRequest.getUserId()));
        comment.setUser(user);

        commentRepository.save(comment);

        return convertToResponse(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 댓글을 찾을 수 없습니다: " + commentId));

        comment.setDel(DeleteStatus.DELETED);
        comment.setDeleteDate(LocalDateTime.now());

        commentRepository.save(comment);
    }

    // 특정 스터디의 모든 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentDTO.Response> getCommentsByStudyId(Long studyId) {
        List<Comment> comments = commentRepository.findByStudy_StudyIdAndDel(studyId, DeleteStatus.ACTIVE);
        return comments.stream()
                .map(comment -> new CommentDTO.Response(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getCreateDate(),
                        comment.getUser().getUserId()
                ))
                .collect(Collectors.toList());
    }

    // Comment -> CommentDTO.Response 변환
    private CommentDTO.Response convertToResponse(Comment comment) {
        return new CommentDTO.Response(
                comment.getCommentId(),
                comment.getContent(),
                comment.getCreateDate(),
                comment.getUser().getUserId()
        );
    }

    public boolean isCommentOwner(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        return comment.getUser().getUserId().equals(userId);
    }
}
