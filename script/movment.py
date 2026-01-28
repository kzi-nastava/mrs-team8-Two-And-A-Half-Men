import requests
class Movment:

    BASE_URL = "http://localhost:8080/api/v1"
    LOGIN_ENDPOINT = "/login"
    access_token = ""
    headers = {}
    driverID = ""
    def __init__(self, username="driver@test.com" , password="password"):
        self.loginDriver(username, password)
        self.headers = {
            "Authorization": f"Bearer {self.access_token}"
        }
        self.GetMineID()

    def loginDriver(self, username, password):
        login_url = self.BASE_URL + self.LOGIN_ENDPOINT

        payload = {
        "username": username,
        "password": password
        }

        res = requests.post(login_url, json=payload)

        if res.status_code != 200:
            raise Exception("Login failed")

        data = res.json()
        self.access_token = data["accessToken"]
    def GetMineID(self):
        url = self.BASE_URL + "/me"
        res = requests.get(url, headers=self.headers)
        if res.status_code != 200:
            raise Exception("Failed to get driver info")
        self.driverID = res.json()["id"]
    def move(self,latitude, longitude):
        url = self.BASE_URL + "/drivers/locations/{driver_id}".format(driver_id=self.driverID)
        payload = {
            "latitude": latitude,
            "longitude": longitude
        }
        res = requests.put(url, json=payload, headers=self.headers)
        if res.status_code != 200:
            raise Exception("Failed to update location")
        