from tkinter import *
from tkinter import ttk
import threading
import socket

HOST = "127.0.0.1"
TEACHER_PORT = 1600
STUDENT_PORT = 1700

def resolve_question(question_text, port, label):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.settimeout(5)
    try:
        sock.connect((HOST, port))
        sock.send(bytes(question_text + "\n", "utf-8"))
        response_text = str(sock.recv(1024), "utf-8")
    except ConnectionError:
        response_text = f"Eroare de conectare pe portul {port}!"
    except Exception as e:
        response_text = f"Eroare:{str(e)}"
    finally:
        sock.close()

    # response_widget.insert(END, f"[{label}]: {response_text}\n")
    root.after(0, lambda: response_widget.insert(END, f"[{label}]: {response_text}\n"))
def ask_teacher():
    question_text = question.get()
    threading.Thread(
        target=resolve_question,
        args=(question_text, TEACHER_PORT, "PROFESOR")
    ).start()

def ask_student():
    question_text = question.get()
    try:
        port = int(student_port.get())
    except ValueError:
        port = STUDENT_PORT
    threading.Thread(
        target=resolve_question,
        args=(question_text, port, "STUDENT")
    ).start()

if __name__ == '__main__':
    root = Tk()
    root.title("Interactiune profesor-studenti")
    root.columnconfigure(0, weight=1)
    root.rowconfigure(0, weight=1)

    content = ttk.Frame(root, padding=10)
    content.grid(column=0, row=0, sticky=(N, W, E, S))

    response_widget = Text(content, height=15, width=50)
    response_widget.grid(column=0, row=0, columnspan=3, rowspan=4, padx=5, pady=5)

    ttk.Label(content, text="Intrebare:").grid(column=3, row=0)
    question = ttk.Entry(content, width=30)
    question.grid(column=3, row=1, columnspan=2)

    ttk.Label(content, text="Port student:").grid(column=3, row=2)
    student_port = ttk.Entry(content, width=10)
    student_port.insert(0, str(STUDENT_PORT))  # valoare default
    student_port.grid(column=4, row=2)

    ttk.Button(
        content,
        text="Intreaba Profesor",
        command=ask_teacher
    ).grid(column=3, row=3)

    ttk.Button(
        content,
        text="Intreaba Student",
        command=ask_student
    ).grid(column=4, row=3)

    ttk.Button(
        content,
        text="Iesi",
        command=root.destroy
    ).grid(column=3, row=4, columnspan=2)

    root.mainloop()