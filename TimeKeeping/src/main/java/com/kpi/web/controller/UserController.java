package com.kpi.web.controller;

import com.kpi.web.model.Activity;
import com.kpi.web.model.Category;
import com.kpi.web.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;

public class UserController {
    private static final SessionFactory SESSION_FACTORY = HibernateUtil.getSessionFactory();
    private static final CriteriaBuilder BUILDER = SESSION_FACTORY.getCriteriaBuilder();
    private final User user;

    private UserController(User user) {
        this.user = user;
    }

    public static UserController login(String login, String password) {
        UserController controller = null;

        try (Session session = SESSION_FACTORY.openSession()) {
            String hql = "FROM User" +
                    " WHERE login = '" + login + "' AND password = '" + password + "'";

            Query query = session.createQuery(hql);
            User user = (User) query.getSingleResult();

            controller = new UserController(user);

        } catch (Exception ex) {
            System.err.printf("Incorrect login \"%s\" or password \"%s\"%n", login, password);
        }

        return controller;
    }

    public List<Activity> getActivities() {
        try (Session session = SESSION_FACTORY.openSession()) {

            CriteriaQuery<Activity> query = BUILDER.createQuery(Activity.class);
            Root<Activity> root = query.from(Activity.class);
            query.select(root).where(BUILDER.equal(root.get("user"), user));

            return session.createQuery(query).getResultList();
        }
    }

    public List<Category> getCategories() {
        try (Session session = SESSION_FACTORY.openSession()) {

            CriteriaQuery<Category> query = BUILDER.createQuery(Category.class);
            Root<Category> root = query.from(Category.class);
            query.select(root);

            return session.createQuery(query).getResultList();
        }
    }

    public void addActivity(String categoryType, int duration) throws RuntimeException {
        Category category = findCategory(categoryType);

        try (Session session = SESSION_FACTORY.openSession()) {
            Transaction transaction = session.beginTransaction();

            if (category == null) {
                throw new RuntimeException(String.format("No such category \"%s\"%n", categoryType));
            }

            Activity activity = new Activity();
            activity.setUser(user);
            activity.setCategory(category);
            activity.setDuration(duration);

            session.save(activity);
            transaction.commit();
        }
    }

    public void deleteActivity(String categoryType, RecordType recordType) throws Exception {
        Category category = findCategory(categoryType);

        try (Session session = SESSION_FACTORY.openSession()) {
            Transaction transaction = session.beginTransaction();

            if (category == null) {
                throw new RuntimeException(String.format("No such category \"%s\"%n", categoryType));
            }

            switch (recordType) {
                case LAST:
                    String sql = "SELECT MAX(activities.id) FROM activities " +
                            "JOIN categories ON activities.category_id = categories.id " +
                            "JOIN users ON activities.user_id = users.id " +
                            "WHERE categories.type = :type AND users.login = :login";

                    Query query = session.createNativeQuery(sql)
                            .setParameter("type", categoryType)
                            .setParameter("login", user.getLogin());
                    Integer lastId = (Integer) query.getSingleResult();

                    session.createQuery("DELETE from Activity WHERE id = :id")
                            .setParameter("id", lastId)
                            .executeUpdate();
                    break;

                case ALL:
                    CriteriaDelete<Activity> criteriaDelete = BUILDER.createCriteriaDelete(Activity.class);
                    Root<Activity> root = criteriaDelete.from(Activity.class);

                    criteriaDelete.where(BUILDER.and(
                            BUILDER.equal(root.get("user"), user),
                            BUILDER.equal(root.get("category"), category)
                    ));
                    session.createQuery(criteriaDelete).executeUpdate();
            }

            transaction.commit();
        }
    }

    private Category findCategory(String categoryType) {
        try (Session session = SESSION_FACTORY.openSession()) {
            Transaction transaction = session.beginTransaction();

            CriteriaQuery<Category> criteriaQuery = BUILDER.createQuery(Category.class);
            Root<Category> root = criteriaQuery.from(Category.class);

            criteriaQuery.select(root).where(BUILDER.equal(root.get("type"), categoryType));
            Query query = session.createQuery(criteriaQuery);
            transaction.commit();

            if (query.getResultList().isEmpty()) {
                return null;
            } else {
                return (Category) query.getSingleResult();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
