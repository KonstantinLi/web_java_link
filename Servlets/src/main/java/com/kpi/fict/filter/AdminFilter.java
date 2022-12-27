package com.kpi.fict.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);

        boolean loggedIn = session != null
                && session.getAttribute("login") != null
                && session.getAttribute("role") != null;

        if (loggedIn) {
            String role = (String) session.getAttribute("role");
            if (role.equals("admin")) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                response.sendRedirect("/");
            }
        }

    }
}
