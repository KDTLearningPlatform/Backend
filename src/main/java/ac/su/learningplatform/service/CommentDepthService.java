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
//    public CommentDepthService(CommentRepository commentRepository,
//                               CommentDepthRepository commentDepthRepository,
//                               UserRepository userRepository,
//                               StudyRepository studyRepository) {
//        this.commentRepository = commentRepository;
//        this.commentDepthRepository = commentDepthRepository;
//        this.userRepository = userRepository;
//        this.studyRepository = studyRepository;
//    }
//
//    // 새로운 댓글을 생성하는 메소드
//    @Transactional
//    public CommentDTO.Response createComment(Long studyId, CommentDTO.Request commentRequest) {
//        // 스터디 ID로 스터디 객체를 찾음. 없으면 예외 발생
//        Study study = studyRepository.findById(studyId)
//                .orElseThrow(() -> new EntityNotFoundException("Study not found"));
//
//        // 사용자 ID로 사용자 객체를 찾음. 없으면 예외 발생
//        User user = userRepository.findById(commentRequest.getUserId())
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        // 새로운 댓글 객체 생성 및 초기화
//        Comment newComment = new Comment();
//        newComment.setContent(commentRequest.getContent());
//        newComment.setCreateDate(LocalDateTime.now());
//        newComment.setStudy(study);
//        newComment.setUser(user);
//
//        // 댓글 저장
//        Comment savedComment = commentRepository.save(newComment);
//
//        // 부모 댓글이 있을 경우 부모 댓글과의 관계를 저장
//        if (commentRequest.getParentCommentId() != null) {
//            Comment parentComment = commentRepository.findById(commentRequest.getParentCommentId())
//                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found: " + commentRequest.getParentCommentId()));
//
//            createCommentDepth(parentComment, savedComment);
//        }
//
//        // 자기 자신과의 관계를 저장 (루트 댓글일 경우)
//        createCommentDepth(savedComment, savedComment);
//
//        // 저장된 댓글을 응답 DTO로 변환하여 반환
//        return convertToResponse(savedComment);
//    }
//
//    // 댓글 사이의 깊이 관계를 저장하는 메소드
//    private void createCommentDepth(Comment ancestor, Comment descendant) {
//        // CommentDepth 객체 생성 및 초기화
//        CommentDepth commentDepth = new CommentDepth();
//        commentDepth.setId(new CommentDepthId(ancestor.getCommentId(), descendant.getCommentId()));
//        commentDepth.setAncestorComment(ancestor);
//        commentDepth.setDescendantComment(descendant);
//        commentDepth.setDepth(ancestor.equals(descendant) ? 0 : 1); // 루트 댓글이면 깊이 0, 자식 댓글이면 깊이 1
//
//        // 깊이 정보 저장
//        commentDepthRepository.save(commentDepth);
//
//        // 부모 댓글과의 관계가 있는 경우, 그 관계에 따른 추가 깊이 정보 저장
//        if (!ancestor.equals(descendant)) {
//            List<CommentDepth> ancestorDepths = commentDepthRepository.findByAncestorComment_CommentIdOrderByDepthAsc(ancestor.getCommentId());
//            for (CommentDepth depth : ancestorDepths) {
//                CommentDepth newDepth = new CommentDepth();
//                newDepth.setId(new CommentDepthId(depth.getAncestorComment().getCommentId(), descendant.getCommentId()));
//                newDepth.setAncestorComment(depth.getAncestorComment());
//                newDepth.setDescendantComment(descendant);
//                newDepth.setDepth(depth.getDepth() + 1); // 부모 댓글의 깊이에 1을 더함
//                commentDepthRepository.save(newDepth);
//            }
//        }
//    }
//
//    // 특정 스터디 ID에 해당하는 모든 댓글을 조회하는 메소드
//    @Transactional(readOnly = true)
//    public List<CommentDTO.Response> getCommentsByStudyId(Long studyId) {
//        // 해당 스터디 ID에 속한 모든 댓글 깊이 정보 조회
//        List<CommentDepth> commentDepths = commentDepthRepository.findByAncestorComment_Study_StudyIdOrderByDepthAsc(studyId);
//        // 각 댓글을 응답 DTO로 변환하여 리스트로 반환
//        return commentDepths.stream()
//                .map(cd -> convertToResponse(cd.getDescendantComment()))
//                .collect(Collectors.toList());
//    }
//
//    // 댓글 엔티티를 응답 DTO로 변환하는 메소드
//    private CommentDTO.Response convertToResponse(Comment comment) {
//        return new CommentDTO.Response(
//                comment.getCommentId(),
//                comment.getContent(),
//                comment.getCreateDate(),
//                comment.getUser().getUserId(),
//                comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null
//        );
//    }
//}
