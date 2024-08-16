package space.jay.permissionmanager.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

internal abstract class BaseActivity : AppCompatActivity() {

    protected val arrayDeniedPermission: Array<String> by lazy { intent.getStringArrayExtra(Constant.Extra.ARRAY_PERMISSION) ?: emptyArray() }
    protected val receiverId by lazy { intent.getStringExtra(Constant.Extra.BROADCAST_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    protected fun sendResult(arrayDeniedPermission: Array<String>) {
        val isGranted = arrayDeniedPermission.isEmpty()
        Intent(receiverId)
            .setPackage(applicationContext.packageName)
            .putExtra(Constant.Extra.RESULT_DENIED_ARRAY_PERMISSION, arrayDeniedPermission)
            .putExtra(Constant.Extra.RESULT_IS_GRANTED, isGranted)
            .apply {
                if (!isGranted) {
                    // 처음 거절인지 확인
                    val isDeniedFirstTime = arrayDeniedPermission.any { ActivityCompat.shouldShowRequestPermissionRationale(this@BaseActivity, it) }
                    putExtra(Constant.Extra.RESULT_DENIED_IS_DENIED_FIRST_TIME, isDeniedFirstTime)
                }
            }
            .run { sendBroadcast(this) }
        finish()
    }
}