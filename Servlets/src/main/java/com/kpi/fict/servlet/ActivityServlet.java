package com.kpi.fict.servlet;

import com.kpi.fict.model.Activity;
import com.kpi.fict.model.AdminController;
import com.kpi.fict.model.Category;
import com.kpi.fict.model.User;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class ActivityServlet extends HttpServlet {

    private static final String JSP_PATH = "/WEB-INF/view/activities.jsp";
    private AdminController controller;
    private static final Logger LOGGER = LogManager.getLogger(ActivityServlet.class);
    private List<Activity> activities;
    private List<User> users;
    private List<Category> categories;

    @Override
    public void init() throws ServletException {
        controller = AdminController.login("root2022", "123");
        LOGGER.info("Initialization ActivityServlet");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        activities = controller.get(Activity.class);
        users = controller.get(User.class);
        categories = controller.get(Category.class);

        String sortParam = req.getParameter("sort");
        String filterParam = req.getParameter("filter");

        if (sortParam != null) {
            session.setAttribute("sort", sortParam);
        }

        if (filterParam != null) {
            session.setAttribute("filter", filterParam);
        }

        sortParam = (String) session.getAttribute("sort");
        filterParam = (String) session.getAttribute("filter");

        if (sortParam != null) {
            LOGGER.info("Using sorting by " + sortParam);
            switch (sortParam) {
                case "category" -> activities = activities.stream().sorted(Comparator.comparing(o -> o.getCategory().getType())).toList();
                case "duration" -> activities = activities.stream().sorted(Comparator.comparingInt(Activity::getDuration)).toList();
            }
        }

        if (filterParam != null) {
            LOGGER.info("Using filtering by " + filterParam);
            String finalFilterParam = filterParam;
            activities = activities.stream().filter(act -> act.getCategory().getType().equals(finalFilterParam)).toList();
        }

        req.setAttribute("activities", activities);
        req.setAttribute("categories", categories);

        LOGGER.info("Rendering admin panel...");
        req.getRequestDispatcher(JSP_PATH).forward(req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int activityId = Integer.parseInt(req.getReader().readLine());

        try {
            LOGGER.info("Confirmation activity id-" + activityId);
            controller.confirmActivity(activityId);
        } catch (Exception e) {
            LOGGER.error("Confirmation activity id-" + activityId + " was failed.");
            throw new RuntimeException(e);
        }
    }

    public AdminController getController() {
        return controller;
    }
}
