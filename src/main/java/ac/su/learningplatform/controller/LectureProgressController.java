package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.LectureProgressDTO;
import ac.su.learningplatform.service.UserLectureProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lectures")
@Controller
public class LectureProgressController {
    private final UserLectureProgressService userLectureProgressService;

    @Autowired
    public LectureProgressController(UserLectureProgressService userLectureProgressService) {
        this.userLectureProgressService = userLectureProgressService;
    }

    @GetMapping("/{userId}/{lectureId}")
    public LectureProgressDTO getLectureProgress(@PathVariable Long userId, @PathVariable Long lectureId) {
        return userLectureProgressService.calculateLectureProgress(userId, lectureId);
    }
}

