package com.example.demo.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping(value = "/web03")
    public String redirectRoot() {
        return "forward:/web03/index.html";
    }

    @RequestMapping(value = "/web03/")
    public String redirectRootSlash() {
        return "forward:/web03/index.html";
    }

    @RequestMapping(value = "/web03/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/web03/index.html";
    }

    @RequestMapping(value = "/web03/{path1:[^\\.]*}/{path2:[^\\.]*}")
    public String redirectDeep() {
        return "forward:/web03/index.html";
    }
}