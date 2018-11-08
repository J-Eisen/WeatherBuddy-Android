# WeatherBuddy-Android
The Java based Android sibling of [WeatherBuddy for iOS](https://github.com/J-Eisen/WeatherBuddy-iOS/).
WeatherBuddy is intended to remove the middle-person of checking the weather. Instead of giving you raw data, it tells you what to wear using a fairly simple algorithm that *you* control. If, unlike me, you prefer to grab a sweatshirt when the temperature is 70˚F, you can adjust it for that.

Additionally, the main way that WeatherBuddy finds your location is not using GPS or a default zipcode (though it *is* able to use those). Instead, it reads your calendar for when you will be traveling from one location to another. For user privacy, it only grabs location and time information.

## Features
* Partial Testing Suite
* Can find the gaps in calendar data
* Can output boolean results from weather data
* User Settings
## Under Development
* Full testing suite
  * JSON parsing test
  * Calendar parsing test
* Graphics
  * Default window will be a simple representation of outfits
  * Various articles on the outfit will be able to be tapped for more info
    
    (e.g. if you tap the buddy's sweatshirt, the temperature of 70˚ will appear)
* Save States
  * User settings will be saved
  * Previous (up to 24 hours) weather data will be saved
    
    This is to minimize its data use and maximize apparent speed
