package technology.iatlas.spaceup.common.model

enum class Routes(
    val title: String,
    val path: String,
    val drawerBehavior: DrawerBehavior = DrawerBehavior(),
    ) {
    HOME("Home", "/home"),
    DOMAINS("Domains", "/domains"),
    SERVICES("Services", "/services"),
    WEBBACKENDS("Web backends", "/webbackends"),
    SWS("Server Web Scripts", "/sws"),
    SETTINGS("Settings", "/settings"),
    ABOUT("About", "/about", DrawerBehavior(hasAfterDivider = true)),
    LOGOUT("Logout", "/logout"),
    LOGIN("Login", "/login", DrawerBehavior(isVisible = false)),

    // TODO DELETE me if test is successful
    // For testing Drawer behavior
    fake1("Logout", "/a"),
    fake2("Logout", "/b"),
    fake3("Logout", "/c"),
    fake4("Logout", "/d"),
    fake5("Logout", "/e"),
    fake6("Logout", "/f"),
    fake7("Logout", "/g"),
}

data class DrawerBehavior(
    val hasAfterDivider: Boolean = false,
    val isVisible: Boolean = true
)