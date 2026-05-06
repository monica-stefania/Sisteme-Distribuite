FROM python:alpine

RUN pip install kafka-python

WORKDIR /bidder
ADD Bidder.py $WORKDIR

CMD ["python", "Bidder.py"]