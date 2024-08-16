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
            if (isLastDecision(i)) {
                // 마지막 결정까지 완료된 상태
                sendResult(
                    i.getStringArrayExtra(Constant.Extra.RESULT_DENIED_ARRAY_PERMISSION) ?: emptyArray(),
                    i.getBooleanExtra(Constant.Extra.RESULT_IS_GRANTED, false)
                )
            } else {
                // 추가로 더 물어 봐야 하는 상태
                requestAfterDenied(
                    i.getStringArrayExtra(Constant.Extra.RESULT_DENIED_ARRAY_PERMISSION) ?: emptyArray(),
                    i.getBooleanExtra(Constant.Extra.RESULT_DENIED_IS_DENIED_FIRST_TIME, false)
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

    private fun isLastDecision(intent: Intent) : Boolean {
        // RESULT_DENIED_IS_DENIED_FIRST_TIME 가 있으면 유저의 마지막 결정이 아님.
        return !intent.hasExtra(Constant.Extra.RESULT_DENIED_IS_DENIED_FIRST_TIME)
    }

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
            // 두번째 권한 요청전 해야할 동작이 있으면 실행.
            listenerPermission.beforeSecondRequest!!.invoke(arrayDeniedPermission) {
                // beforeSecondRequest listener 로부터 들어왔고 더이상 사용하지 않기 때문에 null 할당.
                setListenerBeforeSecondRequestNull()
                // 두번째 권한 요청 실행
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