import requests


def find_by_first_name(first_name):
    query = "/find?firstName={}".format(first_name.replace(" ", "%20"))
    full_url = "http://localhost:8080" + query
    print(full_url)
    try:
        response = requests.get(full_url)
        print(response)
        print(response.text)
    except requests.HTTPError as http_err:
        print('HTTP error occurred: {}'.format(http_err))
    except Exception as err:
        print('Other error occurred: {}'.format(err))


def print_menu():
    print("\n\nAlegeti o Optiune: ")
    print("1 - Cautare Dupa Nume")
    print("10 - Exit Program")


def main():

    while True:
        print_menu()
        try:
            opt = int(input("Optiune: "))
            if opt == 1:
                first_name = input("Firstname: ")
                find_by_first_name(first_name)
            elif opt == 10:
                break
        except ValueError:
            print("Invalid input")
            continue


if __name__ == '__main__':
    main()
