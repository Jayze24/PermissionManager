package space.jay.permissionmanager

import android.content.Context
import android.os.Build
import space.jay.permissionmanager.module.DataListenerPermission
import space.jay.permissionmanager.module.UtilPermission

class PermissionManager private constructor(
    private val contextApplication: Context,
    private val arrayPermission: Array<String>,
    private val listenerPermission: DataListenerPermission,
) {

    class Builder(private var contextApplication: Context) {

        private val permissions: MutableSet<String> = mutableSetOf()
        private val listenerPermission: DataListenerPermission = DataListenerPermission()

        fun setPermission(permission: String) = apply { permissions.add(permission) }

        fun setPermissions(vararg permission: String) = apply { permissions.addAll(permission) }

        fun setPermissionWithTarget(permission: String, minSdk: Int = 0, maxSdk: Int = Int.MAX_VALUE) = apply {
            if (Build.VERSION.SDK_INT in minSdk .. maxSdk) {
                permissions.add(permission)
            }
        }

        fun setListenerShowDialogBeforeRequest(listener: (arrayDeniedPermission: Array<String>, startRequest: () -> Unit) -> Unit) = apply { listenerPermission.beforeRequest = listener }

        fun setListenerShowDialogBeforeSecondRequest(listener: (arrayDeniedPermission: Array<String>, startRequest: () -> Unit) -> Unit) = apply { listenerPermission.beforeSecondRequest = listener }

        fun setListenerShowDialogAfterFinalDenied(listener: (arrayDeniedPermission: Array<String>, showSetting: (Boolean) -> Unit) -> Unit) = apply { listenerPermission.afterFinalDenied = listener }

        fun setListenerResult(listener: (arrayDeniedPermission: Array<String>, isGranted: Boolean) -> Unit) = apply { listenerPermission.result = listener }

        fun check() = PermissionManager(
            contextApplication = contextApplication,
            arrayPermission = permissions.toTypedArray(),
            listenerPermission = listenerPermission
        )
    }

    init {
        // ?????? ?????? ?????? ????????? ??????
        val arrayDeniedPermission = UtilPermission.getArrayDeniedPermission(contextApplication, arrayPermission)
        if (arrayDeniedPermission.isNotEmpty()) {
            // ???????????? ????????? ?????? ??????
            val request = { UtilPermission.requestPermission(contextApplication, arrayDeniedPermission, listenerPermission) }
            listenerPermission.beforeRequest?.invoke(arrayDeniedPermission, request) ?: request()
        } else {
            // ?????? ????????? ?????? ???????????? ??????
            listenerPermission.result?.invoke(emptyArray(), true)
        }
    }

}