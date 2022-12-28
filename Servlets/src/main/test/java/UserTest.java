import com.kpi.fict.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private static UserController CONTROLLER;
    private static Statement statement;

    @BeforeAll
    @DisplayName("Авторизація адміністратора")
    static void beforeAll() throws SQLException {
        CONTROLLER = UserController.login("Kostyantyn", "4dz6hi1q");

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3360/time_keeping?useSSL=false", "root", "123123");
        statement = connection.createStatement();
    }

    @Test
    @DisplayName("Активності користувача Kostyantyn")
    public void getPersonalActivities() throws SQLException {
        List<Activity> activities = CONTROLLER.getActivities();

        ResultSet set = statement.executeQuery("SELECT COUNT(*) FROM activities WHERE user_id = 1");
        set.next();
        int expectedCount = set.getInt(1);

        assertEquals(expectedCount, activities.size());
    }

    @Test
    @DisplayName("Отримати об'єкт категорії \"Спорт\"")
    public void getCategory() {
        Category category = CONTROLLER.findCategory("Спорт");
        assertAll(
                () -> assertNotNull(category),
                () -> assertEquals("Спорт", category.getType()));
    }

    @Test
    @DisplayName("Пошук невідомої категорії")
    public void getUnknownCategory() {
        Category category = CONTROLLER.findCategory("Unknown");
        assertNull(category);
    }

    @Test
    @DisplayName("Список всіх категорій")
    public void getAllCategories() throws SQLException {
        List<Category> categories = CONTROLLER.getCategories();

        ResultSet set = statement.executeQuery("SELECT COUNT(*) FROM categories");
        set.next();
        int expectedCount = set.getInt(1);

        assertEquals(expectedCount, categories.size());
    }

    @Test
    @DisplayName("Створення нової активності користувача Kostyantyn")
    public void addActivity() throws Exception {
        ResultSet set = statement.executeQuery("SELECT COUNT(*) FROM activities WHERE user_id = 1");
        set.next();
        int oldCount = set.getInt(1);

        CONTROLLER.addActivity("Навчання", 5);
        List<Activity> activities = CONTROLLER.getActivities();

        int difference = activities.size() - oldCount;

        CONTROLLER.deleteActivity("Навчання", RecordType.LAST);

        assertEquals(1, difference);
    }
}
