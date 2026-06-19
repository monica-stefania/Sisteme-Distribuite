"""
Pornind de la un fișier text de tip Ebook să se identifice și să se afișeze cu ajutorul Spark
 utilizând limbajul python cele mai des utilizate cuvinte din text (se ignoră entitățile semantice de legătură).
"""

import os
import re

from pyspark import SparkContext, SparkConf, StorageLevel, RDD

if __name__ == '__main__':
    spark_conf = SparkConf().setMaster("local").setAppName("Spark Example")
    spark_context = SparkContext(conf=spark_conf)

    ROOT_DIR = os.path.abspath(os.path.dirname(__file__))
    path: str = "file:///" + os.path.join(ROOT_DIR, 'resources/data.txt')
    lines = spark_context.textFile(path)

    BLACKLIST = ["the", "in", "or", "of", "the", "and", "a", "to", "is", "as", "by", "on", "that", ""]


    #se poate si fara boardcast variable, dar ea eficientizeaza resursele shared between threads
    #necreeand mai multe instante ale variabile BLACKLIST
    broadcast_variable = spark_context.broadcast(BLACKLIST)

    counts = lines\
        .flatMap(lambda line: re.split("[ ;.,:/]", line)) \
        .filter(lambda word: word.lower() not in broadcast_variable.value) \
        .map(lambda word: (word.lower(), 1)) \
        .reduceByKey(lambda a, b: a + b) \
        .sortBy(lambda item: -item[1]) \
        .take(10)

    #print(counts)
    for pair in counts:
        print(pair)