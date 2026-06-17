#!/usr/bin/env python3

import sys

#current_url = None
#internal_links = []

sitemaps = {}
word_counts_per_page = {}
global_word_counts = {}

for line in sys.stdin:
    line = line.strip()
    if not line:
        continue

    """
    url, internal_url = line.split('\t', 1)
    
    if url != current_url:
        if current_url:
            print(f"{current_url}\t{internal_links}")
        current_url = url
        internal_links = []
    internal_links.append(internal_url)
    """
    
    key, value = line.split('\t', 1)
    
    if key.startswith("SITE-MAP:"):
        url = key.split("SITE-MAP:")[1]
        
        if url not in sitemaps:
            sitemaps[url] = set()
        sitemaps[url].add(value)
        
    elif key.startswith("WORD:"):
        parts = key.split("WORD:")[1]
        url, word = parts.split("|", 1)
        count = int(value)
        
        if url not in word_counts_per_page:
            word_counts_per_page[url] = {}
        if word not in word_counts_per_page[url]:
            word_counts_per_page[url][word] = 0
        word_counts_per_page[url][word] += count
            
        if word not in global_word_counts:
            global_word_counts[word] = 0
        global_word_counts[word] += count

print("------SITE_MAPS------")            
#for url, links in sitemaps.items():
     #print(f"{url}\t{links}")
     
print("------TOP 5 WORDS PER PAGE------") 
for url, words in word_counts_per_page.items():
    list_freq = []
    for word, count in words.items():
        list_freq.append([count, word])
        
    list_freq.sort(reverse=True)
    top_5 = list_freq[:5]
    results = []
    for count, word in top_5:
        results.append(f"{word} ({count})")
    print(f"{url}\t{', '.join(results)}")
        
print("------TOP 5 WORDS GLOBAL------") 
list_global = []
for word, count in global_word_counts.items():
    list_global.append([count, word])
    
list_global.sort(reverse=True)
for count, word in list_global[:5]:
    print(f"{word}\t{count}")
