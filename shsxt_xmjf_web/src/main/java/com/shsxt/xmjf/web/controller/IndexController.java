package com.shsxt.xmjf.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：pp
 * @date ：Created in 2019/4/9 16:23
 */
@Controller
public class IndexController {
    @RequestMapping("index")
    public String index(HttpServletRequest request){
        request.setAttribute("ctx",request.getContextPath());
        return "index";
    }

    @RequestMapping("login")
    public  String login(HttpServletRequest request){
        request.setAttribute("ctx",request.getContextPath());
        return "login";
    }

    @RequestMapping("quickLogin")
    public  String quickLogin(HttpServletRequest request){
        request.setAttribute("ctx",request.getContextPath());
        return "quickLogin";
    }


    @RequestMapping("register")
    public  String register(HttpServletRequest request){
        request.setAttribute("ctx",request.getContextPath());
        return "register";
    }


}
