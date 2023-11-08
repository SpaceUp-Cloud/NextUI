package technology.iatlas.spaceup.common.model

enum class Routes(
    val title: String,
    val path: String,
    val drawerBehavior: DrawerBehavior = DrawerBehavior(),
    ) {
    HOME("Home", "/home", DrawerBehavior(isVisible = false)),
    DOMAINS("Domains", "/domains"),
    SERVICES("Services", "/services"),
    WEBBACKENDS("Web backends", "/webbackends"),
    SWS("Server Web Scripts", "/sws"),
    SETTINGS("Settings", "/settings"),
    ABOUT("About", "/about", DrawerBehavior(hasAfterDivider = true)),
    LOGOUT("Logout", "/logout"),
    LOGIN("Login", "/login", DrawerBehavior(isVisible = false)),
}

data class DrawerBehavior(
    val hasAfterDivider: Boolean = false,
    val isVisible: Boolean = true
)