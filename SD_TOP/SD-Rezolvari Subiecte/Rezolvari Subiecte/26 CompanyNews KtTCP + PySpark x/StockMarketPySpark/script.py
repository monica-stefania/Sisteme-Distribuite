from datetime import datetime

from pyspark import SparkContext
from pyspark.streaming import StreamingContext
import json

# netcat -l -p 9999

sc = SparkContext("local[2]", "Application")
ssc = StreamingContext(sc, 3)

lines = ssc.socketTextStream("localhost", 9999)

avg_profit = lines.map(lambda obj: json.loads(obj))\
    .filter(lambda obj: ".png" in obj["image"])\
    .filter(lambda obj: len(str(obj["url"]).replace('\"', '')) <= 80)\
    .flatMap(lambda obj: ("URL: " + str(obj["url"]), "Datetime: " + str(datetime.fromtimestamp(obj["datetime"])), "Headline: " + str(obj["headline"])))


avg_profit.pprint()

ssc.start()
ssc.awaitTermination()
