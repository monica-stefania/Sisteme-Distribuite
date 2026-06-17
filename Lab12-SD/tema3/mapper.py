#!/usr/bin/env python3
import sys

for line in sys.stdin:
    line = line.strip()
    if not line or '\t' not in line:
        continue
        
    host, visit_count = line.split('\t', 1)
    print(f"{host}\t{visit_count}")
