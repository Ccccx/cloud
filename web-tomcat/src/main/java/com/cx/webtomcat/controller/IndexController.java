package com.cx.webtomcat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-28 16:51
 */
@Controller
public class IndexController {

    @GetMapping
    public String goIndex() {
        return "index";
    }
}