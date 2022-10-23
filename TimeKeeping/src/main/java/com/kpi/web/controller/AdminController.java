package com.kpi.web.controller;

import com.kpi.web.model.Activity;
import com.kpi.web.model.Category;
import com.kpi.web.model.User;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Stream;

public class AdminController {
    private static final SessionFactory SESSION_FACTORY = HibernateUtil.getSessionFactory();
    private static final CriteriaBuilder BUILDER = SESSION_FACTORY.getCriteriaBuilder();

    private AdminController() {
    }

    public static AdminController login(String login, String password) {
        AdminController controller = null;

        try (Session session = SESSION_FACTORY.openSession()) {
            String hql = "SELECT login, password FROM Admin" +
                    " WHERE login = '" + login + "' AND password = '" + password + "'";

            if (session.createQuery(hql).list().size() != 0) {
                controller = new AdminController();
            } else {
                throw new Exception();
            }

        } catch (Exception ignored) {}

        return controller;
    }

    public <T> List<T> get(Class<T> clazz) {
        try (Session session = SESSION_FACTORY.openSession()) {
            List<Class> entities = Arrays.asList(User.class, Category.class, Activity.class);

            if (entities.contains(clazz)) {
                CriteriaQuery<T> query = BUILDER.createQuery(clazz);
                Root<T> root = query.from(clazz);
                query.select(root);

                return session.createQuery(query).getResultList();
            } else {
                System.err.printf("No such entity \"%s\"%n", clazz.getSimpleName());
                return null;
            }
        }
    }

    public void sortActivities(List<Activity> activities, String field) {
        sortActivities(activities, field, SortingType.ASCENDING);
    }

    public void sortActivities(List<Activity> activities, String field, SortingType sortingType) {
        switch (field) {
            case "category" -> sortActivitiesByCategory(activities);
            case "user" -> sortActivitiesByUser(activities);
            case "duration" -> sortActivitiesByDuration(activities);
            default -> throw new RuntimeException(String.format("Activity doesn't have the field \"%s\"%n", field));
        }

        if (sortingType == SortingType.DESCENDING) {
            Collections.reverse(activities);
        }
    }

    private void sortActivitiesByCategory(List<Activity> activities) {
        activities.sort(Comparator.comparing(Activity::getCategory));
    }

    private void sortActivitiesByUser(List<Activity> activities) {
        activities.sort(Comparator.comparing(Activity::getUser));
    }

    private void sortActivitiesByDuration(List<Activity> activities) {
        activities.sort(Comparator.comparingInt(Activity::getDuration));
    }

    private List<Activity> filterActivities(List<Activity> activities, FilterType filterType, String value) {
        Stream<Activity> stream = activities.stream();

        return switch (filterType) {
            case USER -> stream.filter(activity -> activity.getUser().getLogin().equals(value)).toList();
            case CATEGORY -> stream.filter(activity -> activity.getCategory().getType().equals(value)).toList();
        };
    }

    public void addUser(String login, String password) throws RuntimeException {
        Session session = SESSION_FACTORY.openSession();

        User user = new User();
        user.setLogin(login);
        user.setPassword(password);

        Transaction transaction = session.beginTransaction();
        session.save(user);

        transaction.commit();
        session.close();
    }

    public void addCategory(String type) throws RuntimeException {
        Session session = SESSION_FACTORY.openSession();

        Category category = new Category();
        category.setType(type);

        Transaction transaction = session.beginTransaction();
        session.persist(category);

        transaction.commit();
        session.close();
    }

    public void confirmActivity(int id) throws Exception {
        try (Session session = SESSION_FACTORY.openSession()) {
            Transaction transaction = session.beginTransaction();

            CriteriaQuery<Activity> criteriaQuery = BUILDER.createQuery(Activity.class);
            Root<Activity> root = criteriaQuery.from(Activity.class);

            criteriaQuery.select(root).where(BUILDER.equal(root.get("id"), id));

            Activity activity = session.createQuery(criteriaQuery).getSingleResult();
            activity.setConfirmed(true);

            session.update(activity);
            transaction.commit();
        }
    }

    public void confirmActivity(String userName, RecordType recordType) throws Exception {
        try (Session session = SESSION_FACTORY.openSession()) {
            Transaction transaction = session.beginTransaction();

            switch (recordType) {
                case LAST -> {
                    String sql = "SELECT MAX(activities.id) FROM activities " +
                            "JOIN users ON activities.user_id = users.id " +
                            "WHERE users.login = :login";

                    Query query = session.createNativeQuery(sql).setParameter("login", userName);

                    Integer lastId = (Integer) query.getSingleResult();
                    Activity activity = session.get(Activity.class, lastId);
                    activity.setConfirmed(true);

                    session.update(activity);
                }
                case ALL -> {
                    List<Activity> activitiesByUser = filterActivities(get(Activity.class), FilterType.USER, userName);
                    for (Activity activity1 : activitiesByUser) {
                        activity1.setConfirmed(true);
                        session.update(activity1);
                    }
                }
            }

            transaction.commit();
        }
    }

    public List<Activity> info(String login) throws Exception {
        try (Session session = SESSION_FACTORY.openSession()) {
            Transaction transaction = session.beginTransaction();

            CriteriaQuery<Activity> criteriaQuery = BUILDER.createQuery(Activity.class);
            Root<Activity> root = criteriaQuery.from(Activity.class);

            criteriaQuery.select(root).where(BUILDER.equal(root.get("user").get("login"), login));
            List<Activity> activitiesByUser = session.createQuery(criteriaQuery).list();

            transaction.commit();

            return activitiesByUser;
        }
    }
}
