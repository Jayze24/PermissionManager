package space.jay.permissionmanager.module

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import space.jay.permissionmanager.base.Constant
import space.jay.permissionmanager.ui.ActivityPackageSetting
import space.jay.permissionmanager.ui.ActivityRequestPermission
import java.util.*

internal object UtilPermission {

    fun requestPermission(
        contextApplication: Context,
        arrayDeniedPermission: Array<String>,
        listenerPermission: DataListenerPermission,
    ) {
        // 응답을 받을 리시버 만들기
        val receiverId = setReceiverRequestResultAndGetReceiverId(contextApplication, listenerPermission)
        // 퍼미션 요청하기
        startActivity(contextApplication, ActivityRequestPermission::class.java, arrayDeniedPermission, receiverId)
    }

    fun showSettingActivity(
        contextApplication: Context,
        arrayDeniedPermission: Array<String>,
        listenerPermission: DataListenerPermission,
    ) {
        // 응답을 받을 리시버 만들기
        val receiverId = setReceiverRequestResultAndGetReceiverId(contextApplication, listenerPermission)
        // 세팅화면 보여주기
        startActivity(contextApplication, ActivityPackageSetting::class.java, arrayDeniedPermission, receiverId)
    }

    fun getArrayDeniedPermission(contextApplication: Context, arrayPermission: Array<String>): Array<String> =
        arrayPermission
            .filter { isDeniedPermission(contextApplication, it) }
            .toTypedArray()

    private fun isDeniedPermission(contextApplication: Context, permission: String): Boolean {
        return when {
            isOverlayPermission(permission) -> isDeniedOverlayPermission(contextApplication)
            else -> ContextCompat.checkSelfPermission(contextApplication, permission) != PackageManager.PERMISSION_GRANTED
        }
    }

    fun isOverlayPermission(permission: String) = permission == Manifest.permission.SYSTEM_ALERT_WINDOW

    private fun isDeniedOverlayPermission(contextApplication: Context) = !Settings.canDrawOverlays(contextApplication)

    fun isBackgroundLocation(permission: String) : Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else {
            false
        }
    }

    fun isAvailableRequestBackgroundLocation(contextApplication: Context) : Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(contextApplication, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                   || ContextCompat.checkSelfPermission(contextApplication, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
    }

    private fun setReceiverRequestResultAndGetReceiverId(
        contextApplication: Context,
        listenerPermission: DataListenerPermission,
    ): String {
        val receiverId = UUID.randomUUID().toString()
        ReceiverPermissionResult(contextApplication, receiverId, listenerPermission).register()
        return receiverId
    }

    private fun startActivity(
        contextApplication: Context,
        cls: Class<*>,
        arrayDeniedPermission: Array<String>,
        receiverId: String,
    ) {
        val intentRequestPermission = Intent(contextApplication, cls)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Constant.Extra.ARRAY_PERMISSION, arrayDeniedPermission)
            .putExtra(Constant.Extra.BROADCAST_ID, receiverId)
        contextApplication.startActivity(intentRequestPermission)
    }
}