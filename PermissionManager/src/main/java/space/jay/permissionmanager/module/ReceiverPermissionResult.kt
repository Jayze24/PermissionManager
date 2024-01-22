package space.jay.permissionmanager.module

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import space.jay.permissionmanager.base.Constant

internal class ReceiverPermissionResult(
    private val contextApplication: Context,
    private val id: String,
    private val listenerPermission: DataListenerPermission,
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.also { i ->
            if (i.action == id) unregister() else return
            when {
                isFromActivityRequestPermission(i) ->
                    requestAfterDenied(
                        i.getStringArrayExtra(Constant.Extra.RESULT_DENIED_ARRAY_PERMISSION) ?: emptyArray(),
                        i.getBooleanExtra(Constant.Extra.RESULT_DENIED_IS_DENIED_FIRST_TIME, false)
                    )
                isFromActivityPackageSetting(i) ->
                    sendResult(
                        i.getStringArrayExtra(Constant.Extra.RESULT_DENIED_ARRAY_PERMISSION) ?: emptyArray(),
                        i.getBooleanExtra(Constant.Extra.RESULT_IS_GRANTED, false)
                    )
            }
        }
    }

    fun register(): ReceiverPermissionResult {
        ContextCompat.registerReceiver(
            contextApplication,
            this,
            IntentFilter(id),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        return this
    }

    private fun unregister() {
        contextApplication.unregisterReceiver(this)
    }

    private fun isFromActivityRequestPermission(intent: Intent) =
        intent.hasExtra(Constant.Extra.RESULT_DENIED_IS_DENIED_FIRST_TIME)
        && intent.hasExtra(Constant.Extra.RESULT_DENIED_ARRAY_PERMISSION)

    private fun isFromActivityPackageSetting(intent: Intent) =
        intent.hasExtra(Constant.Extra.RESULT_IS_GRANTED)
        && intent.hasExtra(Constant.Extra.RESULT_DENIED_ARRAY_PERMISSION)

    private fun sendResult(arrayDeniedPermission: Array<String>, isGranted: Boolean) {
        listenerPermission.result?.invoke(arrayDeniedPermission, isGranted)
    }

    private fun requestPermission(arrayDeniedPermission: Array<String>) {
        UtilPermission.requestPermission(contextApplication, arrayDeniedPermission, listenerPermission)
    }

    private fun showSetting(arrayDeniedPermission: Array<String>) {
        UtilPermission.showSettingActivity(contextApplication, arrayDeniedPermission, listenerPermission)
    }

    private fun requestAfterDenied(arrayDeniedPermission: Array<String>, isDeniedFirst: Boolean) {
        if (isShowDialogBeforeSecondRequest(isDeniedFirst)) {
            listenerPermission.beforeSecondRequest!!.invoke(arrayDeniedPermission) {
                // for call only one time. set beforeSecondRequest listener null.
                setListenerBeforeSecondRequestNull()
                requestPermission(arrayDeniedPermission)
            }
        } else {
            requestAfterFinalDenied(arrayDeniedPermission)
        }
    }

    private fun requestAfterFinalDenied(arrayDeniedPermission: Array<String>) {
        if (isShowDialogAfterFinalDenied()) {
            var isShowSettingInvoked = false
            listenerPermission.afterFinalDenied!!.invoke(arrayDeniedPermission) { isShowSetting ->
                // 처음 들어온 것만 실행 그 뒤에 들어오는 것은 무시
                if (isShowSettingInvoked) return@invoke
                isShowSettingInvoked = true
                if (isShowSetting) {
                    showSetting(arrayDeniedPermission)
                } else {
                    sendResult(arrayDeniedPermission, false)
                }
            }
        } else {
            sendResult(arrayDeniedPermission, false)
        }
    }

    private fun isShowDialogBeforeSecondRequest(isDeniedFirst: Boolean) = isDeniedFirst && listenerPermission.beforeSecondRequest != null

    private fun isShowDialogAfterFinalDenied() = listenerPermission.afterFinalDenied != null

    private fun setListenerBeforeSecondRequestNull() {
        listenerPermission.beforeSecondRequest = null
    }
}