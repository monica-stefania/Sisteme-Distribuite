import beans.StudentBean;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.Year;

public class ActualizareStudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
// se citesc parametrii din cererea de tip POST

        String action = request.getParameter("action");
        File file = new File("/home/theo/Documents/SD/Lab1-SD/JEE-Test/student.xml");

        if("delete".equals(action))
        {
            if(file.exists())
            {
                file.delete();
            }
            request.getRequestDispatcher("./stergere.jsp").forward(request, response);
        }
        else if ("update".equals(action)) {
            String nume = request.getParameter("nume");
            String prenume = request.getParameter("prenume");
            int varsta = Integer.parseInt(request.getParameter("varsta"));

            // initializare serializator Jackson
            XmlMapper mapper = new XmlMapper();

            // creare bean si populare cu date
            StudentBean bean = new StudentBean();
            bean.setNume(nume);
            bean.setPrenume(prenume);
            bean.setVarsta(varsta);

            // serializare bean sub forma de string XML
            mapper.writeValue(file, bean);

            request.getRequestDispatcher("./actualizat.jsp").forward(request, response);
        }
    }
}
