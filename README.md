# Homework3_RoyDigiCesp

This is a group project for Android Development class written by Chris Roy, Taylor Digilio, and Tomas Cespedes. The purpose of this assignment was to utilize the GPS sensor provided on a Samsung Device. The device that this app was specifically designed for is a Samsung Galaxy Tab S2. The app determines the total distance from when the user hits start to when the user hits stop. When the user initially opens the app, they are shown their current coordinates (latitude, longitude), X acceleration, Y acceleration, and Instantaneous velocity. Once the user hits the start button, they have the option to "Drop a Pin" which acts as a checkpoint. The pin is stored in a ScrollView where the user can see the latitude, longitude, Distance from that pin to the previous pin dropped, the velocity between the two pins, and the direction from the previous pin (N, S, E, W). The user can drop as many pins as he desires and if the pins were to overflow onto the buttons it lets the user scroll through them vertically so that there is no issue of overlapping UI. Once the user decides to hit stop, it tells the user the total distance from point A (start) through the pins all the way to point B (stop). On top of that, it also calculates the total velocity between the points. The user after that has the option to hit the "Reset" button and can restart with no data.


# Home Screen when app opens.
![image](https://user-images.githubusercontent.com/35609863/47691989-9edd9a80-dbca-11e8-9b8d-04949ebc713c.png)

# User hits Start Button
![image](https://user-images.githubusercontent.com/35609863/47692001-a43ae500-dbca-11e8-9f99-aafa1ce75784.png)

# User drops pins 
![image](https://user-images.githubusercontent.com/35609863/47692006-a8670280-dbca-11e8-8b1d-d94e8fd175bb.png)

# User hits stop button
![image](https://user-images.githubusercontent.com/35609863/47692011-ac932000-dbca-11e8-812f-f5cb52920849.png)
