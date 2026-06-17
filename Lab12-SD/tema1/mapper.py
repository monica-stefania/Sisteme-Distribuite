#!/usr/bin/env python3

import sys
import requests
import re
from bs4 import BeautifulSoup
from urllib.parse import urljoin 

for line in sys.stdin:
    url = line.strip()
    if not url:
        continue
    
    try:
        headers = {'User-Agent': 'Mozilla/5.0'}
        response = requests.get(url, headers=headers, timeout=5)
        if response.status_code != 200:
            continue
        
        html = response.text
        soup = BeautifulSoup(html, 'html.parser')
        
        anchores = soup.find_all('a')
        for anchore in anchores:
            href = anchore.get('href')
            if href:
                internal_link = urljoin(url, href)
                print(f"SITE-MAP:{url}\t{internal_link}")
                
        context = soup.get_text().lower()
        words = re.findall(r'\b[a-z]{1,}\b', context)
        
        for word in words:
            print(f"WORD:{url}|{word}\t1")
        
    except Exception:
        continue
