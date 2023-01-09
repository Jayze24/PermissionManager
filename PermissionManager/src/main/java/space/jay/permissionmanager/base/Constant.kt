package space.jay.permissionmanager.base

internal object Constant {
    interface Extra {
        companion object {
            const val ARRAY_PERMISSION = "ARRAY_PERMISSION"
            const val BROADCAST_ID = "BROADCAST_ID"

            const val RESULT_DENIED_ARRAY_PERMISSION = "RESULT_DENIED_ARRAY_PERMISSION"
            const val RESULT_DENIED_IS_DENIED_FIRST_TIME = "RESULT_DENIED_IS_DENIED_FIRST_TIME"
            const val RESULT_IS_GRANTED = "RESULT_IS_GRANTED"
        }
    }
}