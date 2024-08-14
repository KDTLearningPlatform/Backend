package ac.su.learningplatform.service;

import ac.su.learningplatform.constant.DeleteStatus;
import ac.su.learningplatform.domain.*;
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

    public CommentService(CommentRepository commentRepository,
                          CommentDepthRepository commentDepthRepository,
                          StudyRepository studyRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.commentDepthRepository = commentDepthRepository;
        this.studyRepository = studyRepository;
        this.userRepository = userRepository;
    }

    // 댓글 생성
    @Transactional
    public CommentDTO.Response createComment(Long studyId, CommentDTO.Request commentRequest) {
        // 스터디 ID로 스터디 객체를 찾음. 없으면 예외 발생
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("Study not found"));

        // 사용자 ID로 사용자 객체를 찾음. 없으면 예외 발생
        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 새로운 댓글 객체 생성 및 초기화
        Comment newComment = new Comment();
        newComment.setContent(commentRequest.getContent());
        newComment.setCreateDate(LocalDateTime.now());
        newComment.setStudy(study);
        newComment.setUser(user);

        // 댓글 저장
        Comment savedComment = commentRepository.save(newComment);

        // 부모 댓글이 있을 경우 부모 댓글과의 관계를 저장
        if (commentRequest.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(commentRequest.getParentCommentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found: " + commentRequest.getParentCommentId()));

            createCommentDepth(parentComment, savedComment);
        }

        // 자기 자신과의 관계를 저장 (루트 댓글일 경우)
        createCommentDepth(savedComment, savedComment);

        // 저장된 댓글을 응답 DTO로 변환하여 반환
        return convertToResponse(savedComment);
    }

    // 댓글 사이의 깊이 관계를 저장하는 메소드
    private void createCommentDepth(Comment ancestor, Comment descendant) {
        // CommentDepth 객체 생성 및 초기화
        CommentDepth commentDepth = new CommentDepth();
        commentDepth.setId(new CommentDepthId(ancestor.getCommentId(), descendant.getCommentId()));
        commentDepth.setAncestorComment(ancestor);
        commentDepth.setDescendantComment(descendant);
        commentDepth.setDepth(ancestor.equals(descendant) ? 0 : 1); // 루트 댓글이면 깊이 0, 자식 댓글이면 깊이 1

        // 깊이 정보 저장
        commentDepthRepository.save(commentDepth);

        // 부모 댓글과의 관계가 있는 경우, 그 관계에 따른 추가 깊이 정보 저장
        if (!ancestor.equals(descendant)) {
            List<CommentDepth> ancestorDepths = commentDepthRepository.findByAncestorComment_CommentIdOrderByDepthAsc(ancestor.getCommentId());
            for (CommentDepth depth : ancestorDepths) {
                CommentDepth newDepth = new CommentDepth();
                newDepth.setId(new CommentDepthId(depth.getAncestorComment().getCommentId(), descendant.getCommentId()));
                newDepth.setAncestorComment(depth.getAncestorComment());
                newDepth.setDescendantComment(descendant);
                newDepth.setDepth(depth.getDepth() + 1); // 부모 댓글의 깊이에 1을 더함
                commentDepthRepository.save(newDepth);
            }
        }
    }

    // 댓글 수정
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
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 댓글을 찾을 수 없습니다: " + commentId));

        comment.setDel(DeleteStatus.DELETED);
        comment.setDeleteDate(LocalDateTime.now());

        commentRepository.save(comment);
    }

    // 특정 스터디의 모든 댓글 조회
    public List<CommentDTO.Response> getCommentsByStudyId(Long studyId) {
        List<Comment> comments = commentRepository.findByStudy_StudyIdAndDel(studyId, DeleteStatus.ACTIVE);
        return comments.stream()
                .map(comment -> new CommentDTO.Response(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getCreateDate(),
                        comment.getUser().getUserId(),
                        comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null
                ))
                .collect(Collectors.toList());
    }

    // Comment -> CommentDTO.Response 변환
    private CommentDTO.Response convertToResponse(Comment comment) {
        return new CommentDTO.Response(
                comment.getCommentId(),
                comment.getContent(),
                comment.getCreateDate(),
                comment.getUser().getUserId(),
                comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null
        );
    }

    // 댓글 소유자 검증 메서드 추가
    public boolean isCommentOwner(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        return comment.getUser().getUserId().equals(userId);
    }
}
