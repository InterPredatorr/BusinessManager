object Build {
    private const val gradleBuildTools = "8.1.2"
    const val buildTools = "com.android.tools.build:gradle:${gradleBuildTools}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
    const val sqlDelightGradlePlugin = "app.cash.sqldelight:gradle-plugin:${SQLDelight.sqlDelightVersion}"
}