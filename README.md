This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

## Architecture Overview

This project follows a common Kotlin Multiplatform architecture:

*   **Shared UI:** The UI is built with Compose Multiplatform and resides in `/composeApp/src/commonMain`. This allows for a single codebase for the user interface across Android and iOS.
*   **ViewModels:** ViewModels are also located in `/composeApp/src/commonMain` and handle the presentation logic for the UI. They interact with repositories to fetch and manage data.
*   **Data Layer:** The data layer, including repositories and network clients, is primarily in `/composeApp/src/commonMain`. It's responsible for abstracting data sources (network, local database, etc.).
*   **Platform-Specific Implementations:** For platform-specific functionalities (like showing a Toast on Android or a UIAlertController on iOS), we use the `expect`/`actual` mechanism. `expect` declarations are in `commonMain`, and `actual` implementations are in the respective platform-specific source sets (e.g., `androidMain`, `iosMain`).

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…