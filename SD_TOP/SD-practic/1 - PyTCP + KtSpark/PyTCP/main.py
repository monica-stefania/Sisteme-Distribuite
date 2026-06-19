import json
import socket
import requests
from datetime import date
import time

#fac cerere catre api pentru a lua simbolurile
response = requests.get('https://finnhub.io/api/v1/stock/symbol?exchange=US&token=brmr2kfrh5rcss140jmg')
data = json.loads(response.text)
symbols = []
for stock in data:
    symbols.append(stock['symbol'])
print(symbols)
today = date.today().strftime("%Y-%m-%d")
socket_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket_server.bind(('localhost', 5050))
socket_server.listen(1)

while True:
    conn, addr = socket_server.accept()
    print('Connected by', addr)

    try:
        for symbol in symbols:
            response_news = requests.get(f'https://finnhub.io/api/v1/company-news?symbol={symbol}&from={today}&to={today}&token=brmr2kfrh5rcss140jmg')
            if response_news.status_code == 200:
                news = json.loads(response_news.text)
                print(news)
                for new in news:
                    message = json.dumps(new) + '\n'
                    conn.send(message.encode())
                    print('Mesasge was sent: ', message)
                    time.sleep(3)
            else:
                time.sleep(1)

    except socket.error:
        print('Socket error')
    finally:
        conn.close()
        print('Socket closed')

