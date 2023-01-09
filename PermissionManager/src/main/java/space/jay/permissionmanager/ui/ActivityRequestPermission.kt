package space.jay.permissionmanager.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import space.jay.permissionmanager.base.BaseActivity
import space.jay.permissionmanager.base.Constant
import space.jay.permissionmanager.module.UtilPermission

internal class ActivityRequestPermission : BaseActivity() {

    private sealed class TypePermission {
        @Suppress("ArrayInDataClass")
        data class Normal(val arrayDeniedPermission: Array<String>) : TypePermission()
        object Overlay : TypePermission()
        object BackgroundLocation : TypePermission()
    }

    private val requestNormalPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), callbackNormalPermission())
    private val requestOverlayPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), callbackOverlayPermission())
    private val stackPermission = ArrayDeque<TypePermission>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splitAndAddStackPermission()
        requestNextTypePermission()
    }

    private fun splitAndAddStackPermission() {
        val arrayNormalPermission = arrayDeniedPermission
            .filter { isNormalPermission(it) }
            .toTypedArray()
        stackPermission.add(TypePermission.Normal(arrayNormalPermission))
    }

    private fun isNormalPermission(permission: String): Boolean {
        return when {
            UtilPermission.isOverlayPermission(permission) -> {
                stackPermission.add(TypePermission.Overlay)
                false
            }
            UtilPermission.isBackgroundLocation(permission) -> {
                stackPermission.add(TypePermission.BackgroundLocation)
                false
            }
            else -> true
        }
    }

    @SuppressLint("InlinedApi")
    private fun requestNextTypePermission() {
        if (stackPermission.isEmpty()) {
            sendResult()
        } else {
            when (val type = stackPermission.removeLast()) {
                is TypePermission.Normal -> requestNormalPermission.launch(type.arrayDeniedPermission)
                is TypePermission.Overlay -> requestOverlayPermission.launch(
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", packageName, null))
                )
                is TypePermission.BackgroundLocation -> {
                    if (UtilPermission.isAvailableRequestBackgroundLocation(applicationContext)) {
                        requestNormalPermission.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                    } else {
                        requestNextTypePermission()
                    }
                }
            }
        }
    }

    private fun callbackNormalPermission() = ActivityResultCallback<Map<String, Boolean>> { _ ->
        requestNextTypePermission()
    }

    private fun callbackOverlayPermission() = ActivityResultCallback<ActivityResult> { _ ->
        requestNextTypePermission()
    }

    private fun sendResult() {
        val arrayDeniedPermission = UtilPermission.getArrayDeniedPermission(applicationContext, arrayDeniedPermission)
        if (arrayDeniedPermission.isEmpty()) {
            sendResult(arrayDeniedPermission)
        } else {
            sendDenied(arrayDeniedPermission)
        }
    }

    private fun sendDenied(arrayDeniedPermission: Array<String>) {
        val isDeniedFirstTime = arrayDeniedPermission.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }
        sendBroadcast(
            Intent(receiverId)
                .putExtra(Constant.Extra.RESULT_DENIED_ARRAY_PERMISSION, arrayDeniedPermission)
                .putExtra(Constant.Extra.RESULT_DENIED_IS_DENIED_FIRST_TIME, isDeniedFirstTime)
        )
        finish()
    }
}