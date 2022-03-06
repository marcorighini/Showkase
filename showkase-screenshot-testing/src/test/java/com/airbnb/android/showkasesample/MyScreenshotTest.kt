package com.airbnb.android.showkasesample

import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.airbnb.android.showkase.annotation.ShowkaseScreenshot
import com.airbnb.android.showkase.screenshot.testing.ShowkaseScreenshotTest
import com.airbnb.android.showkase.screenshot.testing.ShowkaseScreenshotType
import com.airbnb.android.showkasetest.MyTestRootModule
import org.junit.Rule

@ShowkaseScreenshot(rootShowkaseClass = MyTestRootModule::class)
abstract class MyScreenshotTest : ShowkaseScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi()

    override fun onScreenshot(
        id: String,
        name: String,
        group: String,
        styleName: String?,
        screenshotType: ShowkaseScreenshotType,
        composable: () -> Unit
    ) {
        paparazzi.snapshot("file_name_here") { composable() }
    }
}