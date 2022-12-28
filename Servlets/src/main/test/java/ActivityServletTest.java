import com.kpi.fict.model.Activity;
import com.kpi.fict.model.AdminController;
import com.kpi.fict.servlet.ActivityServlet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class ActivityServletTest {
    private static final String JSP_PATH = "/WEB-INF/view/activities.jsp";

    private static ActivityServlet servlet;

    @BeforeAll
    static void initServlet() throws ServletException {
        servlet = new ActivityServlet();
        servlet.init();
    }

    @Test
    @DisplayName("Авторизований адміністратор заходить в адмін-панель")
    public void whenAdminIsAuthorizedReturnAdminPanel() throws ServletException, IOException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(JSP_PATH)).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Підтвердження активності")
    public void confirmActivity() throws IOException, SQLException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final BufferedReader reader = mock(BufferedReader.class);

        AdminController controller = servlet.getController();
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3360/time_keeping?useSSL=false", "root", "123123");
        Statement statement = connection.createStatement();

        int activityId = servlet.getController()
                .get(Activity.class)
                .stream()
                .max(Comparator.comparingInt(Activity::getId))
                .get()
                .getId();

        statement.executeUpdate(String.format("INSERT INTO activities VALUES (%d, 1, 1, 1, 0)", ++activityId));

        when(reader.readLine()).thenReturn(String.valueOf(activityId));
        when(request.getReader()).thenReturn(reader);

        servlet.doPut(request, response);

        Activity activity = controller
                .get(Activity.class)
                .stream()
                .max(Comparator.comparingInt(Activity::getId))
                .get();

        controller.deleteActivity(activityId);

        boolean isConfirmed = activity.getConfirmed();
        assertTrue(isConfirmed);
    }
}
