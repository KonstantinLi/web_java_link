import com.kpi.fict.servlet.AuthServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServletTest {

    private final static String AUTH = "/WEB-INF/view/index.jsp";
    private static final String LOGOUT = "/WEB-INF/view/logout.jsp";

    private AuthServlet servlet;

    @BeforeEach
    void initServlet() {
        servlet = new AuthServlet();
    }

    @Test
    @DisplayName("Вхід користувача на сторінку авторизації")
    public void whenNotAuthorizedUserThenReturnIndexPage() throws ServletException, IOException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("login")).thenReturn(null);
        when(request.getRequestDispatcher(AUTH)).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request, times(1)).getRequestDispatcher(AUTH);
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Вхід авторизованого користувача на сторінку виходу із системи")
    public void whenAuthorizedUserThenReturnLogOutPage() throws ServletException, IOException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("login")).thenReturn("root2022");
        when(session.getAttribute("role")).thenReturn("admin");
        when(request.getRequestDispatcher(LOGOUT)).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request, times(1)).getRequestDispatcher(LOGOUT);
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Перезавантаження сторінки авторизації у видадку некоректних даних")
    public void invalidAuthorizationData() throws ServletException, IOException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(AUTH)).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Перехід адміністратора на адмін-панель")
    public void whenAdminIsAuthorizedThenReturnAdminPanel() throws IOException, ServletException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);
        final PrintWriter pw = mock(PrintWriter.class);

        when(request.getParameter("login")).thenReturn("root2022");
        when(request.getParameter("password")).thenReturn("123");
        when(request.getParameter("isAdmin")).thenReturn("on");
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(pw);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/active");
    }

    @Test
    @DisplayName("Перехід в особистий кабінет користувача")
    public void whenUserIsAuthorizedThenReturnHomePage() throws IOException, ServletException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);
        final PrintWriter pw = mock(PrintWriter.class);

        when(request.getParameter("login")).thenReturn("Kostyantyn");
        when(request.getParameter("password")).thenReturn("4dz6hi1q");
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(pw);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/user");
    }


    @Test
    @DisplayName("Вихід користувача із системи за закриття сесії")
    public void whenUserLogOutThenClosingSession() throws ServletException, IOException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpSession session = mock(HttpSession.class);

        session.setAttribute("login", "login");
        session.setAttribute("password", "password");
        session.setAttribute("role", "role");

        when(request.getSession()).thenReturn(session);

        servlet.doPut(request, response);

        Object login = session.getAttribute("login");
        Object password = session.getAttribute("password");
        Object role = session.getAttribute("role");

        assertAll(
                () -> assertNull(login),
                () -> assertNull(password),
                () -> assertNull(role));
    }
}
