import MovingInRoute
import os

startAddress = os.getenv("START_ADDRESS")
endAddress = os.getenv("END_ADDRESS")
userEmail = os.getenv("USER_EMAIL")
userPassword = os.getenv("USER_PASSWORD")

MovingInRoute.move_along_route(startAddress, endAddress, userEmail, userPassword)
