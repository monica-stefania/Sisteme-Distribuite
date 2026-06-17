from kafka import KafkaProducer
import time

producer = KafkaProducer(bootstrap_servers='localhost:9092')

with open('ebook.txt', 'r', encoding='utf-8') as f:
    for line in f:
        words = line.strip().split()
        for word in words:
            # curatam cuvantul si trimitem
            if word.isalpha():
                producer.send('words-topic', word.lower().encode('utf-8'))
                print(f"Sent: {word.lower()}")
        time.sleep(0.1)  # putin delay ca sa prinda Spark batch-urile

producer.flush()
producer.close()