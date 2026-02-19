"""
worker.py — runs in a separate process for each active driver.

Receives a pre-built coordinate cache from the orchestrator so
Nominatim is never called inside a worker at all.
"""

import random
import time
import logging
import movment
import routeGenerator

STEP_DELAY = 0.5


def run_driver_loop(email: str, password: str, coord_cache: dict) -> None:
    logging.basicConfig(
        level=logging.INFO,
        format=f"[%(asctime)s] [{email}] %(message)s",
        datefmt="%H:%M:%S",
    )
    log = logging.getLogger(__name__)

    location_names = list(coord_cache.keys())

    try:
        mov = movment.Movment(email, password)
    except Exception as exc:
        log.error("Login failed: %s", exc)
        return

    current_name: str = random.choice(location_names)
    last_point = None  # (lat, lon) of the last GPS point sent

    while True:
        dest_name = random.choice([n for n in location_names if n != current_name])
        log.info("Route: %s  ->  %s", current_name, dest_name)

        try:
            lat1, lon1 = last_point if last_point is not None else coord_cache[current_name]
            lat2, lon2 = coord_cache[dest_name]

            route_points = routeGenerator.get_route_points(lat1, lon1, lat2, lon2)
            log.info("Route has %d points.", len(route_points))

            for lat, lon in route_points:
                try:
                    mov.move(lat, lon)
                except Exception as exc:
                    log.warning("move() failed: %s — retrying in 2s", exc)
                    time.sleep(2)
                    try:
                        mov.move(lat, lon)
                    except Exception:
                        pass

                last_point = (lat, lon)
                time.sleep(STEP_DELAY)

            log.info("Reached %s. Picking next destination…", dest_name)
            current_name = dest_name

        except Exception as exc:
            log.error("Route failed: %s. Retrying in 5s.", exc)
            last_point = None
            time.sleep(5)
