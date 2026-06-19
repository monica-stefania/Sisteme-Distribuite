import random
from mq_communication import RabbitMq


class Bidder:
    def __init__(self, bidder_id):
        self.bidder_id = bidder_id
        self.rabbitmq = RabbitMq()

    def bid(self):
        pret_licitat = random.randint(1000, 10000)
        pachet_oferta = {
            "id_bidder": self.bidder_id,
            "suma": pret_licitat
        }

        try:
            print(f"[{self.bidder_id}] Trimit oferta de pret: {pret_licitat}...")
            # Trimitem către MessageProcessor prin cheia 'oferta.bruta'
            self.rabbitmq.send_message(routing_key="oferta.bruta", message=pachet_oferta)

            # Simulăm un defect ocazional (mesaj corupt) pentru a testa procesorul de flux
            if random.random() < 0.15:
                raise ValueError("Eroare de segmentare pachet la nivelul Bidder-ului.")

        except ValueError as e:
            # Raportăm eroarea către ErrorStreamProcessor
            pachet_eroare = {
                "tip": "format_invalid",
                "detalii": f"{self.bidder_id}: {str(e)}",
                "componenta": "Bidder"
            }
            self.rabbitmq.send_message(routing_key="eroare", message=pachet_eroare)


if __name__ == "__main__":
    b_id = f"Bidder_{random.randint(10, 99)}"
    bidder = Bidder(bidder_id=b_id)
    bidder.bid()