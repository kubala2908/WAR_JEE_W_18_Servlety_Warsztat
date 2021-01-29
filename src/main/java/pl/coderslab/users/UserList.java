package pl.coderslab.users;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UserList", value = "/user/list")
public class UserList extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //response.getWriter().append("<h1>Hello world.</h1>");
        UserDao userDao = new UserDao();
        request.setAttribute("users", userDao.findAll());
        getServletContext().getRequestDispatcher("/users/list.jsp").forward(request, response);
        //response.getWriter().append(userDao.findAll()[4].toString());
    }
}
