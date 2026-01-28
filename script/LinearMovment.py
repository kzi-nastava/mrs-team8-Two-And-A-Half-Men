import movment
import time

mov = movment.Movment()
start_latitude = 45.23816
start_longitude = 19.82968
mov.move(start_latitude, start_longitude)
for i in range(10):
    new_latitude = start_latitude + (i * 0.00001)
    new_longitude = start_longitude + (i * 0.00001)
    response = mov.move(new_latitude, new_longitude)
    print(f"Moved to: {new_latitude}, {new_longitude} | Response: {response}")
    time.sleep(0.3)  # Wait for 0.3 second before the next move