from flask import Flask, redirect, url_for, request, render_template, session
import requests

app = Flask(__name__)
app.secret_key = "super_secret_key_012"

AUTH_URL = "http://localhost:8081/auth"
EXPENSE_URL = "http://localhost:8083/expenses"
@app.route('/')
def index():
    return redirect(url_for('login'))

@app.route('/login', methods=['GET', 'POST'])
def login():
    error = None
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']

        data = {
            "username": username,
            "password": password
        }

        response = requests.post(f"{AUTH_URL}/login", json=data).json()
        if response["success"]:
            session['memberId'] = response["memberId"]
            session['username'] = response["username"]
            return redirect(url_for('expenses'))
        else:
            error = data.get("message", "Register error")

    return render_template('login.html', error=error)

@app.route('/register', methods=['GET', 'POST'])
def register():
    error = None
    success = None
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        firstName = request.form['firstName']
        lastName = request.form['lastName']

        data = {
            "username": username,
            "password": password,
            "firstName": firstName,
            "lastName": lastName
        }
        try:
            response = requests.post(f"{AUTH_URL}/register", json=data).json()

            if response["success"]:
                success = "New account successfully created! Now you can log in."
            else:
                error = response.get("message", "Register error")
        except Exception:
            error = data.get("message", "AuthService is not available")
    return render_template('register.html', error=error, success=success)

@app.route("/expenses")
def expenses():
    if "memberId" not in session:
        return redirect(url_for("login"))

    memberId = session["memberId"]
    expenses_list = []
    error = None

    try:
        resp = requests.get(f"{EXPENSE_URL}/member/{memberId}")
        if resp.status_code == 200:
            expenses_list = resp.json().get("expenses", [])
    except Exception:
        error = "ExpenseService it's not available."

    return render_template("expenses.html", expenses=expenses_list, error=error)


@app.route("/expenses/add", methods=["GET", "POST"])
def add_expense():
    if "memberId" not in session:
        return redirect(url_for("login"))

    error   = None
    success = None
    if request.method == "POST":
        payload = {
            "memberId":    session["memberId"],
            "description": request.form["description"],
            "amount":      float(request.form["amount"]),
            "category":    request.form["category"],
            "date":        request.form["date"]
        }
        try:
            resp = requests.post(EXPENSE_URL, json=payload)
            data = resp.json()
            if data.get("success"):
                return redirect(url_for("expenses"))
            else:
                error = data.get("message", "Add error.")
        except Exception:
            error = "ExpenseService it's not available."

    return render_template("add_expense.html", error=error, success=success)

@app.route("/expenses/edit/<int:expense_id>", methods=["GET", "POST"])
def edit_expense(expense_id):
    if "memberId" not in session:
        return redirect(url_for("login"))

    error      = None
    expense    = None

    try:
        resp = requests.get(f"{EXPENSE_URL}/{expense_id}")
        if resp.status_code == 200:
            expense = resp.json().get("expense")
        else:
            return redirect(url_for("expenses"))
    except Exception:
        return redirect(url_for("expenses"))

    if request.method == "POST":
        payload = {
            "memberId":    session["memberId"],
            "description": request.form["description"],
            "amount":      float(request.form["amount"]),
            "category":    request.form["category"],
            "date":        request.form["date"]
        }
        try:
            resp = requests.put(f"{EXPENSE_URL}/{expense_id}", json=payload)
            data = resp.json()
            if data.get("success"):
                return redirect(url_for("expenses"))
            else:
                error = data.get("message", "Updated error")
        except Exception:
            error = "ExpenseService it's not available."

    return render_template("edit_expense.html", expense=expense, error=error)
@app.route("/expenses/delete/<int:expense_id>")
def delete_expense(expense_id):
    if "memberId" not in session:
        return redirect(url_for("login"))
    try:
        requests.delete(f"{EXPENSE_URL}/{expense_id}")
    except Exception:
        pass
    return redirect(url_for("expenses"))

if __name__ == '__main__':
    app.run(debug=True)