#!/usr/bin/env python3
"""reducer.py"""

import sys

current_letter = None
words = []

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    letter, word = line.split()  # line.split('\t', 1)

    # this IF-switch only works because Hadoop sorts map output
    # by key (here: word) before it is passed to the reducer
    if current_letter != letter:
        if current_letter:
            # write result to STDOUT
            print('%s\t%s' % (current_letter, words))
        current_letter = letter
        words.clear()
    words.append(word)

if current_letter:
    print('%s\t%s' % (current_letter, words))
