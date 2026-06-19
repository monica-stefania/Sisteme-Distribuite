from mq_communication import RabbitMq


class Auctioneer:
    def __init__(self):
        self.rabbitmq = RabbitMq()

    def receive_bids(self, date_curate):
        bidder = date_curate.get("id_bidder")
        suma = date_curate.get("suma")

        try:
            print(f"[Auctioneer] Oferta finala inregistrata: {bidder} a licitat {suma}.")

            # Simulare eroare de sistem/cozi (Prag de siguranta depasit)
            if suma > 9000:
                raise RuntimeError("Suma licitata depaseste capacitatea maxima a buffer-ului alocat.")

        except RuntimeError as e:
            pachet_eroare = {
                "tip": "erori_sistem_cozi",
                "detalii": f"Auctioneer Limitation: {str(e)}",
                "componenta": "Auctioneer"
            }
            self.rabbitmq.send_message(routing_key="eroare", message=pachet_eroare)

    def run(self):
        try:
            self.rabbitmq.listen_queue("licitatie.queue.procesate", self.receive_bids)
        except Exception as e:
            pachet_eroare = {
                "tip": "erori_comunicare",
                "detalii": f"Conexiune esuata la broker: {str(e)}",
                "componenta": "Auctioneer"
            }
            self.rabbitmq.send_message(routing_key="eroare", message=pachet_eroare)


if __name__ == "__main__":
    auctioneer = Auctioneer()
    auctioneer.run()