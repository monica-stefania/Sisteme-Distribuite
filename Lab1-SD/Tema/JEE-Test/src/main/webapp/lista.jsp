<%@ page import="java.util.List" %>
<%@ page import="beans.StudentBean" %>
<html>
<body>
    <h1>Gestiune Studenti</h1>

    <form action="./list-students" method="get">
        Cauta student: <input type="text" name="search">
        <button type="submit">Cauta</button>
        <a href="./list-students">Arata toti</a>
    </form>

    <h3>Adauga un student nou</h3>
        <form action="./list-students" method="post">
            <input type="hidden" name="action" value="add">

            Nume: <input type="text" name="nume"> <br><br>
            Prenume: <input type="text" name="prenume"> <br><br>
            Varsta: <input type="number" name="varsta"> <br><br>
            <button type="submit">Adauga Student</button>
        </form>

    <ul>
            <%
            List<StudentBean> lista = (List<StudentBean>) request.getAttribute("listaStudenti");
            if (lista != null) {
                for (StudentBean s : lista) {
            %>
            <li>
                <form action="./list-students" method="post" style="display:inline;">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="<%= s.getId() %>">

                    Nume: <input type="text" name="nume" value="<%= s.getNume() %>">
                    Prenume: <input type="text" name="prenume" value="<%= s.getPrenume() %>">
                    Varsta: <input type="number" name="varsta" value="<%= s.getVarsta() %>">

                    <button type="submit">Actualizeaza</button>
                </form>

                <form action="./list-students" method="post" style="display:inline;">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="<%= s.getId() %>">
                    <button type="submit" style="color:red;">Sterge</button>
                </form>
            </li>
            <%  } } %>
        </ul>

</body>
</html>