from __future__ import print_function
from operator import itemgetter

import sys

currentWord = None
currentWordCount = 0
fileListWithCount = {}
lineList = []
word = None

# input comes from STDIN
for line in sys.stdin:  # CTRL+D pentru a trimite EOF
      # parse the input we got from mapper.py
      word, fileName = line.strip().split(',', 1)
      word = str(word).replace('<', '')  # -> doar cuvantul
      fileName = str(fileName).replace(' {', '').replace(': 1}>', '')  # -> doar filename

      lineList.append((word, fileName))

lineList.sort()
print(lineList)

for pair in lineList:
      word, fileName = pair

      if currentWord == None: # primul cuvant
            currentWord = word
            fileListWithCount[word] = {} # trebuie sa initializam dictionarul nested pentru fiecare cuvant nou
            fileListWithCount[word][fileName] = 1
            continue

      if currentWord == word:
            if fileName not in fileListWithCount[currentWord]:
                  fileListWithCount[currentWord][fileName] = 1
            else:
                  fileListWithCount[currentWord][fileName] += 1
            continue

      if currentWord != word:
            print("<%s, {%s}>" % (currentWord, fileListWithCount[currentWord]))
            fileList = [fileName]
            currentWord = word
            fileListWithCount[word] = {} # trebuie sa initializam dictionarul nested pentru fiecare cuvant nou
            fileListWithCount[word][fileName] = 1
            continue

# do not forget to output the last word if needed!
if currentWord == word:
      print("<%s, {%s}>" % (currentWord, fileListWithCount[currentWord]))
