import com.kpi.fict.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {

    private static AdminController CONTROLLER;
    private static Statement statement;

    @BeforeAll
    @DisplayName("Авторизація адміністратора")
    static void beforeAll() throws SQLException {
        CONTROLLER = AdminController.login("root2022", "123");

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3360/time_keeping?useSSL=false", "root", "123123");
        statement = connection.createStatement();
    }

    @Test
    @DisplayName("Список всіх користувачів")
    public void getUsers() throws SQLException {
        List<User> users = CONTROLLER.get(User.class);

        ResultSet set = statement.executeQuery("SELECT COUNT(*) FROM users");
        set.next();
        int expectedCount = set.getInt(1);

        assertEquals(expectedCount, users.size());
    }

    @Test
    @DisplayName("Список всіх активностей")
    public void getActivities() throws SQLException {
        List<Activity> activities = CONTROLLER.get(Activity.class);

        ResultSet set = statement.executeQuery("SELECT COUNT(*) FROM activities");
        set.next();
        int expectedCount = set.getInt(1);

        assertEquals(expectedCount, activities.size());
    }

    @Test
    @DisplayName("Список всіх активностей користувача")
    public void getActivitiesByUser() throws Exception {
        List<Activity> activities = CONTROLLER.info("Kostyantyn");

        ResultSet set = statement.executeQuery("SELECT COUNT(*) FROM activities WHERE user_id = 1");
        set.next();
        int expectedCount = set.getInt(1);

        assertEquals(expectedCount, activities.size());
    }

    @Test
    @DisplayName("Сортування активностей за тривалістю")
    public void sortActivitiesByDuration() {
        List<Activity> actualActivities = CONTROLLER.get(Activity.class);

        List<Activity> expectedActivities = actualActivities
                .stream()
                .sorted(Comparator.comparingInt(Activity::getDuration))
                .toList();

        CONTROLLER.sortActivities(actualActivities, "duration");

        assertIterableEquals(expectedActivities, actualActivities);
    }

    @Test
    @DisplayName("Сортування активностей за категоріями")
    public void sortActivitiesByCategory() {
        List<Activity> actualActivities = CONTROLLER.get(Activity.class);

        List<Activity> expectedActivities = actualActivities
                .stream()
                .sorted(Comparator.comparing(Activity::getCategory))
                .toList();

        CONTROLLER.sortActivities(actualActivities, "category");

        assertIterableEquals(expectedActivities, actualActivities);
    }

    @Test
    @DisplayName("Сортування активностей за користувачами")
    public void sortActivitiesByUser() {
        List<Activity> actualActivities = CONTROLLER.get(Activity.class);

        List<Activity> expectedActivities = actualActivities
                .stream()
                .sorted(Comparator.comparing(Activity::getUser))
                .toList();

        CONTROLLER.sortActivities(actualActivities, "user");

        assertIterableEquals(expectedActivities, actualActivities);
    }

    @Test
    @DisplayName("Фльтрування активностей за користувачем")
    public void filterActivitiesByUser() {
        String userLogin = "Olga";
        List<Activity> activities = CONTROLLER.get(Activity.class);

        List<Activity> expectedActivities = activities
                .stream()
                .filter(act -> act.getUser().getLogin().equals(userLogin))
                .toList();

        List<Activity> actualActivities = CONTROLLER.filterActivities(activities, FilterType.USER, userLogin);

        assertIterableEquals(expectedActivities, actualActivities);
    }

    @Test
    @DisplayName("Фльтрування активностей за категорією")
    public void filterActivitiesByCategory() {
        String categoryType = "Шопінг";
        List<Activity> activities = CONTROLLER.get(Activity.class);

        List<Activity> expectedActivities = activities
                .stream()
                .filter(act -> act.getCategory().getType().equals(categoryType))
                .toList();

        List<Activity> actualActivities = CONTROLLER.filterActivities(activities, FilterType.CATEGORY, categoryType);

        assertIterableEquals(expectedActivities, actualActivities);
    }

    @Test
    @DisplayName("Список всіх категорій")
    public void getCategories() throws SQLException {
        List<Category> categories = CONTROLLER.get(Category.class);

        ResultSet set = statement.executeQuery("SELECT COUNT(*) FROM categories");
        set.next();
        int expectedCount = set.getInt(1);

        assertEquals(expectedCount, categories.size());
    }

    @Test
    @DisplayName("Передача невідомого класу контролеру")
    public void throwExceptionIfUnknownClass() {
        assertThrows(IllegalArgumentException.class, () -> CONTROLLER.get(Object.class));
    }

    @Test
    @DisplayName("Створення нового користувача")
    public void addUser() throws SQLException {
        ResultSet set = statement.executeQuery("SELECT COUNT(*) FROM users");
        set.next();
        int oldCount = set.getInt(1);

        CONTROLLER.addUser("Test user", "12345");
        List<User> users = CONTROLLER.get(User.class);

        int difference = users.size() - oldCount;
        CONTROLLER.deleteUser("Test user");

        assertEquals(1, difference);
    }

    @Test
    @DisplayName("Створення нової категорії")
    public void addCategory() throws SQLException {
        ResultSet set = statement.executeQuery("SELECT COUNT(*) FROM categories");
        set.next();
        int oldCount = set.getInt(1);

        CONTROLLER.addCategory("Test category");
        List<Category> categories = CONTROLLER.get(Category.class);

        int difference = categories.size() - oldCount;
        CONTROLLER.deleteCategory("Test category");

        assertEquals(1, difference);
    }

    @Test
    @DisplayName("Підтвердження активності")
    public void confirmActivity() throws Exception {
        int lastIndex = CONTROLLER.get(Activity.class)
                .stream()
                .max(Comparator.comparingInt(Activity::getId))
                .get()
                .getId();

        statement.executeUpdate(String.format("INSERT INTO activities values (%d, 1, 1, 5, 0);", ++lastIndex));
        CONTROLLER.confirmActivity(lastIndex);

        int finalLastIndex = lastIndex;
        Activity activity = CONTROLLER.get(Activity.class)
                .stream()
                .filter(act -> act.getId() == finalLastIndex)
                .findAny()
                .get();

        boolean isConfirmed = activity.getConfirmed();

        assertTrue(isConfirmed);
    }
}
