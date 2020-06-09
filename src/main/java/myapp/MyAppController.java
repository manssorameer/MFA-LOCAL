package myapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class MyAppController {
    public String issuer = "MFA-TEAM";
    public String accountName= "User1";
    GoogleAuthenticator gAuth = new GoogleAuthenticator();
    public String loginStatus="";
    @GetMapping("/")
    public String process(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        String messages = (String) session.getAttribute("MY_SESSION_MESSAGES");
        String otpAuthURL =   (String) session.getAttribute("MY_OTP");;;
        if (messages == null) {


            final GoogleAuthenticatorKey key = gAuth.createCredentials();
             otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, accountName, key);

            messages=key.getKey();
        }
        model.addAttribute("otp", otpAuthURL);
        session.setAttribute("MY_OTP", otpAuthURL);
        model.addAttribute("sessionMessages", messages);
        session.setAttribute("MY_SESSION_MESSAGES", messages);
        return "Keypage.html";
    }

    @PostMapping("/keyGen")
    public String persistMessage(HttpServletRequest request) {


        request.getSession().setAttribute("MY_SESSION_MESSAGES", null);
        request.getSession().setAttribute("MY_OTP", null);
        return "redirect:/";

    }
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {


        request.getSession().setAttribute("MY_SESSION_MESSAGES", null);
        request.getSession().setAttribute("MY_OTP", null);
        return "redirect:/";

    }
    @PostMapping("/nextValidate")
    public String ValidateToken(HttpServletRequest request) {
        return "Token page.html";
    }
    @PostMapping("/validateToken")
    public String persistMessage(@RequestParam("token") String token,Model model, HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        String msgs = (String) request.getSession().getAttribute("MY_SESSION_MESSAGES");
        if (token.matches("[0-9]+") && gAuth.authorize(msgs, Integer.parseInt(token) )) {
            loginStatus = "";
            return "End page.html";
        }else{
            loginStatus = "Token is not valid";
            model.addAttribute("loginStatus", loginStatus);

            return "Token page.html";
        }
    }

}
