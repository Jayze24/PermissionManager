# PermissionManager
### Easily check permission with your own dialog.

https://user-images.githubusercontent.com/32419237/211525309-b5bda646-2b9b-4aae-b508-532ea9213f82.mp4

---------------------------
## Dependency
Include the library in your `settings.gradle`
```kotlin
repositories {
    ....
    maven { url 'https://jitpack.io' }
}
```
Include the library in your `build.gradle`
```kotlin
dependencies {
    implementation 'com.github.Jayze24:PermissionManager:0.0.1'
}
```
## Usage
### Check permission
To start using the library you need to call `PermissionManagere.Build(context)` and set permission you need. after that just call `check()` at the end of builder :
```kotlin
PermissionManager.Builder(applicationContext)
    .setPermission(Manifest.permission.CAMERA)
    .setListenerResult { arrayDeniedPermission, isGranted -> 
        // The result of the permission request is here.
        // arrayDeniedPermission(Array) returns an array of denied permissions.
        // isGranted(Boolean) returns the result of the permission request.
    }
    .check()
```
### Set up your dialog
There are three listeners that can display your dialog.
* `setListenerShowDialogBeforeRequest` : Triggered before requesting permission. You must call `startRequest()` to go to the next step when the dialog closes.
* `setListenerShowDialogBeforeSecondRequest` : Triggered when [shouldShowRequestPermissionRationale](https://developer.android.com/reference/androidx/core/app/ActivityCompat#shouldShowRequestPermissionRationale(android.app.Activity,java.lang.String), "android developers") is true. shouldShowRequestPermissionRationale is checked after the first permission request is denied. You must call `startRequest()` to go to the next step when the dialog closes.
* `setListenerShowDialogAfterFinalDenied` : Triggered after the last permission request has been denied. You must call 'showSetting(Boolean)' when the button is pressed or when the popup closes to proceed to the next step. If 'true', the setting screen is displayed. If 'false', the setting screen is skipped.
```kotlin
PermissionManager.Builder(applicationContext)
    .setPermission(Manifest.permission.CAMERA)
    .setListenerShowDialogBeforeRequest { arrayDeniedPermission, startRequest ->
        // Set your custom dialog here. This listener is triggered before permission is requested.
        // The most important thing is to call 'startRequest()' to go to the next step when the dialog closes. 
        // ex)
        AlertDialog.Builder(this)
            .setMessage("This permission is needed to ...")
            .setPositiveButton("Confirm", null)
            .setOnDismissListener { startRequest() }
            .show()
    }
    .setListenerShowDialogBeforeSecondRequest { arrayDeniedPermission, startRequest ->
        // Set your custom dialog here. This listener is triggered after the first permission request is denied.
        // The most important thing is to call 'startRequest()' to go to the next step when the dialog closes.
        // ex)
        AlertDialog.Builder(this)
            .setMessage("This permission is needed to ...")
            .setPositiveButton("Confirm", null)
            .setOnDismissListener { startRequest() }
            .show()
    }
    .setListenerShowDialogAfterFinalDenied { arrayDeniedPermission, showSetting ->
        // Set your custom dialog here. This listener is triggered after the final permission request is denied.
        // You must call 'showSetting(Boolean)' when the button is pressed or when the popup closes to proceed to the next step.
        // If 'true', the setting screen is displayed. If 'false', the setting screen is skipped.
        // ex)
        AlertDialog.Builder(this)
            .setMessage("Please turn on permission at setting ...")
            .setPositiveButton("Setting") { _, _ -> showSetting(true) }
            .setNegativeButton("Close") { _,_ -> showSetting(false) }
            .setOnDismissListener { showSetting(false) }
            .show()
    }
    .setListenerResult { arrayDeniedPermission, isGranted -> 
        // The result of the permission request is here.
        // arrayDeniedPermission(Array) returns an array of denied permissions.
        // isGranted(Boolean) returns the result of the permission request.
    }
    .check()
```
### Targeting by SDK version
You can set min/max sdk version in `setPermissionWithTarget`.
```kotlin
@SuppressLint("InlinedApi")
private fun checkPermissionReadStorage() {
    PermissionManager.Builder(applicationContext)
        .setPermissionWithTarget(Manifest.permission.READ_EXTERNAL_STORAGE, maxSdk = Build.VERSION_CODES.S_V2)
        .setPermissionWithTarget(Manifest.permission.READ_MEDIA_IMAGES, minSdk = Build.VERSION_CODES.TIRAMISU)
        .setListenerResult { arrayDeniedPermission, isGranted ->
            // The result of the permission request is here.
            // arrayDeniedPermission(Array) returns an array of denied permissions.
            // isGranted(Boolean) returns the result of the permission request.
        }
        .check()
}
```
