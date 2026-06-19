from __future__ import print_function
from operator import itemgetter

import sys

currentWord = None
currentWordCount = 0
urlListWithCount = {}
lineList = []
word = None

# input comes from STDIN
for line in sys.stdin:  # CTRL+D pentru a trimite EOF
      # parse the input we got from mapper.py
      word, urlName = line.strip().split(',', 1)
      word = str(word).replace('<', '')  # -> doar cuvantul
      urlName = str(urlName).replace(' {', '').replace(': 1}>', '')  # -> doar urlName

      lineList.append((word, urlName))

lineList.sort()
print(lineList)

for pair in lineList:
      word, urlName = pair

      if currentWord == None: # primul cuvant
            currentWord = word
            urlListWithCount[word] = {} # trebuie sa initializam dictionarul nested pentru fiecare cuvant nou
            urlListWithCount[word][urlName] = 1
            continue

      if currentWord == word:
            if urlName not in urlListWithCount[currentWord]:
                  urlListWithCount[currentWord][urlName] = 1
            else:
                  urlListWithCount[currentWord][urlName] += 1
            continue

      if currentWord != word:
            print("<%s, {%s}>" % (currentWord, urlListWithCount[currentWord]))
            urlList = [urlName]
            currentWord = word
            urlListWithCount[word] = {} # trebuie sa initializam dictionarul nested pentru fiecare cuvant nou
            urlListWithCount[word][urlName] = 1
            continue

# do not forget to output the last word if needed!
if currentWord == word:
      print("<%s, {%s}>" % (currentWord, urlListWithCount[currentWord]))
