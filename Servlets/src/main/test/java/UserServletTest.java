import com.kpi.fict.model.RecordType;
import com.kpi.fict.model.UserController;
import com.kpi.fict.servlet.AuthServlet;
import com.kpi.fict.servlet.UserServlet;
import org.junit.jupiter.api.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServletTest {
    private static final String PERSONAL = "/WEB-INF/view/personal.jsp";

    private static UserServlet servlet;

    @BeforeAll
    static void initServlet() {
        servlet = new UserServlet();
    }

    @Test
    @Order(1)
    @DisplayName("Перехід на сторінку виходу із системи, " +
            "коли адміністратор намагається зайти в особистий кабінет користувача")
    public void whenAdminIsAuthorizedThenReturnLogOutPage() throws ServletException, IOException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("admin");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/");
    }

    @Test
    @Order(2)
    @DisplayName("Перехід користувача на його особисту сторінку")
    public void authorizedUserComeInPersonalPage() throws ServletException, IOException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("user");
        when(session.getAttribute("login")).thenReturn("Kostyantyn");
        when(session.getAttribute("password")).thenReturn("4dz6hi1q");
        when(request.getRequestDispatcher(PERSONAL)).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    @Order(3)
    @DisplayName("Створення активності користувача")
    public void createActivity() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);

        UserController controller = servlet.getController();

        String login = "Kostyantyn";
        String categoryType = "Спорт";
        int duration = 3;

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("category")).thenReturn(categoryType);
        when(request.getParameter("duration")).thenReturn(String.valueOf(duration));
        when(request.getParameter("login")).thenReturn(login);

        int oldCount = controller.getActivities().size();
        servlet.doPost(request, response);
        int newCount = controller.getActivities().size();

        controller.deleteActivity(categoryType, RecordType.LAST);

        assertEquals(1, newCount - oldCount);
    }
}
