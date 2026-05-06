FROM python:alpine

RUN pip install kafka-python

WORKDIR /auctioneer
ADD Auctioneer.py $WORKDIR

CMD ["python", "Auctioneer.py"]