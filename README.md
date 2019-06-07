# Weather App

This is a sample weather app to demonstrate a clean architecture for building apps on android. This is only a sample app and you need to apply for api from https://openweathermap.org to run this project.

Users can choose their favorite cities which they want to monitor from a preselected list cities or they can choose to detect location of the phone automatically. Weather will be stored in the database and will be refreshed every time user opens the app with a threshold interval which can be adjusted in the project settings

# Project Architecture

* Kotlin is the main programming language in the project.
* This project is based on clean architecture.
* Business Logic is separated out from View Layer and can be tested separately independent of View Layer
* Dagger2 is used to inject dependencies.
* Persistence is provided by ROOM
* Android ViewModel library is used for implementing MVVM architecture.
* ViewModel uses Android Live Data to update the changes is UI, Live data is used because it is lifecycle aware
* Updates from Repository Layer to ViewModels is done using RxJava2
* DataBinding is used to propagate changes in the UI
* Dependency stubs are provided by Mockito for testing

## Getting Started

Clone the project and open it in android studio. Add the api key in the project's `local.properties` file

```
api.key="<YOUR API KEY>"
```
### Prerequisites

*  Android Studio > 3.0
*  Open Weather Map API key

## Automated Tests

This project contains UI tests as well as Unit Tests. Units tests are run using JUnit and UI Tests are using AndroidJUnitRunner. Both of these can be run  easily either from android studio or command line.

## UI Tests
You need to disable animations to run UI tests. This can be done programmatically if you are using emulator or a phone with root access, just uncomment/add the following lines in your app level gradle file and run tests using Android Studio.

```
def adb = android.getAdbExe().toString()

afterEvaluate {
    task grantAnimationPermissionDev(type: Exec, dependsOn: 'installDebug') {
        commandLine "$adb shell pm grant $android.defaultConfig.applicationId android.permission.SET_ANIMATION_SCALE".split(' ')
    }

    tasks.each { task ->
        if (task.name.startsWith('assembleDebugAndroidTest')) {
            task.dependsOn grantAnimationPermissionDev
        }
    }
}
```

## Built With

* [RxJava](https://github.com/ReactiveX/RxJava) - Reactive framework used in the app
* [Dagger2](https://github.com/google/dagger) - Dependency Injection Management
* [Retrofit](https://square.github.io/retrofit/) - Networking client for network calls
* [Room](https://developer.android.com/topic/libraries/architecture/room) - Database used for persistence
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Helper framework to implement MVVM architecture
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Lifecycle aware observable framework
* [RxLocation](https://github.com/patloew/RxLocation) - Reactive location library
* [RxPermissions](https://github.com/tbruyelle/RxPermissions) - Reactive Permissions library
* [Mockito](https://site.mockito.org/) - Used for mocking classes for testing
* [Espresso](https://developer.android.com/training/testing/espresso) - Android UI testing framework

