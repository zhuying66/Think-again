package com.zhuying.zaikaolv.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zhuying.zaikaolv.AppContainer
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.ui.screens.classify.ClassifyScreen
import com.zhuying.zaikaolv.ui.screens.home.HomeScreen
import com.zhuying.zaikaolv.ui.screens.quiz.QuizScreen
import com.zhuying.zaikaolv.ui.screens.record.RecordScreen
import com.zhuying.zaikaolv.ui.screens.result.ResultScreen
import com.zhuying.zaikaolv.ui.screens.settings.SettingsScreen

private const val ROUTE_HOME = "home"
private const val ROUTE_RECORD = "record"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_CLASSIFY = "classify"
private const val ROUTE_QUIZ = "quiz/{mode}"
private const val ROUTE_RESULT = "result/{mode}/{answers}"

private fun quizRoute(mode: QuestionMode) = "quiz/${mode.key}"
private fun resultRoute(mode: QuestionMode, answersCsv: String) =
    "result/${mode.key}/$answersCsv"

@Composable
fun ZaikaolvRoot(container: AppContainer) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    val showBottomBar = route == ROUTE_HOME || route == ROUTE_RECORD || route == ROUTE_SETTINGS

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    tonalElevation = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    val items = listOf(
                        Triple(ROUTE_HOME, "首页", Icons.Filled.Home),
                        Triple(ROUTE_RECORD, "记录", Icons.Outlined.History),
                        Triple(ROUTE_SETTINGS, "设置", Icons.Filled.Settings),
                    )
                    items.forEach { (r, label, icon) ->
                        val selected = route == r
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) navController.navigate(r) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(ROUTE_HOME) {
                HomeScreen(
                    onStart = { mode -> navController.navigate(quizRoute(mode)) },
                    onStartClassify = { navController.navigate(ROUTE_CLASSIFY) },
                )
            }
            // 分类前置步骤:答完 5 道规模判断题后自动进入对应问卷
            composable(ROUTE_CLASSIFY) {
                ClassifyScreen(
                    onExit = { navController.popBackStack() },
                    onClassified = { mode ->
                        navController.navigate(quizRoute(mode)) {
                            // 分类已完成,返回键直接回首页而不是回到分类问答
                            popUpTo(ROUTE_HOME)
                        }
                    },
                )
            }
            composable(
                route = ROUTE_QUIZ,
                arguments = listOf(navArgument("mode") { type = NavType.StringType }),
            ) { entry ->
                val mode = QuestionMode.fromKey(entry.arguments?.getString("mode") ?: "small")
                QuizScreen(
                    mode = mode,
                    onBack = { navController.popBackStack() },
                    onFinished = { m, answers ->
                        val csv = answers.joinToString(",")
                        navController.navigate(resultRoute(m, csv)) {
                            popUpTo(ROUTE_HOME)
                        }
                    },
                )
            }
            composable(
                route = ROUTE_RESULT,
                arguments = listOf(
                    navArgument("mode") { type = NavType.StringType },
                    navArgument("answers") { type = NavType.StringType },
                ),
            ) { entry ->
                val mode = QuestionMode.fromKey(entry.arguments?.getString("mode") ?: "small")
                val answers = entry.arguments?.getString("answers") ?: ""
                ResultScreen(
                    container = container,
                    mode = mode,
                    answersCsv = answers,
                    onRestart = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(ROUTE_RECORD) { RecordScreen(container = container) }
            composable(ROUTE_SETTINGS) { SettingsScreen(container = container) }
        }
    }
}
