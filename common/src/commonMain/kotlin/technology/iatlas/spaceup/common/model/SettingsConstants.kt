package technology.iatlas.spaceup.common.model

enum class SettingsConstants(
    private var naming: String,
    var title: String,
    var type: String
) {
    REMEMBER_SERVER("rememberServer", "Remember Server", "boolean"),
    SERVER_URL("serverUrl", "Server url", "string"),
    REMEMBER_CREDENTIALS("rememberCredentials", "Remember Credentials", "boolean"),
    USERNAME("username", "Username", "string"),
    PASSWORD("password", "Password", "string"),
    AUTO_LOGIN("autoLogin", "Auto Login", "boolean");

    override fun toString(): String {
        return naming
    }
}