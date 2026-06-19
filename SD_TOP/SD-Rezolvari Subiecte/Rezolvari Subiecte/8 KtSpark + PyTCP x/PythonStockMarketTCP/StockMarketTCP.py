import socket
import sys
import time
from datetime import date
import requests
import json

# Facem rost de simboluri
symbols = requests.get('https://finnhub.io/api/v1/stock/symbol?exchange=US&token=brmr2kfrh5rcss140jmg')
parsed_symbols = json.loads(symbols.text)

# Cream serverul in care vom trimite simbolurile si stirile
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the socket to the port
server_address = ('localhost', 9999)
print(sys.stderr, 'starting up on %s port %s' % server_address)
sock.bind(server_address)

# Listen for incoming connections
sock.listen(1)

while True:
    # Wait for a connection
    print(sys.stderr, 'waiting for a connection')
    connection, client_address = sock.accept()

    try:
        print(sys.stderr, 'connection from', client_address)

        for symbol in parsed_symbols:
            # Aici facem cerere pentru stirile asociate simbolului
            news_associated = requests.get('https://finnhub.io/api/v1/company-news?symbol='+ str(symbol['symbol']) + '&from=' + str(date.today()) + '&to=' + str(date.today()) + '&token=brmr2kfrh5rcss140jmg')

            parsed_news_associated = json.loads(news_associated.text)

            for piece_of_news in parsed_news_associated:
                # fara \n nu merge
                connection.sendall(bytes(str(piece_of_news).replace('\"', 'IquoteI').replace('\'', '\"') + '\n', encoding='utf8')) # transformam in sr fiindca by default e dict
                time.sleep(3)

    finally:
        # Clean up the connection
        connection.close()
