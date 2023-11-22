package com.airbnb.showkase.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.airbnb.showkase.models.ShowkaseBrowserScreenMetadata
import com.airbnb.showkase.models.ShowkaseElementsMetadata
import com.airbnb.showkase.models.ShowkaseProvider

@Composable
fun ShowkaseBrowser(rootModuleCanonicalName: Nothing) {
    val (
        groupedComponentsList,
        groupedColorsList,
        groupedTypographyList
    ) = getShowkaseProviderElements(rootModuleCanonicalName)

    val showkaseBrowserScreenMetadata = remember { mutableStateOf(ShowkaseBrowserScreenMetadata()) }
    when {
        groupedComponentsList.isNotEmpty() || groupedColorsList.isNotEmpty() ||
                groupedTypographyList.isNotEmpty() -> {
            ShowkaseBrowserApp(
                groupedComponentsList.groupBy { it.group },
                groupedColorsList.groupBy { it.colorGroup },
                groupedTypographyList.groupBy { it.typographyGroup },
                showkaseBrowserScreenMetadata
            )
        }

        else -> {
            ShowkaseErrorScreen(
                errorText = "There were no elements that were annotated with either " +
                        "@ShowkaseComposable, @ShowkaseTypography or @ShowkaseColor. If " +
                        "you think this is a mistake, file an issue at " +
                        "https://github.com/airbnb/Showkase/issues"
            )
        }
    }
}

private fun getShowkaseProviderElements(
    classKey: String
): ShowkaseElementsMetadata {
    return try {
        val showkaseComponentProvider = Class.forName("$classKey${AUTOGEN_CLASS_NAME}").newInstance()

        val showkaseMetadata = (showkaseComponentProvider as ShowkaseProvider).metadata()

        ShowkaseElementsMetadata(
            componentList = showkaseMetadata.componentList,
            colorList = showkaseMetadata.colorList,
            typographyList = showkaseMetadata.typographyList
        )
    } catch (exception: ClassNotFoundException) {
        ShowkaseElementsMetadata()
    }
}

private const val AUTOGEN_CLASS_NAME = "Codegen"
