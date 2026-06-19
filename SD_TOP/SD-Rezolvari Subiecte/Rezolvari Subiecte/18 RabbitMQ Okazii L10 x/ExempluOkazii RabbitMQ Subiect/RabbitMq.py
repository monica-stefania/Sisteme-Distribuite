import pika

class RabbitMq():
    # config = {
    #     'host': '0.0.0.0',
    #     'port': 5678,
    #     'username': 'student',
    #     'password': 'student',
    #     'exchange': 'pyrabbitsample.exchange',
    #     'routing_key': 'pyrabbitsample.routingkey',
    #     'queue': 'pyrabbitsample.queue'
    # }

    def __init__(self, config):
        self.config = config
        self.credentials = pika.PlainCredentials(config['username'], config['password'])
        self.parameters = (pika.ConnectionParameters(host=config['host']),
                           pika.ConnectionParameters(port=config['port']),
                           pika.ConnectionParameters(credentials=self.credentials))
        self.current_message = None

    def on_received_message(self, blocking_channel,
                            deliver, properties, message):
        result = message.decode('utf-8')
        blocking_channel.confirm_delivery()
        try:
            print(result)
            self.current_message = result
        except Exception as e:
            print(e)
            print("wrong data format")
        finally:
            blocking_channel.stop_consuming()

    def receive_message(self):
        # automatically close the connection
        with pika.BlockingConnection(self.parameters) as connection:
            # automatically close the channel
            with connection.channel() as channel:
                channel.basic_consume(self.config['queue'],
                                      self.on_received_message,
                                      auto_ack=True)
                try:
                    channel.start_consuming()
                # Don't recover connections closed by server
                except Exception:
                    print("Connection closed by broker.")
                    print('OR')
                    print("AMQP Channel Error")
                # Don't recover on channel errors
                # Don't recover from KeyboardInterrupt
                except KeyboardInterrupt:
                    print("Application closed.")
        return self.current_message

    def send_message(self, message):
        # automatically close the connection
        with pika.BlockingConnection(self.parameters) as connection:
            # automatically close the channel
            with connection.channel() as channel:
                channel.basic_publish(
                    exchange=self.config['exchange'],
                    routing_key=self.config['routing_key'],
                    body=message)

    # def clear_queue(self, channel):
    #     channel.queue_purge(self.config['queue'])
