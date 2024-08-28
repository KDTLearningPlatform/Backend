package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.constant.Role;
import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.UserRankingService;
import ac.su.learningplatform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/auth")
@SessionAttributes({"oauth2User", "provider", "email", "name", "profileImage"})
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRankingService userRankingService;

    @PostMapping("/chooseSocialLogin")
    public ResponseEntity<Map<String, String>> chooseSocialLogin(@RequestParam("provider") String provider) {
        String redirectUrl;
        if ("google".equals(provider)) {
            redirectUrl = "/oauth2/authorization/google";
        } else if ("naver".equals(provider)) {
            redirectUrl = "/oauth2/authorization/naver";
        } else {
            redirectUrl = "/auth/chooseSocialLogin";
        }
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", redirectUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth2/redirect")
    public RedirectView handleOAuth2Redirect(HttpSession session, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken token) {
            OAuth2User oAuth2User = token.getPrincipal();
            String provider = token.getAuthorizedClientRegistrationId();
            String email = null;
            String name = null;
            String profileImage = null;

            if ("google".equals(provider)) {
                email = oAuth2User.getAttribute("email");
                name = oAuth2User.getAttribute("name");
                profileImage = oAuth2User.getAttribute("picture");
            } else if ("naver".equals(provider)) {
                Map<String, Object> responseMap = oAuth2User.getAttribute("response");
                if (responseMap != null) {
                    email = (String) responseMap.get("email");
                    name = (String) responseMap.get("name");
                    profileImage = (String) responseMap.get("profile_image");
                }
            }

            User existingUser = userService.findByEmail(email);
            if (existingUser != null) {
                String jwtToken = jwtService.generateToken(existingUser.getEmail());
                session.setAttribute("jwtToken", jwtToken);
                userRankingService.updateRankingCache();
                return new RedirectView("http://localhost:3000/main");
            }
            session.setAttribute("oauth2User", oAuth2User);
            session.setAttribute("provider", provider);
            session.setAttribute("email", email);
            session.setAttribute("name", name);
            session.setAttribute("profileImage", profileImage);
            return new RedirectView("http://localhost:3000/profilecompletion");
        } else {
            return new RedirectView("http://localhost:3000/login");
        }
    }

    @GetMapping("/additionalInfo")
    public ResponseEntity<Map<String, String>> additionalInfoPage(HttpSession session) {
        Map<String, String> response = new HashMap<>();
        response.put("email", (String) session.getAttribute("email"));
        response.put("name", (String) session.getAttribute("name"));
        response.put("profileImage", (String) session.getAttribute("profileImage"));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/additionalInfo")
    public ResponseEntity<Map<String, String>> additionalInfo(@RequestBody Map<String, Object> payload, HttpSession session) {
        try {
            String nickname = (String) payload.get("nickname");
            int goalVidCnt = (Integer) payload.get("goalVidCnt");
            String email = (String) session.getAttribute("email");
            String name = (String) session.getAttribute("name");
            String profileImage = (String) session.getAttribute("profileImage");
            String provider = (String) session.getAttribute("provider");

            if (email == null || name == null || profileImage == null || provider == null) {
                logger.error("Session attributes are missing");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "no"));
            }

            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProfileImage(profileImage);
            user.setNickname(nickname);
            user.setGoalVidCnt(goalVidCnt);
            user.setSocialType(provider);
            user.setRole(Role.USER);

            User savedUser = userService.signup(user);
            String jwtToken = jwtService.generateToken(email);
            session.setAttribute("jwtToken", jwtToken);

            userRankingService.updateRankingCache();

            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            logger.error("Error during additionalInfo processing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "no"));
        }
    }

    @GetMapping("/main")
    public ResponseEntity<Map<String, String>> mainPage(HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            return ResponseEntity.ok(Map.of("status", "no"));
        }

        String email = jwtService.extractUsername(token);
        User user = userService.findByEmail(email);

        Map<String, String> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("name", user.getNickname());
        response.put("goalVidCnt", String.valueOf(user.getGoalVidCnt()));
        response.put("dailyVidCnt", String.valueOf(user.getDailyVidCnt()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/editProfile")
    public ResponseEntity<Map<String, Object>> editProfilePage(HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String email = jwtService.extractUsername(token);
        User user = userService.findByEmail(email);

        Map<String, Object> response = new HashMap<>();
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("goalVidCnt", user.getGoalVidCnt());
        response.put("nickname", user.getNickname());
        response.put("profileImage", user.getProfileImage());
        response.put("point", user.getTotalPoint());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody Map<String, Object> payload,
                                                             HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "redirect"));
        }

        String email = jwtService.extractUsername(token);
        User user = userService.findByEmail(email);

        String nickname = (String) payload.get("nickname");
        int goalVidCnt = (Integer) payload.get("goalVidCnt");
        String profileImage = (String) payload.get("profileImage");

        user.setNickname(nickname);
        user.setGoalVidCnt(goalVidCnt);
        user.setProfileImage(profileImage);
        userService.updateUser(user.getUserId(), user);

        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("status", "redirect"));
    }

    // jwt 토큰을 확인하기 위한 임시 Controller 추가
    @GetMapping("/session-info")
    public ResponseEntity<Map<String, String>> getSessionInfo(HttpSession session) {
        Map<String, String> response = new HashMap<>();
        String jwtToken = (String) session.getAttribute("jwtToken");

        response.put("jwtToken", Objects.requireNonNullElse(jwtToken, "없음"));

        return ResponseEntity.ok(response);
    }
}