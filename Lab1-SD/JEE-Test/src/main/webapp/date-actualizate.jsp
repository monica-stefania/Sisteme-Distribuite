<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title> Actualizare student</title>
        <meta charset="UTF-8">
    </head>
    <body>
        <h3>Actualizare date student</h3>
        Introduceti datele despre student:
        <form action="./actualizare-student" method="post">
            Nume: <input type="text" name="nume" />
            <br />
            Prenume: <input type="text" name="prenume" />
            <br />
            Varsta: <input type="number" name="varsta" />
            <br />
            <br />
            <button type="submit" name="action" value="update">Actualizare</button>
            <button type="submit" name="action" value="delete" style="color: red;">Stergere</button>
            </form>
        </form>
    </body>
</html>