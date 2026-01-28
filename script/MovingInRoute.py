print("Moving in a route...")

import movment
import time 
import routeGenerator 
def move_along_route(startAddress , endAddress, username="driver@test.com", password="password"):
    mov = movment.Movment(username, password)
    print("Starting movement along the route...")
    route_points = routeGenerator.get_route_points_address(startAddress, endAddress)
    print(f"Route has {len(route_points)} points.")
    for point in route_points:
        latitude, longitude = point
        response = mov.move(latitude, longitude)
        print(f"Moved to: {latitude}, {longitude} | Response: {response}")
        time.sleep(0.5)  