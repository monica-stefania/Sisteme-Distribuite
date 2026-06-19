import pika
import json
import time


class RabbitMq:
    config = {
        'host': 'localhost',
        'port': 5672,
        'username': 'student',
        'password': 'student',
        'exchange': 'licitatie.direct'
    }

    def __init__(self):
        credentials = pika.PlainCredentials(self.config['username'], self.config['password'])
        self.parameters = pika.ConnectionParameters(
            host=self.config['host'],
            port=self.config['port'],
            credentials=credentials
        )

    def send_message(self, routing_key, message):
        """Trimite un mesaj către exchange folosind o cheie de rutare specifică"""
        with pika.BlockingConnection(self.parameters) as connection:
            with connection.channel() as channel:
                channel.basic_publish(
                    exchange=self.config['exchange'],
                    routing_key=routing_key,
                    body=json.dumps(message)
                )

    def listen_queue(self, queue_name, callback_func):
        """Ascultă continuu o coadă cu logică nativă de auto-retry în caz de eroare"""
        while True:
            try:
                with pika.BlockingConnection(self.parameters) as connection:
                    with connection.channel() as channel:

                        def on_message(ch, method, properties, body):
                            try:
                                data = json.loads(body.decode('utf-8'))
                                callback_func(data)
                            except Exception as e:
                                print(f"Eroare procesare mesaj: {e}")

                        channel.basic_consume(queue=queue_name, on_message_callback=on_message, auto_ack=True)
                        print(f"[*] Se ascultă pe coada: {queue_name}...")
                        channel.start_consuming()

            except (pika.exceptions.AMQPConnectionError, pika.exceptions.AMQPChannelError):
                print("Disconnected de la RabbitMQ. Se reîncearcă reconectarea în 5 secunde...")
                time.sleep(5)
            except pika.exceptions.ConnectionClosedByBroker:
                print("Conexiunea a fost închisă de broker. Reîncercăm...")
                time.sleep(5)
            except KeyboardInterrupt:
                print("\nAplicație oprită de utilizator.")
                break
            except Exception as e:
                print(f"Eroare neprevăzută: {e}. Reîncercăm...")
                time.sleep(5)