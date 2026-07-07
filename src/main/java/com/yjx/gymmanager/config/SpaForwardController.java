package com.yjx.gymmanager.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaForwardController {
    @RequestMapping(value = {"/", "/admin/**", "/member/**", "/login"})
    public String forward() {
        return "forward:/index.html";
    }
}
