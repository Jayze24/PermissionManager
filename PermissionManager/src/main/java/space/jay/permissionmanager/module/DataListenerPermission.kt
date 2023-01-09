package space.jay.permissionmanager.module

internal data class DataListenerPermission(
    var beforeRequest: ((arrayDeniedPermission: Array<String>, startRequest: () -> Unit) -> Unit)? = null,
    var beforeSecondRequest: ((arrayDeniedPermission: Array<String>, startRequest: () -> Unit) -> Unit)? = null,
    var afterFinalDenied: ((arrayDeniedPermission: Array<String>, showSetting: (Boolean) -> Unit) -> Unit)? = null,
    var result: ((arrayDeniedPermission: Array<String>, isGranted: Boolean) -> Unit)? = null,
)