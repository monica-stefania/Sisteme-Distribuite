#!/usr/bin/env python3
import sys

site_counts = {}

for line in sys.stdin:
    line = line.strip()
    
    if not line or '\t' not in line:
        continue
        
    host, count = line.split('\t', 1)
    visit_count = int(count)
 
    if host not in site_counts:
        site_counts[host] = 0
    site_counts[host] += visit_count
    
print("-----TOP 5 SITES-----")
sorted_list = []
for host, count in site_counts.items():
    sorted_list.append([count, host])

sorted_list.sort(reverse=True)
for count, host in sorted_list[:5]:
    print(f"{host}\t{count}")
