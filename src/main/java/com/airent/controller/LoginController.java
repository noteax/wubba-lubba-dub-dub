package com.airent.controller;

import com.airent.service.LoginService;
import com.airent.util.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(method = RequestMethod.GET, path = "/login")
    public void login(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/register")
    public void registerUser(HttpServletResponse response, String phoneNumber, String userName) {
        Objects.requireNonNull(phoneNumber);
        Objects.requireNonNull(userName);

        boolean alreadyExists = loginService.registerNewUser(PhoneNumber.normalize(phoneNumber), userName);
        if (alreadyExists) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }


    }

    @RequestMapping(method = RequestMethod.POST, path = "/rememberPassword")
    public void sendNewPassword(String phoneNumber) {
        Objects.requireNonNull(phoneNumber);
    }

}
