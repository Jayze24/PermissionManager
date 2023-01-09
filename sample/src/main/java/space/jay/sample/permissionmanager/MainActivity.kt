package space.jay.sample.permissionmanager

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import space.jay.permissionmanager.PermissionManager
import space.jay.sample.permissionmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initButton()
    }

    private fun initButton() {
        binding.buttonLocation.setOnClickListener { checkPermissionLocation() }
        binding.buttonReadStorage.setOnClickListener { checkPermissionReadStorage() }
        binding.buttonCamera.setOnClickListener { checkPermissionCamera() }
        binding.buttonOverlay.setOnClickListener { checkPermissionOverlay() }
        binding.buttonRecordAudio.setOnClickListener { checkPermissionRecordAudio() }
        binding.buttonPostNotificationsAndReadContacts.setOnClickListener { checkPermissionPostNotificationsAndReadContacts() }
    }

    private fun checkPermissionLocation() {
        PermissionManager.Builder(applicationContext)
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .setPermissionWithTarget(Manifest.permission.ACCESS_BACKGROUND_LOCATION, minSdk = Build.VERSION_CODES.Q)
            .setListenerShowDialogBeforeRequest { arrayDeniedPermission, startRequest ->
                printLog("button Location", "ShowDialogBeforeRequest", arrayDeniedPermission)
                yourCustomDialogFirstStartRequest(startRequest)
            }
            .setListenerShowDialogBeforeSecondRequest { arrayDeniedPermission, startRequest ->
                printLog("button Location", "ShowDialogBeforeSecondRequest", arrayDeniedPermission)
                yourCustomDialogSecondStartRequest(startRequest)
            }
            .setListenerShowDialogAfterFinalDenied { arrayDeniedPermission, showSetting ->
                printLog("button Location", "ShowDialogAfterFinalDenied", arrayDeniedPermission)
                yourCustomDialogShowSetting(showSetting)
            }
            .setListenerResult { arrayDeniedPermission, isGranted -> printLog("button Location", "Result($isGranted)", arrayDeniedPermission) }
            .check()
    }

    @SuppressLint("InlinedApi")
    private fun checkPermissionReadStorage() {
        PermissionManager.Builder(applicationContext)
            .setPermissionWithTarget(Manifest.permission.READ_EXTERNAL_STORAGE, maxSdk = Build.VERSION_CODES.S_V2)
            .setPermissionWithTarget(Manifest.permission.READ_MEDIA_IMAGES, minSdk = Build.VERSION_CODES.TIRAMISU)
            .setPermissionWithTarget(Manifest.permission.READ_MEDIA_VIDEO, minSdk = Build.VERSION_CODES.TIRAMISU)
            .setPermissionWithTarget(Manifest.permission.READ_MEDIA_AUDIO, minSdk = Build.VERSION_CODES.TIRAMISU)
            .setListenerShowDialogBeforeRequest { arrayDeniedPermission, startRequest ->
                printLog("button ReadStorage", "ShowDialogBeforeSecondRequest", arrayDeniedPermission)
                yourCustomDialogFirstStartRequest(startRequest)
            }
            .setListenerResult { arrayDeniedPermission, isGranted -> printLog("button ReadStorage", "Result($isGranted)", arrayDeniedPermission) }
            .check()
    }

    private fun checkPermissionCamera() {
        PermissionManager.Builder(applicationContext)
            .setPermission(Manifest.permission.CAMERA)
            .setListenerShowDialogBeforeSecondRequest { arrayDeniedPermission, startRequest ->
                printLog("button Camera", "ShowDialogBeforeSecondRequest", arrayDeniedPermission)
                yourCustomDialogSecondStartRequest(startRequest)
            }
            .setListenerResult { arrayDeniedPermission, isGranted -> printLog("button Camera", "Result($isGranted)", arrayDeniedPermission) }
            .check()
    }

    private fun checkPermissionOverlay() {
        PermissionManager.Builder(applicationContext)
            .setPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .setListenerShowDialogAfterFinalDenied { arrayDeniedPermission, showSetting ->
                printLog("button Overlay", "ShowDialogAfterFinalDenied", arrayDeniedPermission)
                yourCustomDialogShowSetting(showSetting)
            }
            .setListenerResult { arrayDeniedPermission, isGranted -> printLog("button Overlay", "Result($isGranted)", arrayDeniedPermission) }
            .check()
    }

    private fun checkPermissionRecordAudio() {
        PermissionManager.Builder(applicationContext)
            .setPermission(Manifest.permission.RECORD_AUDIO)
            .setListenerShowDialogBeforeRequest { arrayDeniedPermission, startRequest ->
                printLog("button RecordAudio", "ShowDialogBeforeRequest", arrayDeniedPermission)
                yourCustomDialogFirstStartRequest(startRequest)
            }
            .setListenerShowDialogBeforeSecondRequest { arrayDeniedPermission, startRequest ->
                printLog("button RecordAudio", "ShowDialogBeforeSecondRequest", arrayDeniedPermission)
                yourCustomDialogSecondStartRequest(startRequest)
            }
            .setListenerResult { arrayDeniedPermission, isGranted -> printLog("button RecordAudio", "Result($isGranted)", arrayDeniedPermission) }
            .check()
    }

    @SuppressLint("InlinedApi")
    private fun checkPermissionPostNotificationsAndReadContacts() {
        PermissionManager.Builder(applicationContext)
            .setPermissionWithTarget(Manifest.permission.POST_NOTIFICATIONS, minSdk = Build.VERSION_CODES.TIRAMISU)
            .setPermission(Manifest.permission.READ_CONTACTS)
            .setListenerShowDialogBeforeSecondRequest { arrayDeniedPermission, startRequest ->
                printLog("button PostNotificationsAndReadContacts", "ShowDialogBeforeSecondRequest", arrayDeniedPermission)
                yourCustomDialogSecondStartRequest(startRequest)
            }
            .setListenerShowDialogAfterFinalDenied { arrayDeniedPermission, showSetting ->
                printLog("button PostNotificationsAndReadContacts", "ShowDialogAfterFinalDenied", arrayDeniedPermission)
                yourCustomDialogShowSetting(showSetting)
            }
            .setListenerResult { arrayDeniedPermission, isGranted -> printLog("button PostNotificationsAndReadContacts", "Result($isGranted)", arrayDeniedPermission) }
            .check()
    }

    private fun yourCustomDialogFirstStartRequest(startRequest: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("First Permission Request")
            .setMessage("This permission is needed to ...")
            .setPositiveButton("Confirm", null)
            .setOnDismissListener { startRequest() }
            .show()
    }

    private fun yourCustomDialogSecondStartRequest(startRequest: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Second Permission Request")
            .setMessage("This permission is needed to ...")
            .setPositiveButton("Confirm", null)
            .setOnDismissListener { startRequest() }
            .show()
    }

    private fun yourCustomDialogShowSetting(showSetting: (Boolean) -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Show Setting")
            .setMessage("Please turn on permission at setting ...")
            .setPositiveButton("Setting") { _, _ -> showSetting(true) }
            .setNegativeButton("Close") { _,_ -> showSetting(false) }
            .setOnDismissListener { showSetting(false) }
            .show()
    }

    private fun printLog(name: String, tag: String, denied: Array<String>) {
        Log.d(
            "PERMISSION MANAGER",
            "---------------------------"+
            "\n${name.uppercase()}" +
            "\ntag = $tag" +
            "\ndenied = ${denied.toList()}" +
            "\n---------------------------"
        )
    }
}