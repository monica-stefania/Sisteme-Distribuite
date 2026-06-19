import time

from RabbitMq import RabbitMq
from func_timeout import func_set_timeout

class ConsumerAuctioneer:
    def __init__(self, config):
        self.rabbitMq = RabbitMq(config)
        self.message_queue = []

    @func_set_timeout(15)
    def consume(self):
        while True:
            self.message_queue.append(self.rabbitMq.receive_message())


    def getQueue(self):
        return self.message_queue


class ProducerAuctioneer:
    def __init__(self, config):
        self.rabbitMq = RabbitMq(config)

    def send(self, message):
        self.rabbitMq.send_message(message)

####################################################

class ConsumerBidder:
    def __init__(self, config):
        self.rabbitMq = RabbitMq(config)

    def getMessage(self):
        return self.rabbitMq.receive_message()


class ProducerBidder:
    def __init__(self, config):
        self.rabbitMq = RabbitMq(config)

    def send(self, message):
        self.rabbitMq.send_message(message)

####################################################

class ConsumerMessageProcessor_NOTIFICATIONS:
    def __init__(self, config):
        self.rabbitMq = RabbitMq(config)
        self.message_queue = []

    def getMessage(self):
        return self.rabbitMq.receive_message()

    @func_set_timeout(5)
    def consume(self):
        while True:
            self.message_queue.append(self.rabbitMq.receive_message())

    def getQueue(self):
        return self.message_queue

    class ConsumerMessageProcessor:
        def __init__(self, config):
            self.rabbitMq = RabbitMq(config)
            self.message_queue = []

        def getMessage(self):
            return self.rabbitMq.receive_message()

        #@func_set_timeout(20)
        def consume(self):
            while True:
                self.message_queue.append(self.rabbitMq.receive_message())

        def getQueue(self):
            return self.message_queue

class ConsumerMessageProcessor_BIDS:
    def __init__(self, config):
        self.rabbitMq = RabbitMq(config)
        self.message_queue = []

    def getMessage(self):
        return self.rabbitMq.receive_message()

    def consume(self):
        while True:
            message = self.rabbitMq.receive_message()
            if message == 'incheiat':
                break
            self.message_queue.append(message)


    def getQueue(self):
        return self.message_queue

class ProducerMessageProcessor:
    def __init__(self, config):
        self.rabbitMq = RabbitMq(config)

    def send(self, message):
        self.rabbitMq.send_message(message)
####################################################

class ConsumerBiddingProcessor:
    def __init__(self, config):
        self.rabbitMq = RabbitMq(config)
        self.message_queue = []

    def getMessage(self):
        return self.rabbitMq.receive_message()

    @func_set_timeout(30)
    def consume(self):
        while True:
            self.message_queue.append(self.rabbitMq.receive_message())

    def getQueue(self):
        return self.message_queue

class ProducerBiddingProcessor:
    def __init__(self, config):
        self.rabbitMq = RabbitMq(config)

    def send(self, message):
        self.rabbitMq.send_message(message)
