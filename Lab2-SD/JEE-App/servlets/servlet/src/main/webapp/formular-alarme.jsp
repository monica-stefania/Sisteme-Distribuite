<html xmlns:jsp="http://java.sun.com/JSP/Page">
	<head>
		<title>Formular parametrii alarmare</title>
		<meta charset="UTF-8" />
	</head>
	<body>
		<h3>Formular parametrii alarmare</h3>
		Introduceti datele:
		<form action="./alarm-db" method="post">
			Varsta minima: <input type="text" name="minVarsta" />
			<br />
			Varsta maxima: <input type="text" name="maxVarsta" />
			<br />
			<br />
			<button type="submit" name="submit">Trimite</button>
		</form>
	</body>
</html>