# Webserver

WORKING:

	POST:
		Adding new device via POST at /devices
		Adding a new reading via POST at /devices/current
	
	GET:
		Getting all devices info at /devices
		Getting devices info at /devices/{id} or 
					/devices/current
		Getting a device's readings at /devices/{id}/readings or
						/devices/current/readings
		Getting a location's readings at /locations/{id}/readings or
		Getting all locations at /locations
NOT WORKING:
    
	
TODOLIST:

	When inserting a new device or location check whether it already
	exists or not.
	
	When inserting a non-valid ID it should send a 404 error!
	
	
