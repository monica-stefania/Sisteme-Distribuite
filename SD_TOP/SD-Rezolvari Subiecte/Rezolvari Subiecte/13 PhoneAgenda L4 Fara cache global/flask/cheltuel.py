from flask import Flask, render_template, request

from serviciu_cheltuieli import CheltuieliService

app = Flask(__name__)

serv = CheltuieliService()

@app.route("/")
def hello():
	return draw_page()

@app.route("/cheltuie", methods=["POST"])
def cheltuie():
	serv.cheltuie(int(request.form["amount"]), request.form["who"])
	return draw_page()

@app.route("/depune", methods=["POST"])
def depunde():
	serv.depunde(int(request.form["amount"]), request.form["who"])
	return draw_page()

def draw_page():
	return render_template("index.html", bani_mama=serv.get_bani_mama(), bani_tata=serv.get_bani_tata(), bani_copil=serv.get_bani_copil())

if __name__ == "__main__":
	app.run()