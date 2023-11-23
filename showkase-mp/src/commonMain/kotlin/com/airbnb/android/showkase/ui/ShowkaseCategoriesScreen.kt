//package com.airbnb.showkase.ui
//
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.ui.platform.LocalContext
//import androidx.navigation.NavHostController
//import com.airbnb.showkase.models.ShowkaseCategory
//import com.airbnb.showkase.models.clear
//import com.airbnb.showkase.models.clearActiveSearch
//import com.airbnb.showkase.models.update
//import java.util.Locale
//
//@Composable
//internal fun ShowkaseCategoriesScreen(
//    showkaseBrowserScreenMetadata: MutableState<com.airbnb.showkase.models.ShowkaseBrowserScreenMetadata>,
//    navController: NavHostController,
//    categoryMetadataMap: Map<ShowkaseCategory, Int>
//) {
//    val activity = LocalContext.current as AppCompatActivity
//    LazyColumn {
//        items(
//            items = categoryMetadataMap.entries.toList(),
//            itemContent = { (category, categorySize) ->
//                val defaultLocale = Locale.getDefault()
//                val title = category.name
//                    .lowercase(defaultLocale)
//                    .replaceFirstChar { it.titlecase(defaultLocale) }
//
//                SimpleTextCard(
//                    text = "$title ($categorySize)",
//                    onClick = {
//                        showkaseBrowserScreenMetadata.update {
//                            copy(
//                                currentGroup = null,
//                                isSearchActive = false,
//                                searchQuery = null
//                            )
//                        }
//                        when (category) {
//                            ShowkaseCategory.COMPONENTS -> navController.navigate(
//                                com.airbnb.showkase.models.ShowkaseCurrentScreen.COMPONENT_GROUPS
//                            )
//                            ShowkaseCategory.COLORS -> navController.navigate(
//                                com.airbnb.showkase.models.ShowkaseCurrentScreen.COLOR_GROUPS
//                            )
//                            ShowkaseCategory.TYPOGRAPHY -> navController.navigate(
//                                com.airbnb.showkase.models.ShowkaseCurrentScreen.TYPOGRAPHY_GROUPS
//                            )
//                        }
//                    }
//                )
//            }
//        )
//    }
//    BackButtonHandler {
//        goBackFromCategoriesScreen(activity, showkaseBrowserScreenMetadata)
//    }
//}
//
//private fun goBackFromCategoriesScreen(
//    activity: AppCompatActivity,
//    showkaseBrowserScreenMetadata: MutableState<com.airbnb.showkase.models.ShowkaseBrowserScreenMetadata>
//) {
//    val isSearchActive = showkaseBrowserScreenMetadata.value.isSearchActive
//    when {
//        isSearchActive -> showkaseBrowserScreenMetadata.clearActiveSearch()
//        else -> activity.finish()
//    }
//}
//
//internal fun goBackToCategoriesScreen(
//    showkaseBrowserScreenMetadata: MutableState<com.airbnb.showkase.models.ShowkaseBrowserScreenMetadata>,
//    navController: NavHostController
//) {
//
//    when {
//        showkaseBrowserScreenMetadata.value.isSearchActive -> {
//            showkaseBrowserScreenMetadata.clearActiveSearch()
//        }
//        else -> {
//            showkaseBrowserScreenMetadata.clear()
//            navController.navigate(com.airbnb.showkase.models.ShowkaseCurrentScreen.SHOWKASE_CATEGORIES)
//        }
//    }
//}
