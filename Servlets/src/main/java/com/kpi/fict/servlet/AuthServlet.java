package com.kpi.fict.servlet;

import com.kpi.fict.model.AdminController;
import com.kpi.fict.model.UserController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthServlet extends HttpServlet {

    private static final String AUTH = "/WEB-INF/view/index.jsp";
    private static final String LOGOUT = "/WEB-INF/view/logout.jsp";

    private static final Logger LOGGER = LogManager.getLogger(ActivityServlet.class);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String login = (String) session.getAttribute("login");

        if (login == null) {
            LOGGER.info("Client is not authorized. Forward to authorization form...");
            req.getRequestDispatcher(AUTH).forward(req, resp);
        } else {
            String role = (String) session.getAttribute("role");
            req.setAttribute("login", login);
            req.setAttribute("role", role);

            LOGGER.info("Client is authorized. Rendering logout page...");
            req.getRequestDispatcher(LOGOUT).forward(req, resp);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        if (!isValidAuth(req)) {
            LOGGER.warn("Invalid authorization data. Please, try again!");
            doGet(req, resp);
            return;
        }

        resp.setContentType("text/html");

        final String login = req.getParameter("login");
        final String password = req.getParameter("password");

        String isAdmin = req.getParameter("isAdmin");
        if (isAdmin != null && isAdmin.equals("on")) {
            AdminController controller = AdminController.login(login, password);
            LOGGER.info("Authorization of admin " + login);
            try (PrintWriter pw = resp.getWriter()) {
                if (controller != null) {
                    session.setAttribute("login", login);
                    session.setAttribute("password", password);
                    session.setAttribute("role", "admin");
                    resp.sendRedirect("/active");
                } else {
                    pw.println("Auth failed");
                }
            }
        } else {
            UserController controller = UserController.login(login, password);
            LOGGER.info("Authorization of user " + login);
            try (PrintWriter pw = resp.getWriter()) {
                if (controller != null) {
                    session.setAttribute("login", login);
                    session.setAttribute("password", password);
                    session.setAttribute("role", "user");
                    resp.sendRedirect("/user");
                } else {
                    pw.println("Auth failed");
                }
            }
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        session.removeAttribute("login");
        session.removeAttribute("password");
        session.removeAttribute("role");

        LOGGER.info("Logout and session closing...");
    }

    public boolean isValidAuth(final HttpServletRequest req) {
        final String login = req.getParameter("login");
        final String password = req.getParameter("password");

        return login != null && login.length() > 0 &&
                password != null && password.length() > 0;
    }
}
