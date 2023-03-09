# Spyware Weather App 

## Displays weather, track users location and dump contacts to database

> The goal is to create a normal looking app and  
> track users location and dump contacts to a database without their knowledge.   

## About
> The app lets you register/login with using SQLiteDatabase  
> When logged in it will ask for permissions.
> If the user accepts the permissions it will display the weather forecast for the users current location.  
> and start tracking the users location and dump the contacts to a database without their knowledge.

## Features
- Login/register
- Weather forecast (using OpenWeatherMap API) on current location
- Location tracking (using FusedLocationProviderClient)
- Contacts dump
- Firebase realtime database
- Firebase datastore

> This application was a school project build using Kotlin.

## Run it yourself

- Clone the repo
- Create a api key from https://openweathermap.org/api and add in the code
- Create a firebase realtime database and firebase datastore
- In the firebase datastore console start a collection named "users"
- Add the config in the app folder (google-services.json)
- Run the app (NOTE: You need to have a device with Android 13)

> NOTE: When running the app make sure your location is turned on and that you have internet connection.  
> NOTE: When location is not working, open google maps and locate you device. Then try again.  

> More info on how to connect to firebase can be found here: https://firebase.google.com/docs/android/setup

