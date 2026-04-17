from tkinter import *
from tkinter import ttk
import threading
import socket

HOST = "localhost"

def resolve_question(port_s, formatted_message):
    # creare socket TCP
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # incercare de conectare catre microserviciul Teacher
    try:
        sock.connect((HOST, int(port_s)))

        # transmitere intrebare - se deleaga intrebarea catre microserviciu
        sock.send(bytes(formatted_message + "\n", "utf-8"))

        # primire raspuns -> microserviciul Teacher foloseste coregrafia de microservicii pentru a trimite raspunsul inapoi
        response_text = str(sock.recv(1024), "utf-8")

    except ConnectionError:
        # in cazul unei erori de conexiune, se afiseaza un mesaj
        response_text = "Eroare de conectare la microserviciul Student!"

    # se adauga raspunsul primit in caseta text din interfata grafica
    response_widget.insert(END, response_text)


def ask_question():
    # preluare text intrebare de pe interfata grafica
    question_text = question.get()
    port_s = port_student.get()
    dest = port_dest.get()

    formatted_message = f"intrebare {port_s} {dest} {question_text}"

    # pornire thread separat pentru tratarea intrebarii respective
    # astfel, nu se blocheaza interfata grafica!
    threading.Thread(target=resolve_question, args=(port_s, formatted_message)).start()


if __name__ == '__main__':
    # elementul radacina al interfetei grafice
    root = Tk()
    root.title("Interactiune studenti - profesor")

    # la redimensionarea ferestrei, cadrele se extind pentru a prelua spatiul ramas
    root.columnconfigure(0, weight=1)
    root.rowconfigure(0, weight=1)

    # cadrul care incapsuleaza intregul continut
    content = ttk.Frame(root)

    # caseta text care afiseaza raspunsurile la intrebari
    response_widget = Text(content, height=10, width=50)

    # eticheta text din partea dreapta
    question_label = ttk.Label(content, text="Studentul intreaba:")

    # caseta de introducere text cu care se preia intrebarea de la utilizator
    question = ttk.Entry(content, width=50)

    port_label = ttk.Label(content, text="Port de interactiune:")
    port_student = ttk.Entry(content, width=20)
    port_student.insert(0, "1701")

    port_dest_label = ttk.Label(content, text="Port de destinatie:")
    port_dest = ttk.Entry(content, width=20)
    port_dest.insert(0, "1600")

    # butoanele din dreapta-jos
    ask = ttk.Button(content, text="Intreaba", command=ask_question)  # la apasare, se apeleaza functia ask_question
    exitbtn = ttk.Button(content, text="Iesi", command=root.destroy)  # la apasare, se iese din aplicatie

    # plasarea elementelor in layout-ul de tip grid
    content.grid(column=0, row=0)
    response_widget.grid(column=0, row=0, columnspan=3, rowspan=5)
    question_label.grid(column=3, row=0, columnspan=2)
    question.grid(column=3, row=1, columnspan=2)

    port_label.grid(column=3, row=2)
    port_student.grid(column=4, row=2)

    port_dest_label.grid(column=3, row=3)
    port_dest.grid(column=4, row=3)


    ask.grid(column=3, row=4)
    exitbtn.grid(column=4, row=4)

    # bucla principala a interfetei grafice care asteapta evenimente de la utilizator
    root.mainloop()
