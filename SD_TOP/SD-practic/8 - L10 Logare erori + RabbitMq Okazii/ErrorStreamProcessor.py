import json
import os
from mq_communication import RabbitMq


class ErrorStreamProcessor:
    def __init__(self, filename="statistici_erori.json"):
        self.filename = filename
        self.rabbitmq = RabbitMq()

        # Dicționarul în care contorizăm erorile, exact cum s-a cerut
        self.stats = {
            "erori_comunicare": 0,
            "erori_sistem_cozi": 0,
            "format_invalid": 0
        }

        # Dacă fișierul există deja de la o rulare anterioară, îi încărcăm starea
        if os.path.exists(self.filename):
            try:
                with open(self.filename, "r") as f:
                    self.stats.update(json.load(f))
            except Exception:
                print("[ErrorProcessor] Fișierul de statistici existent este gol sau corupt. Se reinițializează.")

    def abstract_error_handler(self, mesaj_eroare):
        """
        Funcția de callback apelată automat când vine un mesaj pe coada de erori.
        Aici se realizează statistica în mod dinamic.
        """
        tip_eroare = mesaj_eroare.get("tip")
        detalii = mesaj_eroare.get("detalii")
        componenta = mesaj_eroare.get("componenta")

        if tip_eroare in self.stats:
            self.stats[tip_eroare] += 1
            print(f"\n[ERROR PROCESSOR] Interceptat de la [{componenta}]: {tip_eroare} -> {detalii}")
            self.salveaza_in_fisier()
        else:
            print(f"\n[WARNING] S-a primit un tip de eroare nespecificat: {tip_eroare}")

    def afiseaza_raport_consola(self):
        """Afișează starea curentă a statisticilor în terminal"""
        print("\n================ STATISTICI ERORI CURENTE ================")
        print(f" ❌ Erori de comunicare (rețea): {self.stats['erori_comunicare']}")
        print(f" ⚠️ Erori sistem de cozi (buffer/praguri): {self.stats['erori_sistem_cozi']}")
        print(f" 📝 Format invalid (mesaje malformate): {self.stats['format_invalid']}")
        print("==========================================================")

    def salveaza_in_fisier(self):
        """Scrie dicționarul actualizat în fișierul local JSON"""
        with open(self.filename, "w") as f:
            json.dump(self.stats, f, indent=4)
        self.afiseaza_raport_consola()

    def run(self):
        print("[ErrorProcessor] Microserviciul a pornit și monitorizează fluxul de erori...")
        # Ascultă pe coada dedicată erorilor (specificată în configurarea RabbitMQ)
        self.rabbitmq.listen_queue("licitatie.queue.erori", self.abstract_error_handler)


if __name__ == "__main__":
    processor = ErrorStreamProcessor()
    processor.run()