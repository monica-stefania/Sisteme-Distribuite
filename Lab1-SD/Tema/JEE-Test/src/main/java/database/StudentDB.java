package database;

import beans.StudentBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDB {
    private static final String URL = "jdbc:sqlite:/home/theo/Documents/SD/Lab1-SD/Tema/JEE-Test/studenti.db";

    public StudentDB()
    {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
            stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS students(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nume TEXT NOT NULL, " +
                    "prenume TEXT NOT NULL, " +
                    "varsta INTEGER)";
            stmt.execute(sql);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public void addStudent(StudentBean student){
        String sql = "INSERT INTO students(nume, prenume, varsta) VALUES(?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, student.getNume());
            pstmt.setString(2, student.getPrenume());
            pstmt.setInt(3, student.getVarsta());
           // pstmt.setInt(4, student.getId());
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteStudent(int id){
        String sql = "DELETE FROM students WHERE id = ?";

        try {
            Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateStudent(StudentBean student){
        String sql = "UPDATE students SET nume=?, prenume=?, varsta=? WHERE id=?";

        try {
            Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, student.getNume());
            pstmt.setString(2, student.getPrenume());
            pstmt.setInt(3, student.getVarsta());
            pstmt.setInt(4, student.getId());
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<StudentBean> getAllStudents(String search) {
        List<StudentBean> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        // Daca avem un termen de cautare, modificam query-ul
        boolean hasSearch = search != null && !search.trim().isEmpty();
        if (hasSearch) {
            sql += " WHERE nume LIKE ? OR prenume LIKE ?";
        }

        try {
            Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            if (hasSearch) {
                pstmt.setString(1, "%" + search+ "%");
                pstmt.setString(2, "%" + search+ "%");
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(new StudentBean(
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getString("prenume"),
                        rs.getInt("varsta"))
                );
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}
