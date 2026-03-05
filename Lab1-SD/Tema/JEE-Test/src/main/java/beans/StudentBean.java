package beans;

public class StudentBean implements java.io.Serializable{
    private int id;
    private String nume = null;
    private String prenume = null;
    private int varsta = 0;
    public StudentBean() {
    }
    public StudentBean(int id, String nume, String prenume, int varsta) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.varsta = varsta;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNume() {
        return nume;
    }
    public void setNume(String nume) {
        this.nume = nume;
    }
    public String getPrenume() {
        return prenume;
    }
    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }
    public int getVarsta() {
        return varsta;
    }
    public void setVarsta(int varsta) {
        this.varsta = varsta;
    }
}
