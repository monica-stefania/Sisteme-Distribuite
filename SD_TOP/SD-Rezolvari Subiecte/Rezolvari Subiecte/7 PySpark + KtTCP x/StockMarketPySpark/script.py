from pyspark import SparkContext
from pyspark.streaming import StreamingContext
import json

# netcat -l -p 9999

sc = SparkContext("local[2]", "Application")
ssc = StreamingContext(sc, 3)

lines = ssc.socketTextStream("localhost", 9999)

avg_profit = lines.map(lambda obj: json.loads(obj)).filter(lambda obj: obj["targetHigh"] > 0)\
    .filter(lambda obj: ((obj["targetMean"]+obj["targetLow"])/2 * 100 / obj["targetHigh"]) > 40)\
    .flatMap(lambda obj: (obj["symbol"], ((obj["targetMean"]+obj["targetLow"])/2 * 100 / obj["targetHigh"])))

avg_profit.pprint()

ssc.start()
ssc.awaitTermination()
