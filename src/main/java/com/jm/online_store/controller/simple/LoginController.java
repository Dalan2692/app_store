package com.jm.online_store.controller.simple;

import com.jm.online_store.config.security.Twitter.TwitterAuth;
import com.jm.online_store.config.security.odnoklassniki.OAuth2Odnoklassniki;
import com.jm.online_store.config.security.vk.VkApiClient;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class LoginController {

    private final OAuth2Odnoklassniki oAuth2Odnoklassniki;
    private final TwitterAuth twitterAuth;
    private final UserService userService;
    private final VkApiClient vkApiClient;

    @GetMapping("/oauth")
    public String oAuthOdnoklassniki(@RequestParam String code) {
        oAuth2Odnoklassniki.UserAuth(code);
        return "redirect:/";
    }

    @GetMapping("/oauthTwitter")
    public String oAuthTwitter(@RequestParam String oauth_verifier) throws InterruptedException, ExecutionException, IOException {
        if (twitterAuth.getAccessToken(oauth_verifier)) {
            return "redirect:/";
        } else {
            return "TwitterRegistrationPage";
        }
    }

    @GetMapping("/TwitterRegistrationPage")
    public String twitterRegPage() {
        return "TwitterRegistrationPage";
    }

    @GetMapping(value = "/login")
    public String loginPage(Model model,
                            @RequestParam(value = "token", required = false) String token)
                            throws InterruptedException, ExecutionException, IOException {
        String authUrlOK = oAuth2Odnoklassniki.getAuthorizationUrl();
        model.addAttribute("authUrlOK", authUrlOK);

        String twitterUrl = twitterAuth.twitterAuth();
        model.addAttribute("twitterUrl", twitterUrl);

        model.addAttribute("authUrlVK", vkApiClient.getVkAuthUrl());
        return "login";
    }

    @RequestMapping(value = "/auth-vk", method = RequestMethod.GET)
    public String loginVk(@RequestParam(value = "code", required = false) String code) {
        vkApiClient.authUser(vkApiClient.requestVkApi(code));
        return "redirect:/";
    }

    @GetMapping("/denied")
    public String deniedPage() {
        return "denied";
    }

//    @RequestMapping(value = "/login/{token}", method = RequestMethod.POST)
//    public ResponseEntity<?> loginByToken(@RequestParam(value = "token", required = false) String token,
//                                          @RequestBody HttpServletRequest request) {
//        User user = userService.getUserByToken(token);
//        System.out.println("Try to login with token. User = " + user.toString());
//        System.out.println("Try to login with token. User email = " + user.getEmail());
//        System.out.println("Try to login with token. User password = " + user.getPassword());
//        Map<Object, Object> response = new HashMap<>();
//        response.put("name", user.getEmail());
//        response.put("password", user.getPassword());
//        System.out.println("Activating token. Step 3. Login controller");
////        return "redirect:/customer";
//        return ResponseEntity.ok(response);
//    }
}
