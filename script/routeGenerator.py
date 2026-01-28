import requests
import math

def haversine(p1, p2):
    R = 6371000  # meters
    lat1, lon1 = math.radians(p1[0]), math.radians(p1[1])
    lat2, lon2 = math.radians(p2[0]), math.radians(p2[1])

    dlat = lat2 - lat1
    dlon = lon2 - lon1

    a = math.sin(dlat/2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon/2)**2
    return 2 * R * math.atan2(math.sqrt(a), math.sqrt(1 - a))


def interpolate(p1, p2, t):
    return (
        p1[0] + (p2[0] - p1[0]) * t,
        p1[1] + (p2[1] - p1[1]) * t
    )


def sample_every_20m(coords):
    sampled = []
    leftover = 0

    for i in range(len(coords) - 1):
        p1 = (coords[i][1], coords[i][0])     # (lat, lon)
        p2 = (coords[i+1][1], coords[i+1][0])

        segment_dist = haversine(p1, p2)
        d = leftover

        while d < segment_dist:
            t = d / segment_dist
            sampled.append(interpolate(p1, p2, t))
            d += 20

        leftover = d - segment_dist

    return sampled


def get_route_points(lat1, lon1, lat2, lon2):
    url = f"https://router.project-osrm.org/route/v1/driving/{lon1},{lat1};{lon2},{lat2}?overview=full&geometries=geojson"
    r = requests.get(url)
    r.raise_for_status()

    data = r.json()
    coords = data["routes"][0]["geometry"]["coordinates"]
    return sample_every_20m(coords)

def geocode_address(address):
    url = "https://nominatim.openstreetmap.org/search"
    params = {
        "q": address,
        "format": "json",
        "limit": 1
    }
    headers = {
        "User-Agent": "PythonRouteScript/1.0"  # Nominatim requires user-agent
    }

    r = requests.get(url, params=params, headers=headers)
    r.raise_for_status()
    data = r.json()
    
    if not data:
        raise ValueError(f"Address not found: {address}")
    
    lat = float(data[0]["lat"])
    lon = float(data[0]["lon"])
    return lat, lon


def get_route_points_address(start_address, end_address):
    lat1, lon1 = geocode_address(start_address)
    lat2, lon2 = geocode_address(end_address)
    return get_route_points(lat1, lon1, lat2, lon2)