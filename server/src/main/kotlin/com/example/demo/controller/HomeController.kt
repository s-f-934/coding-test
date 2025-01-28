package com.example.demo.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Home redirection to OpenAPI api documentation
 */
@Controller
class HomeController {

    /**
     * redirect to swagger-ui.
     */
    @RequestMapping("/")
    fun index(): String = "redirect:swagger-ui.html"
}
