import requests
from flask import Flask, session, url_for, render_template, redirect, request, flash

app = Flask(__name__)
app.secret_key = "cheie_secreta"

MAGAZIN_URL = "http://localhost:46304"

@app.route('/')
def index():
    if 'email' in session:
        return redirect(url_for('magazin'))
    return render_template('index.html')

@app.route('/register-view')
def register_view():
    return render_template("register.html")

@app.route('/login', methods=['POST'])
def login():
    date_login = {
        "email": request.form['email'],
        "parola": request.form['parola']
    }
    try:
        response = requests.post(f"{MAGAZIN_URL}/login", data=date_login)
        if response.status_code == 200:
            client_data = response.json()
            session['nume'] = client_data['nume']
            session['prenume'] = client_data['prenume']
            session['email'] = client_data['email']
            session['adresaLivrare'] = client_data['adresaLivrare']
            return redirect(url_for('magazin'))
        else:
            flash("Email sau parolă incorectă!")
    except requests.exceptions.ConnectionError:
        flash("Eroare de conexiune cu serverul Spring Boot.")

    return redirect(url_for('index'))


@app.route('/register', methods=['POST'])
def register():
    date_inregistrare = {
        "nume": request.form['nume'],
        "prenume": request.form['prenume'],
        "email": request.form['email'],
        "parola": request.form['parola'],
        "adresaLivrare": request.form['adresaLivrare'],
        "adresaFacturare": request.form['adresaFacturare'],
        "telefon": request.form['telefon']
    }
    try:
        response = requests.post(f"{MAGAZIN_URL}/register", data=date_inregistrare)
        if response.status_code == 200:
            flash("Cont creat cu succes! Te poți autentifica acum.")
            return redirect(url_for('index'))
        else:
            flash(response.text)
    except requests.exceptions.ConnectionError:
        flash("Eroare de conexiune cu serverul Spring Boot.")

    return render_template('register.html')

@app.route('/magazin')
def magazin():
    if 'email' not in session:
        return redirect(url_for('index'))
    try:
        response = requests.get(f"{MAGAZIN_URL}/produse")
        if response.status_code == 200:
            produse = response.json()
        else:
            produse = []
    except:
        produse = ["Eroare conexiune DB"]

    return render_template('magazin.html', produse=produse)

@app.route('/trimite_comanda', methods=['POST'])
def trimite_comanda():
    if 'email' not in session:
        return redirect(url_for('index'))

    date_comanda = {
        "email": session['email'],
        "produs": request.form['produs'],
        "cantitate": request.form['cantitate'],
        "adresaLivrare": request.form['adresaLivrare']
    }

    try:
        response = requests.post(f"{MAGAZIN_URL}/comanda", data=date_comanda)
        if response.status_code == 200:
            flash("Comanda a fost trimisă cu succes!")
            session['adresaLivrare'] = request.form['adresaLivrare']
        else:
            flash("Eroare la procesarea comenzii.")
    except requests.exceptions.ConnectionError:
        flash("Eroare de conexiune cu Spring Boot.")

    return redirect(url_for('magazin'))

@app.route('/logout')
def logout():
    session.clear()
    flash("Ai fost deconectat cu succes.")
    return redirect(url_for('index'))

if __name__ == '__main__':
    app.run(port=5001, debug=True)