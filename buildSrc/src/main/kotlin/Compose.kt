object Compose {
    const val composeVersion = "1.6.0"
//    const val runtime = "androidx.compose.runtime:runtime:${composeVersion}"
    const val runtimeLiveData = "androidx.compose.runtime:runtime-livedata:${composeVersion}"
    const val ui = "androidx.compose.ui:ui:${composeVersion}"
    const val material = "androidx.compose.material:material:${composeVersion}"
    const val uiTooling = "androidx.compose.ui:ui-tooling:${composeVersion}"
    const val foundation = "androidx.compose.foundation:foundation:${composeVersion}"
    const val compiler = "androidx.compose.compiler:compiler:${composeVersion}"
    const val icons = "androidx.compose.material:material-icons-extended:$composeVersion"
    const val composeBom = "androidx.compose:compose-bom:2023.10.01"
    private const val constraintLayoutComposeVersion = "2.1.4"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:${constraintLayoutComposeVersion}"

    private const val composeActivitiesVersion = "1.8.0"
    const val activity = "androidx.activity:activity-compose:${composeActivitiesVersion}"

    private const val composeNavigationVerson = "2.7.4"
    const val navigation = "androidx.navigation:navigation-compose:${composeNavigationVerson}"
}