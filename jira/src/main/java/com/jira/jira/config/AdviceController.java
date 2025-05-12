package com.jira.jira.config;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class AdviceController {
    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return (uri != null && !uri.isEmpty()) ? uri.replaceAll("/$", "") : "/";
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

    // Add the request object explicitly to the context
    @ModelAttribute("request")
    public HttpServletRequest addRequestToContext(HttpServletRequest request) {
        return request;
    }
}