# Ticketmaster App Clone

An Android application that lets users discover and save local events using the [Ticketmaster API](https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/). The app features real-time location-based event filtering powered by the **Google Maps SDK**, allowing users to visually explore events near them. It includes **Firebase Authentication** for user sign-in and **Firestore Database** support for saving events to a personal list. The UI is built using **Android’s native view system**, with dynamic event cards and an interactive map.


## Features

- Search for nearby events using the Ticketmaster Discovery API
- View events on a live Google Map based on current location
- Sign up or log in using Firebase Authentication
- Save events to your personal list via Firestore
- Clean, dynamic UI built using Android's native XML views

## Getting Started

To run the app on your local machine:

1. **Clone the repository**  
   ```bash
   git clone https://github.com/shahqureshi2727/Ticketmaster-App-Clone.git
   cd Ticketmaster-App-Clone
   ```

2. **Open the project in Android Studio**

3. **Obtain a Google Maps API Key**  
   - Go to the [Google Cloud Console](https://console.cloud.google.com/)
   - Create a project (or use an existing one)
   - Enable the **Maps SDK for Android**
   - Generate an API Key

4. **Add your Google Maps SDK API Key**  
   In `app/src/main/AndroidManifest.xml`, replace the placeholder:
   ```kotlin
   android.value="YOUR API KEY"
   ```
5. **Add your Ticketmaster Discover API Key**
   - Go to the [Ticketmaster API](https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/)
   - Create an App (or use an existing one)
   - You will then be provided with a Consumer Key
   - In `app/src/main/java/com/example/finalprojectv2/ui/search/SearchTicketFragment.kt`, replace the placeholder:
   ```kotlin
   ticketAPI.getTickets("YOUR API KEY", city, keyword, "date,asc").enqueue(object : Callback<TicketData?> { ... }
   ```

6. **Run the app** on a device or emulator with location services enabled.
