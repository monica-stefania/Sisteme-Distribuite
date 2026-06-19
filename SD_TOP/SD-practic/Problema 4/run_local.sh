#!/bin/bash

for file in input/*.txt; do
    map_input_file="$file" ./mapper.py < "$file"
done | sort | ./reducer.py