package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.dto.LectureDetailsDTO;
import ac.su.learningplatform.dto.LectureListDTO;
import ac.su.learningplatform.dto.LectureRequestDTO;
import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.LectureService;
import ac.su.learningplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;
    private final JwtService jwtService;
    private final UserService userService;

    public LectureController(LectureService lectureService, JwtService jwtService, UserService userService) {
        this.lectureService = lectureService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @GetMapping("/createLecture")
    public String createLecturePage() {
        return "createLecture";
    }

    @GetMapping
    public ResponseEntity<List<LectureListDTO>> getAllLectures(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword) {
        List<LectureListDTO> lectures = lectureService.getAllFilteredLectures(tag, keyword);
        return new ResponseEntity<>(lectures, HttpStatus.OK);
    }

    @GetMapping("/details/{lectureId}")
    public String lectureDetailsPage(@PathVariable Long lectureId, Model model, HttpSession session) {
        LectureDetailsDTO lectureDetails = lectureService.getLectureById(lectureId);
        model.addAttribute("lectureDetails", lectureDetails);

        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            return "redirect:/auth/chooseSocialLogin";
        }

        // JWT 토큰을 파싱하여 사용자 이메일을 추출
        String email = jwtService.extractUsername(token);
        if (email == null) {
            model.addAttribute("errorMessage", "사용자 정보를 불러오는 중 오류가 발생했습니다.");
            return "lectureDetails";
        }

        // 이메일로 사용자 정보 조회
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("errorMessage", "사용자 정보를 불러오는 중 오류가 발생했습니다.");
            return "lectureDetails";
        }

        model.addAttribute("currentUserId", user.getUserId());
        model.addAttribute("lectureUserId", lectureDetails.getUserId());

        return "lectureDetails";
    }
    @GetMapping("/editLecture/{lectureId}")
    public String editLecturePage(@PathVariable Long lectureId, Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            return "redirect:/auth/chooseSocialLogin";
        }

        // JWT 토큰을 파싱하여 사용자 이메일을 추출
        String email = jwtService.extractUsername(token);
        if (email == null) {
            model.addAttribute("errorMessage", "사용자 정보를 불러오는 중 오류가 발생했습니다.");
            return "editLecture";
        }

        // 이메일로 사용자 정보 조회
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("errorMessage", "사용자 정보를 불러오는 중 오류가 발생했습니다.");
            return "editLecture";
        }

        // 강의 정보를 조회하여 모델에 추가
        LectureDetailsDTO lecture = lectureService.getLectureById(lectureId);
        model.addAttribute("lecture", lecture);

        // 사용자 정보를 모델에 추가
        model.addAttribute("name", user.getName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("goalVidCnt", user.getGoalVidCnt());
        model.addAttribute("dailyVidCnt", user.getDailyVidCnt());

        return "editLecture";
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureDetailsDTO> getLectureById(@PathVariable Long lectureId) {
        LectureDetailsDTO lectureDTO = lectureService.getLectureById(lectureId);
        return new ResponseEntity<>(lectureDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LectureRequestDTO> createLecture(
            @RequestPart("lecture") LectureRequestDTO lectureRequestDTO,
            @RequestPart("files") List<MultipartFile> files, HttpSession session) {

        String jwtToken = (String) session.getAttribute("jwtToken");

        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String userEmail = jwtService.extractUsername(jwtToken);
        Long userId = userService.findByEmail(userEmail).getUserId();

        lectureRequestDTO.setUserId(userId);
        LectureRequestDTO createdLectureDTO = lectureService.createLecture(lectureRequestDTO, files);
        return new ResponseEntity<>(createdLectureDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{lectureId}")
    public ResponseEntity<LectureDetailsDTO> updateLecture(
            @PathVariable Long lectureId,
            @RequestPart("lecture") LectureDetailsDTO lectureDetailsDTO,
            @RequestPart("files") List<MultipartFile> files) {
        LectureDetailsDTO updatedLecture = lectureService.updateLecture(lectureId, lectureDetailsDTO, files);
        return new ResponseEntity<>(updatedLecture, HttpStatus.OK);
    }

    @DeleteMapping("/{lectureId}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long lectureId) {
        lectureService.deleteLecture(lectureId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
