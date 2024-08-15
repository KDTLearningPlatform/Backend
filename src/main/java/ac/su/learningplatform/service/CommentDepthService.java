//package ac.su.learningplatform.service;
//
//import ac.su.learningplatform.domain.Comment;
//import ac.su.learningplatform.domain.CommentDepth;
//import ac.su.learningplatform.domain.CommentDepthId;
//import ac.su.learningplatform.domain.Study;
//import ac.su.learningplatform.domain.User;
//import ac.su.learningplatform.dto.CommentDTO;
//import ac.su.learningplatform.repository.CommentDepthRepository;
//import ac.su.learningplatform.repository.CommentRepository;
//import ac.su.learningplatform.repository.StudyRepository;
//import ac.su.learningplatform.repository.UserRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class CommentDepthService {
//
//    private final CommentRepository commentRepository;
//    private final CommentDepthRepository commentDepthRepository;
//    private final UserRepository userRepository;
//    private final StudyRepository studyRepository;
//
//    public CommentDepthService(CommentRepository commentRepository, CommentDepthRepository commentDepthRepository, UserRepository userRepository, StudyRepository studyRepository) {
//        this.commentRepository = commentRepository;
//        this.commentDepthRepository = commentDepthRepository;
//        this.userRepository = userRepository;
//        this.studyRepository = studyRepository;
//    }
//
//    @Transactional
//    public CommentDTO.Response createComment(Long studyId, CommentDTO.Request commentRequest) {
//        Study study = studyRepository.findById(studyId)
//                .orElseThrow(() -> new EntityNotFoundException("Study not found"));
//
//        User user = userRepository.findById(commentRequest.getUserId())
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        Comment newComment = new Comment();
//        newComment.setContent(commentRequest.getContent());
//        newComment.setCreateDate(LocalDateTime.now());
//        newComment.setStudy(study);
//        newComment.setUser(user);
//
//        if (commentRequest.getParentCommentId() != null) {
//            Comment parentComment = commentRepository.findById(commentRequest.getParentCommentId())
//                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found: " + commentRequest.getParentCommentId()));
//
//            newComment.setParentComment(parentComment);
//            commentRepository.save(newComment);
//            createCommentDepth(parentComment, newComment);
//        } else {
//            commentRepository.save(newComment);
//            createCommentDepth(newComment, newComment);
//        }
//
//        return convertToResponse(newComment);
//    }
//
//    private void createCommentDepth(Comment ancestor, Comment descendant) {
//        CommentDepth commentDepth = new CommentDepth();
//        commentDepth.setId(new CommentDepthId(ancestor.getCommentId(), descendant.getCommentId()));
//        commentDepth.setAncestorComment(ancestor);
//        commentDepth.setDescendantComment(descendant);
//        commentDepth.setDepth(ancestor.equals(descendant) ? 0 : 1);
//
//        commentDepthRepository.save(commentDepth);
//
//        if (!ancestor.equals(descendant)) {
//            List<CommentDepth> ancestorDepths = commentDepthRepository.findByAncestorComment_CommentIdOrderByDepthAsc(ancestor.getCommentId());
//            for (CommentDepth depth : ancestorDepths) {
//                CommentDepth newDepth = new CommentDepth();
//                newDepth.setId(new CommentDepthId(depth.getAncestorComment().getCommentId(), descendant.getCommentId()));
//                newDepth.setAncestorComment(depth.getAncestorComment());
//                newDepth.setDescendantComment(descendant);
//                newDepth.setDepth(depth.getDepth() + 1);
//                commentDepthRepository.save(newDepth);
//            }
//        }
//    }
//
//    @Transactional(readOnly = true)
//    public List<CommentDTO.Response> getCommentsByStudyId(Long studyId) {
//        List<CommentDepth> commentDepths = commentDepthRepository.findByAncestorComment_Study_StudyIdOrderByDepthAsc(studyId);
//        return commentDepths.stream()
//                .map(cd -> convertToResponse(cd.getDescendantComment()))
//                .distinct()
//                .collect(Collectors.toList());
//    }
//
//    private CommentDTO.Response convertToResponse(Comment comment) {
//        return new CommentDTO.Response(
//                comment.getCommentId(),
//                comment.getContent(),
//                comment.getCreateDate(),
//                comment.getUser().getUserId(),
//                comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null
//        );
//    }
//
//    @Transactional
//    public void deleteComment(Long commentId) {
//        // 댓글이 존재하는지 확인
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
//
//        // CommentDepth에서 해당 댓글과 관련된 모든 관계 삭제
//        List<CommentDepth> commentDepths = commentDepthRepository.findByAncestorComment_CommentIdOrderByDepthAsc(commentId);
//        commentDepths.forEach(commentDepthRepository::delete);
//
//        // 댓글 삭제
//        commentRepository.delete(comment);
//    }
//
//    @Transactional
//    public CommentDTO.Response updateComment(Long commentId, CommentDTO.Request commentRequest) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
//
//        comment.setContent(commentRequest.getContent());
//
//        User user = userRepository.findById(commentRequest.getUserId())
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//        comment.setUser(user);
//
//        commentRepository.save(comment);
//
//        return convertToResponse(comment);
//    }
//}
