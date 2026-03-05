import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UpdateStudentServlet extends HttpServlet {
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int id = Integer.parseInt(request.getParameter("id"));
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));

        EntityManagerFactory factory =   Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        Query query = em.createQuery("UPDATE StudentEntity s SET s.nume = :nume, s.prenume = :prenume, s.varsta = :varsta WHERE s.id = :id");
        query.setParameter("nume", nume);
        query.setParameter("prenume", prenume);
        query.setParameter("varsta", varsta);
        query.setParameter("id", id);
        query.executeUpdate();
        transaction.commit();

        em.close();
        factory.close();
        response.sendRedirect("./fetch-student-list");
    }
}
