<%@ page import="java.util.List" %>
<%@ page import="beans.StudentBean" %>
<html>
<body>
    <h2>Rezultatele cautarii</h2>

    <a href="./list-students"><button>Inapoi la lista completa</button></a>

    <hr>

    <ul>
        <%
        List<StudentBean> lista = (List<StudentBean>) request.getAttribute("listaStudenti");

        if (lista == null || lista.isEmpty()) {
        %>
            <h3 style="color: red;">Nu a fost gasit niciun student cu acest nume!</h3>
            <p>Incearca sa cauti folosind un alt nume sau prenume.</p>
        <%
        } else {
            for (StudentBean s : lista) {
        %>
            <li>
                <%= s.getNume() %> <%= s.getPrenume() %> - <%= s.getVarsta() %> ani
            </li>
        <%
            }
        }
        %>
    </ul>

</body>
</html>