#!/usr/bin/env python3
import sys
import os
import re

for line in sys.stdin:
    line = line.strip()

    # Hadoop Streaming pune numele fișierului curent în variabila map_input_file
    document_path = os.environ.get("map_input_file", "unknown_document")
    document_id = os.path.basename(document_path)

    words = re.findall(r"[a-zA-Z]+", line.lower())

    for word in words:
        print(f"{word}\t{document_id}:1")