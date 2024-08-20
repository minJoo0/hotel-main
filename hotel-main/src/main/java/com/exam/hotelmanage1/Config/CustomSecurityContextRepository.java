package com.exam.hotelmanage1.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

@Log4j2
public class CustomSecurityContextRepository implements SecurityContextRepository {
    private static final String SPRING_SECURITY_CONTEXT_ADMIN = "SPRING_SECURITY_CONTEXT_ADMIN";
    private static final String SPRING_SECURITY_CONTEXT_USER = "SPRING_SECURITY_CONTEXT_USER";

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        return new DeferredSecurityContext() {
            private boolean generated = false;
            private SecurityContext context;

            @Override
            public boolean isGenerated() {
                return generated;
            }

            @Override
            public SecurityContext get() {
                if (context == null) {
                    HttpSession session = request.getSession(false);
                    context = SecurityContextHolder.createEmptyContext();

                    if (session != null) {
                        String path = request.getRequestURI();
                        if (path.startsWith("/admin") || path.startsWith("/manager") || path.startsWith("/api") || path.startsWith("/store"))  {
                            context = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT_ADMIN);
                            log.info("Admin SecurityContext Loaded: {}", context);
                            if (context == null) {
                                context = SecurityContextHolder.createEmptyContext();
                                generated = true;
                            }
                        } else if (path.startsWith("/user")) {
                            context = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT_USER);
                            log.info("User SecurityContext Loaded: {}", context);
                            if (context == null) {
                                context = SecurityContextHolder.createEmptyContext();
                                generated = true;
                            }
                        }
                    }

                    if (context == null) {
                        context = SecurityContextHolder.createEmptyContext();
                        generated = true;
                    }
                }

                return context;
            }
        };
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        String path = request.getRequestURI();

        if (path.startsWith("/admin") || path.startsWith("/manager") || path.startsWith("/api") || path.startsWith("/store")) {
            session.setAttribute(SPRING_SECURITY_CONTEXT_ADMIN, context);
            log.info("Admin SecurityContext Saved: {}", context);
        } else if (path.startsWith("/user")) {
            session.setAttribute(SPRING_SECURITY_CONTEXT_USER, context);
            log.info("User SecurityContext Saved: {}", context);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String path = request.getRequestURI();

        if (session != null) {
            if (path.startsWith("/admin") || path.startsWith("/manager") || path.startsWith("/api") || path.startsWith("/store")) {
                boolean contains = session.getAttribute(SPRING_SECURITY_CONTEXT_ADMIN) != null;
                log.info("Admin SecurityContext Contains: {}", contains);
                return contains;
            } else if (path.startsWith("/user")) {
                boolean contains = session.getAttribute(SPRING_SECURITY_CONTEXT_USER) != null;
                log.info("User SecurityContext Contains: {}", contains);
                return contains;
            }
        }

        return false;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        throw new UnsupportedOperationException("loadContext is not supported, use loadDeferredContext");
    }
}