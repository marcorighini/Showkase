package com.airbnb.android.showkasesample

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun SampleShowkase() {
    Column {
        RootModuleCodegen().getShowkaseComponents().map { it.component() }
    }
}