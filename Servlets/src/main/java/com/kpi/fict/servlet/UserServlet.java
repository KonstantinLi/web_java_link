package com.kpi.fict.servlet;

import com.kpi.fict.model.Activity;
import com.kpi.fict.model.Category;
import com.kpi.fict.model.User;
import com.kpi.fict.model.UserController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class UserServlet extends HttpServlet {

    private UserController controller;
    private static final String AUTH_PATH = "/WEB-INF/view/index.jsp";
    private static final String PERSONAL = "/WEB-INF/view/personal.jsp";
    private static final Logger LOGGER = LogManager.getLogger(ActivityServlet.class);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession();
        String role = (String) session.getAttribute("role");

        if (role.equals("admin")) {
            LOGGER.warn("Admin was trying to come in user's webpage. Redirecting to root URL...");
            resp.sendRedirect("/");
        } else {
            if (controller == null) {
                String login = (String) session.getAttribute("login");
                String password = (String) session.getAttribute("password");

                controller = UserController.login(login, password);
                if (controller == null) {
                    LOGGER.warn("User isn't authorized. Redirecting to authorization form...");
                    req.getRequestDispatcher(AUTH_PATH).forward(req, resp);
                }
            }

            User user = controller.getUser();
            List<Activity> activities = controller.getActivities();
            List<Category> categories = controller.getCategories();

            req.setAttribute("user", user);
            req.setAttribute("activities", activities);
            req.setAttribute("categories", categories);

            LOGGER.info("Rendering webpage of user " + user.getLogin());
            req.getRequestDispatcher(PERSONAL).forward(req, resp);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String categoryType = req.getParameter("category");
        int duration = Integer.parseInt(req.getParameter("duration"));

        LOGGER.info("Creating new activity of user " + session.getAttribute("login"));
        controller.addActivity(categoryType, duration);
    }

    public UserController getController() {
        return controller;
    }
}
