package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.User;
import ac.su.learningplatform.constant.Role;
import ac.su.learningplatform.service.JwtService;
import ac.su.learningplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Map;

@Controller
@RequestMapping("/auth")
@SessionAttributes({"oauth2User", "provider", "email", "name", "profileImage"})
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/chooseSocialLogin")
    public String chooseSocialLoginPage() {
        return "chooseSocialLogin";
    }

    @PostMapping("/chooseSocialLogin")
    public String chooseSocialLogin(@RequestParam("provider") String provider, Model model) {
        model.addAttribute("provider", provider);
        if ("google".equals(provider)) {
            return "redirect:/oauth2/authorization/google";
        } else if ("naver".equals(provider)) {
            return "redirect:/oauth2/authorization/naver";
        } else {
            return "redirect:/auth/chooseSocialLogin";
        }
    }

    @GetMapping("/oauth2/redirect")
    public String handleOAuth2Redirect(HttpSession session, Model model) {
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
                Map<String, Object> response = oAuth2User.getAttribute("response");
                if (response != null) {
                    email = (String) response.get("email");
                    name = (String) response.get("name");
                    profileImage = (String) response.get("profile_image");
                }
            }

            User existingUser = userService.findByEmail(email);
            if (existingUser != null) {
                String jwtToken = jwtService.generateToken(existingUser.getEmail());
                session.setAttribute("jwtToken", jwtToken);
                return "redirect:/auth/main";
            }
            model.addAttribute("oauth2User", oAuth2User);
            model.addAttribute("provider", provider);
            model.addAttribute("email", email);
            model.addAttribute("name", name);
            model.addAttribute("profileImage", profileImage);
        } else {
            return "redirect:/auth/chooseSocialLogin";
        }
        return "redirect:/auth/additionalInfo";
    }

    @GetMapping("/additionalInfo")
    public String additionalInfoPage(@SessionAttribute("oauth2User") OAuth2User oAuth2User,
                                     @SessionAttribute("email") String email,
                                     @SessionAttribute("name") String name,
                                     @SessionAttribute("profileImage") String profileImage,
                                     Model model) {
        if (oAuth2User == null) {
            return "redirect:/auth/chooseSocialLogin";
        }
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        model.addAttribute("profileImage", profileImage);
        return "additionalInfo";
    }

    @PostMapping("/additionalInfo")
    public String additionalInfo(@RequestParam("nickname") String nickname,
                                 @RequestParam("goalVidCnt") int goalVidCnt,
                                 @SessionAttribute("email") String email,
                                 @SessionAttribute("name") String name,
                                 @SessionAttribute("profileImage") String profileImage,
                                 @SessionAttribute("provider") String provider,
                                 SessionStatus sessionStatus,
                                 HttpSession session) {

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setProfileImage(profileImage);
        user.setNickname(nickname);
        user.setGoalVidCnt(goalVidCnt);
        user.setSocialType(provider);
        user.setRole(Role.USER);

        User savedUser;
        try {
            savedUser = userService.signup(user);
        } catch (Exception e) {
            return "redirect:/auth/chooseSocialLogin";
        }

        sessionStatus.setComplete();

        String jwtToken = jwtService.generateToken(email);
        // JWT 토큰을 세션에 저장
        session.setAttribute("jwtToken", jwtToken);
        return "redirect:/auth/main";
    }

    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {
        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            return "redirect:/auth/chooseSocialLogin";
        }

        // JWT 토큰을 파싱하여 사용자 이메일을 추출
        String email = jwtService.extractUsername(token);

        // 이메일로 사용자 정보 조회
        User user = userService.findByEmail(email);

        // 사용자 정보를 모델에 추가
        model.addAttribute("name", user.getName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("goalVidCnt", user.getGoalVidCnt());
        model.addAttribute("dailyVidCnt", user.getDailyVidCnt());

        return "main";
    }

    @GetMapping("/editProfile")
    public String editProfilePage(HttpSession session, Model model) {
        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            return "redirect:/auth/chooseSocialLogin";
        }

        // JWT 토큰을 파싱하여 사용자 이메일을 추출
        String email = jwtService.extractUsername(token);

        // 이메일로 사용자 정보 조회
        User user = userService.findByEmail(email);

        // 사용자 정보를 모델에 추가
        model.addAttribute("name", user.getName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("goalVidCnt", user.getGoalVidCnt());
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("profileImage", user.getProfileImage());

        return "profileEdit";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam("nickname") String nickname,
                                @RequestParam("goalVidCnt") int goalVidCnt,
                                @RequestParam("profileImage") String profileImage,
                                HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        if (token == null) {
            return "redirect:/auth/chooseSocialLogin";
        }

        // JWT 토큰을 파싱하여 사용자 이메일을 추출
        String email = jwtService.extractUsername(token);

        // 이메일로 사용자 정보 조회
        User user = userService.findByEmail(email);

        // 사용자 정보 업데이트
        user.setNickname(nickname);
        user.setGoalVidCnt(goalVidCnt);
        user.setProfileImage(profileImage);
        userService.updateUser(user.getUserId(), user);

        return "redirect:/auth/main";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/chooseSocialLogin";
    }
}
