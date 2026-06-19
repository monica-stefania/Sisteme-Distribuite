import requests
from bs4 import BeautifulSoup
from bs4.element import Comment


def tag_visible(element):
    if element.parent.name in ['style', 'script', 'head', 'title', 'meta', '[document]']:
        return False
    if isinstance(element, Comment):
        return False
    return True


def text_from_html(body):
    soup = BeautifulSoup(body, 'html.parser')
    texts = soup.findAll(text=True)
    visible_tests = filter(tag_visible, texts)
    return u" ".join(t.strip() for t in visible_tests)


urls = ['http://mike.tuiasi.ro',
        'http://ac.tuiasi.ro']

for url in urls:
    url_content = requests.get(url).text

    url_content = text_from_html(url_content)

    L = []
    for word in url_content.strip().split():
        if word.isalnum():
            L.append((word.strip().lower(), url))

    for (word, url_to_show) in L:
        print('<%s, {%s: 1}>' % (word, url_to_show))
