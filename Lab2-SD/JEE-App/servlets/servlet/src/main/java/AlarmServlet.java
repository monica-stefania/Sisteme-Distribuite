import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AlarmServlet extends HttpServlet{
    private MonitorThread monitorThread;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        StringBuilder message = new StringBuilder();
        message.append("<h2>Alarme monitorizare baza de date: </h2>");
        if (monitorThread == null) {
            message.append("<p>Monitorizarea nu a fost inca pornita. Va rugam sa setati parametrii din formular.</p>");
        }
        else if(monitorThread.getAlarms().isEmpty())
            message.append("<p>Nu exista alarme</p>");
        else
        {
            message.append("<br><ul>");
            for(String msg : monitorThread.getAlarms())
            {
                message.append("<li>" + msg + "</li>");
            }
            message.append("</ul>");
        }
        message.append("<br /><a href='./'>Inapoi la meniul principal</a>");
        response.setContentType("text/html");
        response.getWriter().println(message);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        int minVarsta = Integer.parseInt(request.getParameter("minVarsta"));
        int maxVarsta = Integer.parseInt(request.getParameter("maxVarsta"));

        if(monitorThread != null)
            monitorThread.opreste();
        monitorThread = new MonitorThread(minVarsta, maxVarsta);
        monitorThread.start();

        response.setContentType("text/html");
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>Monitorizarea a pornit cu succes!</h2>");
        response.getWriter().println("<p>S-au setat limitele: " + minVarsta + " - " + maxVarsta + " ani.</p>");

        response.getWriter().println("<br /><a href='./alarm-db'>Verifica alarmele aici</a>");
        response.getWriter().println("<br /><br /><a href='./'>Inapoi la meniul principal</a>");
        response.getWriter().println("</body></html>");
    }

    @Override
    public void destroy() {
        if (monitorThread != null) {
            monitorThread.opreste();
        }
    }
}
