import os
import sys
from mq_communication import RabbitMq, q


def main():
    rabbit_mq = RabbitMq()

    while True:
        msg = input("Write your message: ")
        if msg == "x" or msg == "stop":
            break

        rabbit_mq.send_message(message=msg)

        # asyncronous function
        rabbit_mq.receive_message()

        # wait until a message comes
        response = q.get()

        # process response
        print(response)


if __name__ == '__main__':
    main()
