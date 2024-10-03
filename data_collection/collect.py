import requests
import json

def fetch_data():
    req = requests.get('https://dekalbpublic.etaspot.net/service.php?service=get_vehicles&includeETAData=1&inService=1&orderedETAArray=1&token=TESTING')

    if req.status_code == 200:
        print(req.json())
        json.dump(req.json(), open('data.json', 'w'))

if __name__ == '__main__':
    fetch_data()