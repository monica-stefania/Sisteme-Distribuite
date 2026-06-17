#!/usr/bin/env python3
import sqlite3
from urllib.parse import urlparse

sqlite_path = '/home/moni/.mozilla/firefox/6rytudg5.default-esr/places.sqlite'

print(f"Se citește baza de date din: {sqlite_path}")

conn = sqlite3.connect(sqlite_path)
cursor = conn.cursor()

cursor.execute("SELECT url, visit_count FROM moz_places WHERE visit_count > 0")

with open("tema3/input/istoric_input.txt", "w") as f:
    for url, visit_count in cursor.fetchall():
        parsed_url = urlparse(url)
        host = parsed_url.netloc
        
        if not host:
            continue
            
        host = host.replace('www.', '')
        f.write(f"{host}\t{visit_count}\n")

print("Gata! Istoricul a fost salvat local în 'istoric_input.txt'.")
conn.close()
