package ac.su.learningplatform.controller;

import ac.su.learningplatform.dto.UserRankingDTO;
import ac.su.learningplatform.service.UserRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class UserRankingController {

    private final UserRankingService userRankingService;

    @Autowired
    public UserRankingController(UserRankingService userRankingService) {
        this.userRankingService = userRankingService;
    }

    @GetMapping
    public List<UserRankingDTO> getUserRanking() {
        return userRankingService.getUserRanking();
    }
}