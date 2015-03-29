import requests

url=url = 'https://api.uber.com/v1/estimates/price'

parameters = {
    'server_token': 'u3AbNk9xAODJh8dZnTeKrV3dKSaO0Oa6kgX2fVOn',
    'latitude': 33.7758,
    'longitude': -84.3947,
}

response = requests.get(url, params=parameters)

data = response.json()