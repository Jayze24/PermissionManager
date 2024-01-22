package space.jay.sample.permissionmanager

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import space.jay.permissionmanager.PermissionManager
import space.jay.sample.permissionmanager.databinding.ActivityMainBinding
import space.jay.sample.permissionmanager.databinding.DialogRequestPermissionBinding
import space.jay.sample.permissionmanager.databinding.DialogRequestPermissionSettingBinding

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

    @SuppressLint("InlinedApi")
    private fun checkPermissionLocation() {
        PermissionManager.Builder(applicationContext)
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .setPermissionWithTarget(Manifest.permission.ACCESS_BACKGROUND_LOCATION, minSdk = Build.VERSION_CODES.Q)
            .setListenerShowDialogBeforeRequest { arrayDeniedPermission, startRequest ->
                printLog("Location", "ShowDialogBeforeRequest", arrayDeniedPermission)
                yourCustomDialogFirstStartRequest(startRequest, R.raw.dialog_location)
            }
            .setListenerShowDialogBeforeSecondRequest { arrayDeniedPermission, startRequest ->
                printLog("Location", "ShowDialogBeforeSecondRequest", arrayDeniedPermission)
                yourCustomDialogSecondStartRequest(startRequest)
            }
            .setListenerShowDialogAfterFinalDenied { arrayDeniedPermission, showSetting ->
                printLog("Location", "ShowDialogAfterFinalDenied", arrayDeniedPermission)
                yourCustomDialogShowSetting(showSetting)
            }
            .setListenerResult { arrayDeniedPermission, isGranted ->
                printLog("Location", "Result($isGranted)", arrayDeniedPermission)
                showToast("Location", isGranted)
            }
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
                printLog("ReadStorage", "ShowDialogBeforeSecondRequest", arrayDeniedPermission)
                yourCustomDialogFirstStartRequest(startRequest, R.raw.dialog_storage)
            }
            .setListenerResult { arrayDeniedPermission, isGranted ->
                printLog("ReadStorage", "Result($isGranted)", arrayDeniedPermission)
                showToast("ReadStorage", isGranted)
            }
            .check()
    }

    private fun checkPermissionCamera() {
        PermissionManager.Builder(applicationContext)
            .setPermission(Manifest.permission.CAMERA)
            .setListenerShowDialogBeforeSecondRequest { arrayDeniedPermission, startRequest ->
                printLog("Camera", "ShowDialogBeforeSecondRequest", arrayDeniedPermission)
                yourCustomDialogSecondStartRequest(startRequest)
            }
            .setListenerResult { arrayDeniedPermission, isGranted ->
                printLog("Camera", "Result($isGranted)", arrayDeniedPermission)
                showToast("Camera", isGranted)
            }
            .check()
    }

    private fun checkPermissionOverlay() {
        PermissionManager.Builder(applicationContext)
            .setPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .setListenerShowDialogAfterFinalDenied { arrayDeniedPermission, showSetting ->
                printLog("Overlay", "ShowDialogAfterFinalDenied", arrayDeniedPermission)
                yourCustomDialogShowSetting(showSetting)
            }
            .setListenerResult { arrayDeniedPermission, isGranted ->
                printLog("Overlay", "Result($isGranted)", arrayDeniedPermission)
                showToast("Overlay", isGranted)
            }
            .check()
    }

    private fun checkPermissionRecordAudio() {
        PermissionManager.Builder(applicationContext)
            .setPermission(Manifest.permission.RECORD_AUDIO)
            .setListenerShowDialogBeforeRequest { arrayDeniedPermission, startRequest ->
                printLog("RecordAudio", "ShowDialogBeforeRequest", arrayDeniedPermission)
                yourCustomDialogFirstStartRequest(startRequest, R.raw.dialog_record)
            }
            .setListenerShowDialogBeforeSecondRequest { arrayDeniedPermission, startRequest ->
                printLog("RecordAudio", "ShowDialogBeforeSecondRequest", arrayDeniedPermission)
                yourCustomDialogSecondStartRequest(startRequest)
            }
            .setListenerResult { arrayDeniedPermission, isGranted ->
                printLog("RecordAudio", "Result($isGranted)", arrayDeniedPermission)
                showToast("RecordAudio", isGranted)
            }
            .check()
    }

    @SuppressLint("InlinedApi")
    private fun checkPermissionPostNotificationsAndReadContacts() {
        PermissionManager.Builder(applicationContext)
            .setPermissionWithTarget(Manifest.permission.POST_NOTIFICATIONS, minSdk = Build.VERSION_CODES.TIRAMISU)
            .setPermission(Manifest.permission.READ_CONTACTS)
            .setListenerShowDialogBeforeSecondRequest { arrayDeniedPermission, startRequest ->
                printLog("PostNotificationsAndReadContacts", "ShowDialogBeforeSecondRequest", arrayDeniedPermission)
                yourCustomDialogSecondStartRequest(startRequest)
            }
            .setListenerShowDialogAfterFinalDenied { arrayDeniedPermission, showSetting ->
                printLog("PostNotificationsAndReadContacts", "ShowDialogAfterFinalDenied", arrayDeniedPermission)
                yourCustomDialogShowSetting(showSetting)
            }
            .setListenerResult { arrayDeniedPermission, isGranted ->
                printLog("PostNotificationsAndReadContacts", "Result($isGranted)", arrayDeniedPermission)
                showToast("PostNotificationsAndReadContacts", isGranted)
            }
            .check()
    }

    private fun yourCustomDialogFirstStartRequest(startRequest: () -> Unit, animation: Int) {
        Dialog(this).apply {
            val view : DialogRequestPermissionBinding = DialogRequestPermissionBinding.inflate(layoutInflater, null, false)
            view.lottieAnimation.setAnimation(animation)
            view.textViewTitle.text = "1. Permission Request"
            view.textViewMessage.text = "This permission is needed to ..."
            view.buttonConfirm.setOnClickListener { this.dismiss() }
            setContentView(view.root)
            setOnDismissListener { startRequest() }
            show()
        }
    }

    private fun yourCustomDialogSecondStartRequest(startRequest: () -> Unit) {
        Dialog(this).apply {
            val view : DialogRequestPermissionBinding = DialogRequestPermissionBinding.inflate(layoutInflater, null, false)
            view.lottieAnimation.setAnimation(R.raw.dialog_request)
            view.textViewTitle.text = "2. Permission Request"
            view.textViewMessage.text = "This permission is needed to ..."
            view.buttonConfirm.setOnClickListener { this.dismiss() }
            setContentView(view.root)
            setOnDismissListener { startRequest() }
            show()
        }
    }

    private fun yourCustomDialogShowSetting(showSetting: (Boolean) -> Unit) {
        Dialog(this).apply {
            val view = DialogRequestPermissionSettingBinding.inflate(layoutInflater, null, false)
            view.buttonSetting.setOnClickListener {
                showSetting(true)
                this.dismiss()
            }
            view.buttonClose.setOnClickListener {
                showSetting(false)
                this.dismiss()
            }
            setContentView(view.root)
            setOnDismissListener { showSetting(false) }
            show()
        }
    }

    private fun printLog(permissionName: String, tag: String, denied: Array<String>) {
        Log.d(
            "PERMISSION MANAGER",
            "---------------------------" +
            "\n${permissionName.uppercase()}" +
            "\ntag = $tag" +
            "\ndenied = ${denied.toList()}" +
            "\n---------------------------"
        )
    }

    private fun showToast(permissionName: String, result: Boolean) {
        Toast.makeText(this, "$permissionName isGranted = $result", Toast.LENGTH_SHORT).show()
    }
}