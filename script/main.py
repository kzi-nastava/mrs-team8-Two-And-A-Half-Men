import MovingInRoute
import sys

import sys

if len(sys.argv) != 5:
    print("Usage: python script.py <firstAddress> <secondAddress> <username> <password>")
    sys.exit(1)

firstAddress = sys.argv[1]
secondAddress = sys.argv[2]
username = sys.argv[3]
password  = sys.argv[4]
print(f"Moving from {firstAddress} to {secondAddress} with user {username} and password {password}")

MovingInRoute.move_along_route(firstAddress, secondAddress, username, password )
