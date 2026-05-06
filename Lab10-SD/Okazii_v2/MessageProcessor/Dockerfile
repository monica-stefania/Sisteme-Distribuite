FROM python:alpine

RUN pip install kafka-python

WORKDIR /message-processor
ADD MessageProcessor.py $WORKDIR

CMD ["python", "MessageProcessor.py"]