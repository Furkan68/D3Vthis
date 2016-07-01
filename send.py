from urllib2 import *
import urllib
import json
import time

MY_API_KEY="AIzaSyDIUWxH_rJJvY5Y6RMSmmRBFou4E0H-8lo"
REQUEST_URL = "http://furkancetin.nl/data/?requested=ask"
CHECK_URL = "http://furkancetin.nl/data/"
REQUEST_URL_FALSE = "http://furkancetin.nl/data/?requested=false"

messageTitle = "Beschikbare parkeerplaats!"
messageBody = "U kunt navigeren naar de parkeerplaats"

data={
    "to" : "/topics/my_little_topic",
    "notification" : {
        "body" : messageBody,
        "title" : messageTitle,
        "icon" : "park_green_48",
		"sound": "default"
    }
}

dataAsJSON = json.dumps(data)

request = Request(
    "https://gcm-http.googleapis.com/gcm/send",
    dataAsJSON,
    { "Authorization" : "key="+MY_API_KEY,
      "Content-type" : "application/json"
    }
)
try:
	while True:
	
		request_response = urllib.urlopen(REQUEST_URL)
		request_data = json.loads(request_response.read())
		
		check_response = urllib.urlopen(CHECK_URL)
		check_data = json.loads(check_response.read())
		
		if request_data['data'][0]['requested']:
			if check_data['data'][0]['available']:
				print urlopen(request).read()
				urllib.urlopen(REQUEST_URL_FALSE)
except KeyboardInterrupt:
        print("Measurement stopped by User")

