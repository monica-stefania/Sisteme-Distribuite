import random
import time

from RabbitMq import RabbitMq
import threading

rabbitMq = RabbitMq()

class Consumer(threading.Thread):
    def __init__(self):
        super().__init__()

    def run(self):
        while True:
            rabbitMq.receive_message()
            print('\n\n')
            time.sleep(2)

class Producer(threading.Thread):
    def __init__(self):
        super().__init__()

    def run(self):
        while True:
            i = random.random()*100
            message = 'mesaj {}'.format(int(i))

            # thread-ul producator trimite mesaje catre un topic
            rabbitMq.send_message(message)
            print("Am produs mesajul: {}".format(message))

            time.sleep(2)

if __name__ == '__main__':
    # se creeaza 2 thread-uri: unul producator de mesaje si celalalt consumator
    producer_thread = Producer()
    consumer_thread = Consumer()

    producer_thread.start()
    time.sleep(1)
    consumer_thread.start()

    producer_thread.join()
    consumer_thread.join()
