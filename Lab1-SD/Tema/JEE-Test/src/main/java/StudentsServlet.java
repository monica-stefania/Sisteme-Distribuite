import beans.StudentBean;
import database.StudentDB;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class StudentsServlet extends HttpServlet {
    private StudentDB studentDB;

    @Override
    public void init() throws ServletException {
        studentDB = new StudentDB();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String search = request.getParameter("search");

        List<StudentBean> lista = studentDB.getAllStudents(search);

        request.setAttribute("listaStudenti", lista);
        if(search != null)
        {
            request.getRequestDispatcher("/lista-rezultata.jsp").forward(request, response);
        }
        else
        {
            request.getRequestDispatcher("/lista.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            StudentBean newStudent = new StudentBean(
                    0,
                    request.getParameter("nume"),
                    request.getParameter("prenume"),
                    Integer.parseInt(request.getParameter("varsta"))
            );
            studentDB.addStudent(newStudent);

        } else if ("update".equals(action)) {
            StudentBean updatedStudent = new StudentBean(
                    Integer.parseInt(request.getParameter("id")),
                    request.getParameter("nume"),
                    request.getParameter("prenume"),
                    Integer.parseInt(request.getParameter("varsta"))
            );
            studentDB.updateStudent(updatedStudent);

        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            studentDB.deleteStudent(id);
        }
        response.sendRedirect("./list-students");
    }
}
