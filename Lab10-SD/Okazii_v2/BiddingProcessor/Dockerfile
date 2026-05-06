FROM python:alpine

RUN pip install kafka-python

WORKDIR /biddinng-processor
ADD BiddingProcessor.py $WORKDIR

CMD ["python", "BiddingProcessor.py"]