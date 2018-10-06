package com.owen.web.controller;

import com.owen.base.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2018/7/9.
 */
@Controller
public class HomeController {
//    @GetMapping("/get")
//    @ResponseBody
//    public ApiResponse get(){
//        return  ApiResponse.ofMessage(200,"成功了");
//    }

    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/404")
    public String notFoundPage(){
        return  "404";
    }

    @GetMapping("/403")
    public String accessError(){
        return  "403";
    }

    @GetMapping("/500")
    public String internalError(){
        return  "500";
    }
    @GetMapping("/logout/page")
    public String logoutPage(){
        return  "logout";
    }




}
