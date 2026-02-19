"""
run.py — spawns one looping worker process per active driver.

Builds the coordinate cache ONCE in the main process before spawning
any workers, then passes it to each worker. Total Nominatim calls = 18,
regardless of how many drivers are running.

Press ENTER or Ctrl+C to stop all workers cleanly.
"""

import sys
import signal
import time
import logging
import multiprocessing

import worker
import routeGenerator

# ── Configuration ─────────────────────────────────────────────────────────────

ACTIVE_DRIVERS = [
    "driver@test.com",
    "driver1@test.com",
    "driver4@test.com",
    "driver5@test.com",
    "driver6@test.com",
    "driver7@test.com",
    "driver8@test.com",
]

DRIVER_PASSWORD = "password"

GRACEFUL_SHUTDOWN_TIMEOUT = 5

NOVI_SAD_LOCATIONS = [
    "Trg slobode, Novi Sad, Serbia",
    "Fakultet tehnickih nauka, Novi Sad, Serbia",
    "Zeleznicka stanica Novi Sad, Serbia",
    "Sajam Novi Sad, Serbia",
    "JGSP Novi Sad, Serbia",
    "Medicinski fakultet Novi Sad, Serbia",
    "Zmaj Jovina, Novi Sad, Serbia",
    "Bulevar Evrope, Novi Sad, Serbia",
    "Grbavica, Novi Sad, Serbia",
    "Novo Naselje, Novi Sad, Serbia",
    "Petrovaradin, Novi Sad, Serbia",
    "Liman, Novi Sad, Serbia",
    "Adamovicevo, Novi Sad, Serbia",
    "Klisa, Novi Sad, Serbia",
    "Bulevar Oslobodjenja, Novi Sad, Serbia",
    "Dunavska, Novi Sad, Serbia",
    "Futog, Novi Sad, Serbia",
    "Slana Bara, Novi Sad, Serbia",
]

# Nominatim allows 1 req/s
GEOCODE_DELAY = 1.1

# ── Logging ───────────────────────────────────────────────────────────────────

logging.basicConfig(
    level=logging.INFO,
    format="[%(asctime)s] [orchestrator] %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger(__name__)

# ── Cache build ───────────────────────────────────────────────────────────────

def build_coordinate_cache() -> dict:
    """
    Geocode every location once. Returns { name: (lat, lon) }.
    18 Nominatim calls total, done once before any worker starts.
    """
    cache = {}
    log.info("Geocoding %d locations (this will take ~%.0fs)…",
             len(NOVI_SAD_LOCATIONS), len(NOVI_SAD_LOCATIONS) * GEOCODE_DELAY)

    for address in NOVI_SAD_LOCATIONS:
        try:
            lat, lon = routeGenerator.geocode_address(address)
            cache[address] = (lat, lon)
            log.info("  Cached: %s -> (%.5f, %.5f)", address, lat, lon)
        except Exception as exc:
            log.warning("  Could not geocode '%s': %s — skipping.", address, exc)
        time.sleep(GEOCODE_DELAY)

    log.info("Cache ready: %d/%d locations geocoded.", len(cache), len(NOVI_SAD_LOCATIONS))
    return cache

# ── Process management ────────────────────────────────────────────────────────

def _start_worker(email: str, coord_cache: dict) -> multiprocessing.Process:
    p = multiprocessing.Process(
        target=worker.run_driver_loop,
        args=(email, DRIVER_PASSWORD, coord_cache),
        name=f"driver-{email}",
        daemon=True,
    )
    p.start()
    log.info("Started worker for %s (PID %d)", email, p.pid)
    return p


def _stop_all(processes: list) -> None:
    log.info("Stopping %d worker(s)…", len(processes))
    for p in processes:
        if p.is_alive():
            p.terminate()

    deadline = time.time() + GRACEFUL_SHUTDOWN_TIMEOUT
    for p in processes:
        remaining = deadline - time.time()
        if remaining > 0:
            p.join(timeout=remaining)
        if p.is_alive():
            log.warning("Force-killing %s (PID %d)", p.name, p.pid)
            p.kill()
            p.join()

    log.info("All workers stopped.")

# ── Entry point ───────────────────────────────────────────────────────────────

def main() -> None:
    coord_cache = build_coordinate_cache()

    if len(coord_cache) < 2:
        log.error("Not enough locations geocoded. Aborting.")
        sys.exit(1)

    processes = [_start_worker(email, coord_cache) for email in ACTIVE_DRIVERS]

    log.info("%d worker(s) running. Press ENTER or Ctrl+C to stop all.", len(processes))

    def _shutdown(signum=None, frame=None):
        print()
        _stop_all(processes)
        sys.exit(0)

    signal.signal(signal.SIGINT,  _shutdown)
    signal.signal(signal.SIGTERM, _shutdown)

    try:
        input()
    except EOFError:
        try:
            while True:
                time.sleep(1)
                for i, p in enumerate(processes):
                    if not p.is_alive():
                        email = p.name.removeprefix("driver-")
                        log.warning("Worker for %s died unexpectedly. Restarting…", email)
                        processes[i] = _start_worker(email, coord_cache)
        except KeyboardInterrupt:
            pass

    _shutdown()


if __name__ == "__main__":
    multiprocessing.freeze_support()
    main()
