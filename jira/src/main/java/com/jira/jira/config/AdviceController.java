package com.jira.jira.config;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class AdviceController {
    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        return request.getRequestURI() != null ? request.getRequestURI() : "";
    }

    @ModelAttribute("isHomePage")
    public boolean isHomePage(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path != null && path.equals("/");
    }
    @ModelAttribute("_csrf")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }
}