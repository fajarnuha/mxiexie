# mxiexie

mxiexie is a mobile application that allows you to display QR codes for quick access to MCC Building. 
This project was born out of a desire to enhance the user experience of the official application. This alternative app offers several usability improvements, including:
- Offline functionality
- Optimized network fetching
- Persistent floor access
- An improved user experience through the use of Group Chips instead of dropdown selections.

<video src="/asset/demo.webm" controls="controls" style="max-width: 720px;"></video>

## Overview

This application is a client for a service that provides access data in the form of QR codes. After logging in, you can see a list of your access and select one to display the corresponding QR code. This is useful for quick access to services that use QR codes for authentication or access control.

Your user data, including login credentials and access information, is stored locally on your device and is only sent to the official service for authentication and data retrieval. This application does not save or transmit your data to any other third-party servers.

## Development Process

The development of this application involved several key stages:
*   **Reverse Engineering:** The original application's behavior and API interactions were analyzed using [JADX](https://github.com/skylot/jadx) to decompile its Android APK. This helped in understanding the underlying mechanisms.
*   **Cross-Platform Development:** The application was developed using Kotlin Multiplatform (KMP), allowing for shared business logic across Android and iOS platforms, leading to efficient development and maintenance.
*   **AI-Assisted Coding:** A significant portion of the codebase was developed with the assistance of Android Studio Agent Mode and Jules, leveraging AI to accelerate development and improve code quality.

## Technology Stack

This project is built with modern technologies for cross-platform development:

*   **Kotlin Multiplatform:** The core logic of the application is written in Kotlin and shared between Android and iOS, ensuring consistency and faster development.
*   **Jetpack Compose:** The user interface is built with Jetpack Compose, a modern declarative UI toolkit for building native Android and iOS apps from a single codebase.
*   **Ktor:** For handling network requests, we use Ktor, a lightweight and asynchronous HTTP client for Kotlin.
*   **Datastore Preference Multiplatform:** This library is used for persisting data locally on both Android and iOS.

## Getting the Latest Version

You can download the latest version of the application from the GitHub Releases page.

### Android

1.  Open the [GitHub Releases](https://github.com/FajarNuha/MCC-Plus/releases) page.
2.  Find the latest release and download the `.apk` file.
3.  Open the downloaded file on your Android device to install the application. You may need to allow installations from unknown sources in your device's settings.

### iOS (WIP) <img src="https://img.shields.io/badge/WIP-yellow?style=flat-square" alt="WIP"/> [#2](https://github.com/fajarnuha/mxiexie/issues/2)

1.  Open the [GitHub Releases](https://github.com/FajarNuha/MCC-Plus/releases) page.
2.  Find the latest release and download the `.ipa` file.
3.  To install the `.ipa` file on your iOS device, you can use [AltStore](https://altstore.io/) or other similar tools. Please note that you may need a developer account to sign the application.

## Building from Source

If you want to build the application from the source code, you will need to have the following tools installed:

*   Android Studio
*   Xcode (for the iOS app)
*   Kotlin Multiplatform Mobile plugin

Once you have the necessary tools, you can clone the repository and open it in Android Studio. From there, you can build and run the application on your devices or emulators.
