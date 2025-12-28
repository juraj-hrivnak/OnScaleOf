package teksturepako.onscaleof

enum class Platform {
    DESKTOP,
    ANDROID,
    IOS
}

expect fun platform(): Platform