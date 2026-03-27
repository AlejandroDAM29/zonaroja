package alejandro.developer.data.session

internal const val GUEST_USER_SCOPE = "__guest__"

internal fun String?.toUserScopeKey(): String {
    return this ?: GUEST_USER_SCOPE
}
