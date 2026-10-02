package com.kokteyl.rehberi.ui.nav

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kokteyl.rehberi.AppContainer
import com.kokteyl.rehberi.ui.cocktails.CocktailsScreen
import com.kokteyl.rehberi.ui.cocktails.CocktailsViewModel
import com.kokteyl.rehberi.ui.detail.DetailScreen
import com.kokteyl.rehberi.ui.detail.DetailViewModel
import com.kokteyl.rehberi.ui.favorites.FavoritesScreen
import com.kokteyl.rehberi.ui.favorites.FavoritesViewModel
import com.kokteyl.rehberi.ui.home.HomeScreen
import com.kokteyl.rehberi.ui.home.HomeViewModel
import com.kokteyl.rehberi.ui.ingredients.IngredientsScreen
import com.kokteyl.rehberi.ui.ingredients.IngredientsViewModel
import com.kokteyl.rehberi.ui.results.ResultsScreen
import com.kokteyl.rehberi.ui.results.ResultsViewModel
import com.kokteyl.rehberi.ui.settings.SettingsScreen
import com.kokteyl.rehberi.ui.settings.SettingsViewModel
import com.kokteyl.rehberi.ui.vmFactory

object Routes {
    const val HOME = "home"
    const val COCKTAILS = "cocktails?tag={tag}"
    const val INGREDIENTS = "ingredients"
    const val FAVORITES = "favorites"
    const val RESULTS = "results"
    const val SETTINGS = "settings"
    const val DETAIL = "detail/{id}"

    fun cocktails(tag: String = "") = "cocktails?tag=$tag"
    fun detail(id: String) = "detail/$id"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

@Composable
fun AppNav(container: AppContainer) {
    val repo = container.repository
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val destination = backStack?.destination

    val tabs = remember {
        listOf(
            Tab(Routes.HOME, "ANA SAYFA", Icons.Filled.Home),
            Tab(Routes.COCKTAILS, "KOKTEYLLER", Icons.AutoMirrored.Filled.List),
            Tab(Routes.INGREDIENTS, "MALZEMELERİM", Icons.Filled.ShoppingCart),
            Tab(Routes.FAVORITES, "FAVORİLER", Icons.Filled.Favorite)
        )
    }
    val showBar = tabs.any { t -> destination?.hierarchy?.any { it.route == t.route } == true }

    fun goTab(route: String) {
        val target = if (route == Routes.COCKTAILS) Routes.cocktails() else route
        nav.navigate(target) {
            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = destination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick = { goTab(tab.route) },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label, fontSize = 10.sp, maxLines = 1, softWrap = false) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(tween(220)) },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(220)) },
            popExitTransition = { fadeOut(tween(160)) }
        ) {
            composable(Routes.HOME) {
                val vm = viewModel<HomeViewModel>(factory = vmFactory { HomeViewModel(repo) })
                HomeScreen(
                    vm = vm,
                    onOpenCocktail = { nav.navigate(Routes.detail(it)) },
                    onOpenIngredients = { goTab(Routes.INGREDIENTS) },
                    onOpenResults = { nav.navigate(Routes.RESULTS) },
                    onOpenCocktails = { tag -> nav.navigate(Routes.cocktails(tag)) },
                    onOpenSettings = { nav.navigate(Routes.SETTINGS) }
                )
            }

            composable(
                route = Routes.COCKTAILS,
                arguments = listOf(navArgument("tag") {
                    type = NavType.StringType
                    defaultValue = ""
                })
            ) { entry ->
                val tag = entry.arguments?.getString("tag").orEmpty()
                val vm = viewModel<CocktailsViewModel>(
                    key = "cocktails_$tag",
                    factory = vmFactory { CocktailsViewModel(repo, tag) }
                )
                CocktailsScreen(vm = vm, onOpenCocktail = { nav.navigate(Routes.detail(it)) })
            }

            composable(Routes.INGREDIENTS) {
                val vm = viewModel<IngredientsViewModel>(factory = vmFactory { IngredientsViewModel(repo) })
                IngredientsScreen(vm = vm, onFind = { nav.navigate(Routes.RESULTS) })
            }

            composable(Routes.FAVORITES) {
                val vm = viewModel<FavoritesViewModel>(factory = vmFactory { FavoritesViewModel(repo) })
                FavoritesScreen(vm = vm, onOpenCocktail = { nav.navigate(Routes.detail(it)) })
            }

            composable(Routes.RESULTS) {
                val vm = viewModel<ResultsViewModel>(factory = vmFactory { ResultsViewModel(repo) })
                ResultsScreen(
                    vm = vm,
                    onBack = { nav.popBackStack() },
                    onOpenCocktail = { nav.navigate(Routes.detail(it)) },
                    onPickIngredients = { goTab(Routes.INGREDIENTS) }
                )
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                val vm = viewModel<DetailViewModel>(
                    key = "detail_$id",
                    factory = vmFactory { DetailViewModel(repo, id) }
                )
                DetailScreen(vm = vm, onBack = { nav.popBackStack() })
            }

            composable(Routes.SETTINGS) {
                val vm = viewModel<SettingsViewModel>(factory = vmFactory { SettingsViewModel(container) })
                SettingsScreen(vm = vm, onBack = { nav.popBackStack() })
            }
        }
    }
}
