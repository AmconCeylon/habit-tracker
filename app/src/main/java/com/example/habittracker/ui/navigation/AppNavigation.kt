package com.example.habittracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habittracker.ui.detail.HabitDetailScreen
import com.example.habittracker.ui.edit.HabitEditScreen
import com.example.habittracker.ui.list.HabitListScreen

object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{habitId}"
    const val EDIT = "edit?habitId={habitId}"

    fun detail(id: Long) = "detail/$id"
    fun edit(id: Long? = null) = if (id == null) "edit" else "edit?habitId=$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            HabitListScreen(
                onAddHabit = { navController.navigate(Routes.edit()) },
                onOpenHabit = { id -> navController.navigate(Routes.detail(id)) },
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType }),
        ) {
            HabitDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Routes.edit(id)) },
            )
        }
        composable(
            route = Routes.EDIT,
            arguments = listOf(
                navArgument("habitId") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
            ),
        ) {
            HabitEditScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                // После удаления возвращаемся к списку (экран деталей уже не имеет смысла).
                onDeleted = { navController.popBackStack(Routes.LIST, inclusive = false) },
            )
        }
    }
}
