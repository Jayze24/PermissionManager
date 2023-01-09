package space.jay.permissionmanager.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

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
        sendBroadcast(
            Intent(receiverId)
                .putExtra(Constant.Extra.RESULT_IS_GRANTED, arrayDeniedPermission.isEmpty())
                .putExtra(Constant.Extra.RESULT_DENIED_ARRAY_PERMISSION, arrayDeniedPermission)
        )
        finish()
    }
}