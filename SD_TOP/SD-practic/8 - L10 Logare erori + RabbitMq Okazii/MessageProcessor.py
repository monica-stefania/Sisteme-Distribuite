from mq_communication import RabbitMq


class MessageProcessor:
    def __init__(self):
        self.rabbitmq = RabbitMq()
        self.bidderi_procesati = set()  # Eliminare duplicate

    def proceseaza_mesaj(self, date_oferta):
        try:
            if "id_bidder" not in date_oferta or "suma" not in date_oferta:
                raise KeyError("Lipsesc câmpuri cheie din structura ofertei.")

            bidder_id = date_oferta["id_bidder"]
            suma = date_oferta["suma"]

            # Eliminare duplicate
            if bidder_id in self.bidderi_procesati:
                print(f"[MessageProcessor] Duplicat ignorat pentru {bidder_id}.")
                return

            self.bidderi_procesati.add(bidder_id)
            print(f"[MessageProcessor] Oferta de la {bidder_id} este valida. Trimit la Auctioneer...")

            # Trimitem către coada procesată citită de Auctioneer
            self.rabbitmq.send_message(routing_key="oferta.processed", message=date_oferta)

        except KeyError as ex:
            pachet_eroare = {
                "tip": "format_invalid",
                "detalii": f"Mesaj malformat interceptat: {str(ex)}",
                "componenta": "MessageProcessor"
            }
            self.rabbitmq.send_message(routing_key="eroare", message=pachet_eroare)

        except Exception as ex:
            pachet_eroare = {
                "tip": "erori_comunicare",
                "detalii": f"Eroare retea in MessageProcessor: {str(ex)}",
                "componenta": "MessageProcessor"
            }
            self.rabbitmq.send_message(routing_key="eroare", message=pachet_eroare)

    def run(self):
        # Ascultă cozile brute populate de Bidderi
        self.rabbitmq.listen_queue("licitatie.queue.brute", self.proceseaza_mesaj)


if __name__ == "__main__":
    mp = MessageProcessor()
    mp.run()