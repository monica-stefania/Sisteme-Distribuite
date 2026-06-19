import socket
import sys
import time
from datetime import date, timedelta
import requests
import json

# Cream serverul in care vom trimite simbolurile si stirile
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the socket to the port
server_address = ('localhost', 9998)
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

        # Aici facem cerere pentru stirile asociate simbolului
        news_associated = requests.get('https://finnhub.io/api/v1/company-news?symbol=AAPL&from=' + str(date.today() - timedelta(days=14)) + '&to=' + str(date.today()) + '&token=brmu4j7rh5r90ebn6irg')

        parsed_news_associated = json.loads(news_associated.text)

        for piece_of_news in parsed_news_associated:
            # fara \n nu merge
            piece_of_news = str(piece_of_news).replace(' \'', ' \"').replace('{\'', '{\"').replace('\'}', '\"}').replace('\':', '\":').replace('\',', '\",') + '\n'
            print(piece_of_news)
            connection.sendall(bytes(piece_of_news, encoding='utf8')) # transformam in sr fiindca by default e dict
            time.sleep(5)

    finally:
        # Clean up the connection
        connection.close()
