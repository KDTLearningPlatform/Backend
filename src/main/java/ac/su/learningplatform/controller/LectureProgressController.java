package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.LectureProgressDTO;
import ac.su.learningplatform.service.LectureProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lectureProgress")
@Controller
public class LectureProgressController {
    private final LectureProgressService lectureProgressService;

    @Autowired
    public LectureProgressController(LectureProgressService lectureProgressService) {
        this.lectureProgressService = lectureProgressService;
    }

    // 강의 진행률을 계산하는 엔드포인트
    @GetMapping("/{userId}/{lectureId}")
    public LectureProgressDTO getLectureProgress(@PathVariable Long userId, @PathVariable Long lectureId) {
        return lectureProgressService.calculateLectureProgress(userId, lectureId);
    }

    // 진행중인 강의 목록을 반환하는 엔드포인트
    @GetMapping("/in-progress/{userId}")
    public List<LectureProgressDTO> getInProgressLectures(@PathVariable Long userId) {
        return lectureProgressService.getInProgressLectures(userId);
    }
}

