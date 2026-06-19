#!/usr/bin/env python3
import sys

current_word = None
document_counts = {}

for line in sys.stdin:
    line = line.strip()

    if not line:
        continue

    try:
        word, doc_info = line.split("\t", 1)
        document_id, count = doc_info.split(":", 1)
        count = int(count)
    except ValueError:
        continue

    if current_word != word:
        if current_word is not None:
            sorted_docs = dict(sorted(document_counts.items()))
            result = ", ".join(
                f"{doc}: {cnt}" for doc, cnt in sorted_docs.items()
            )
            print(f"{current_word}\t{{{result}}}")

        current_word = word
        document_counts = {}

    document_counts[document_id] = document_counts.get(document_id, 0) + count

if current_word is not None:
    sorted_docs = dict(sorted(document_counts.items()))
    result = ", ".join(
        f"{doc}: {cnt}" for doc, cnt in sorted_docs.items()
    )
    print(f"{current_word}\t{{{result}}}")