package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.LectureCompletedDTO;
import ac.su.learningplatform.dto.LectureProgressDTO;
import ac.su.learningplatform.dto.UserLectureProgressDTO;
import ac.su.learningplatform.service.LectureProgressService;
import ac.su.learningplatform.service.UserLectureProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lectureProgress")
@Controller
public class LectureProgressController {
    private final LectureProgressService lectureProgressService;

    @Autowired
    private UserLectureProgressService service;

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

    // 완료한 강의 목록을 반환하는 엔드포인트
    @GetMapping("{userId}/completed")
    public List<LectureCompletedDTO> getCompletedLectures(@PathVariable Long userId) {
        return lectureProgressService.getCompletedLectures(userId);
    }

    // 강의 진행률을 업데이트하는 엔드포인트
    @PostMapping("/update/{userId}/{lectureId}")
    public void updateLectureProgress(@PathVariable Long userId, @PathVariable Long lectureId,
                                      @RequestBody UserLectureProgressDTO dto) {
        service.updateProgress(userId, lectureId, dto);
    }
}

