import ejb.StudentEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class MonitorThread extends Thread{
    private int minVarsta, maxVarsta;
    //private int minId, maxId;

    private static final List<String> alarms = new ArrayList<String>();
    private volatile boolean isRunning = true;

    public MonitorThread(int minVarsta, int maxVarsta)
    {
        this.minVarsta = minVarsta;
        this.maxVarsta = maxVarsta;
        //this.minId = minId;
        //this.maxId = maxId;
    }
    public static List<String> getAlarms() {
        return alarms;
    }
    public void opreste() {
        this.isRunning = false;
    }
    @Override
    public void run()
    {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        while(isRunning) {
            try {
                EntityManager em = factory.createEntityManager();
                alarms.clear();

                TypedQuery<StudentEntity> query = em.createQuery("select student from StudentEntity student", StudentEntity.class);
                List<StudentEntity> results = query.getResultList();
                for (StudentEntity student : results) {
                    if (student.getVarsta() > maxVarsta || student.getVarsta() < minVarsta) {
                        alarms.add("Studentul " + student.getNume() + " " + student.getPrenume() + " trebuie sa aiba varsta in intervalul [" + minVarsta + ", " + maxVarsta + "], iar acesta are varsta: " + student.getVarsta());
                    }
                    /*
                    if (student.getId() > maxId || student.getId() < minId) {
                        alarms.add("Studentul " + student.getNume() + " " + student.getPrenume() + " trebuie sa aiba ID-ul in intervalul [" + minId + ", " + maxId + "], iar acesta are id-ul: " + student.getId());
                    }

                     */
                }
                em.close();
                //factory.close();

                Thread.sleep(5000);
            }
            catch (Exception e){
                e.printStackTrace();
                factory.close();
                break;
            }
        }
        factory.close();
    }
}
