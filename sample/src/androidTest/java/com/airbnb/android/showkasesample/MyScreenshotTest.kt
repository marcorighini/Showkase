package com.airbnb.android.showkasesample

import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.annotation.ShowkaseScreenshot
import com.airbnb.android.showkase.screenshot.testing.ShowkaseScreenshotTest
import com.airbnb.android.showkase.screenshot.testing.ShowkaseScreenshotType
import com.airbnb.android.showkasetest.MyTestRootModule

@ShowkaseScreenshot(rootShowkaseClass = MyTestRootModule::class)
abstract class MyScreenshotTest: ShowkaseScreenshotTest {
    override fun onScreenshot(
        id: String,
        name: String,
        group: String,
        styleName: String?,
        screenshotType: ShowkaseScreenshotType,
        composable: @Composable () -> Unit
    ) {
        // TODO(vinaygaba) - Add example of doing on-device screenshot testing.
    }
}