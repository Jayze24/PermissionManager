package space.jay.permissionmanager.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import space.jay.permissionmanager.base.BaseActivity
import space.jay.permissionmanager.module.UtilPermission

internal class ActivityPackageSetting : BaseActivity() {

    private val startSettingActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), callbackSettingActivity())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startSettingActivity.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")))
    }

    private fun callbackSettingActivity() = ActivityResultCallback<ActivityResult> { _ ->
        sendResult(UtilPermission.getArrayDeniedPermission(applicationContext, arrayDeniedPermission))
    }
}