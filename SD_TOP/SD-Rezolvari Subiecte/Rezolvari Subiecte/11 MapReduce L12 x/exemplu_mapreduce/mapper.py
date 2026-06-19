files = ['carte.txt', 'document.txt', 'other.txt']

for file_name in files:
    file_object = open(file_name, "r")

    for line in file_object.readlines():
        L = [(word.strip().lower(), file_name) for word in line.strip().split()]

        for word, filename in L:
            print('<%s, {%s: 1}>' % (word, filename))
