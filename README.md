# NASA Image of the Day
Android application that shows the nasa picture of the day.

The application downloads the NASA Picture of the day RSS and uses a personalized SAX parser to retrieve the following informations:
* Image title
* Image date
* Image jpg
* Image Description

#### Funtionalities

The application presents two main functionalities:
* **Refresh:** Hit `Refresh` and download new RSS.<br> Shows a Progress dialog during process.
  Makes use of threads to keep UI process lightweight.
* **Set Wallpaper:** Hit `Set Wallpaper` and change your homescreen background!. <br>
  Shows a Toast notification when set.

