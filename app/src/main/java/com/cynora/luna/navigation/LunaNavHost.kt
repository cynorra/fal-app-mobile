package com.cynora.luna.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.cynora.luna.core.ui.modern.LunaBackdrop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.cynora.luna.core.ui.components.BottomNavItem
import com.cynora.luna.core.ui.components.LunaBottomNavigation
import com.cynora.luna.feature.fortune.FortuneScreen
import com.cynora.luna.feature.fortune.affirmations.AffirmationsScreen
import com.cynora.luna.feature.fortune.angelnumbers.AngelNumbersScreen
import com.cynora.luna.feature.fortune.birthchart.BirthChartScreen
import com.cynora.luna.feature.fortune.compatibility.CompatibilityScreen
import com.cynora.luna.feature.fortune.dailycard.DailyCardScreen
import com.cynora.luna.feature.fortune.dreams.DreamInterpreterScreen
import com.cynora.luna.feature.fortune.luckynumbers.LuckyNumbersScreen
import com.cynora.luna.feature.fortune.oracle.YesNoOracleScreen
import com.cynora.luna.feature.fortune.tarot.TarotReadingScreen
import com.cynora.luna.feature.fortune.zodiac.ZodiacGuideScreen
import com.cynora.luna.feature.home.HomeScreen
import com.cynora.luna.feature.onboarding.OnboardingScreen
import com.cynora.luna.feature.profile.ProfileScreen
import com.cynora.luna.feature.social.SocialScreen

object Routes {
    const val HOME = "home"
    const val FORTUNE = "fortune"
    const val SOCIAL = "social"
    const val PROFILE = "profile"
    const val BIRTH_CHART = "birth_chart"
    const val DAILY_CARD = "daily_card"
    const val COMPATIBILITY = "compatibility"
    const val ANGEL_NUMBERS = "angel_numbers"
    const val LUCKY_NUMBERS = "lucky_numbers"
    const val AFFIRMATIONS = "affirmations"
    const val YES_NO_ORACLE = "yes_no_oracle"
    const val DREAM_INTERPRETER = "dream_interpreter"
    const val TAROT_READING = "tarot_reading"
    const val ZODIAC_GUIDE = "zodiac_guide"
}

@Composable
fun LunaNavHost(
    deepLinkRoute: String? = null,
    onDeepLinkHandled: () -> Unit = {},
    appVM: AppViewModel = hiltViewModel()
) {
    val nav = rememberNavController()
    val appState by appVM.state.collectAsStateWithLifecycle()

    val items = listOf(
        BottomNavItem(Routes.HOME, Icons.Default.Home, "Home"),
        BottomNavItem(Routes.FORTUNE, Icons.Default.Star, "Fortune"),
        BottomNavItem(Routes.SOCIAL, Icons.Default.Favorite, "Community"),
        BottomNavItem(Routes.PROFILE, Icons.Default.Person, "Profile")
    )

    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottom = currentRoute in listOf(
        Routes.HOME, Routes.FORTUNE, Routes.SOCIAL, Routes.PROFILE
    )

    LunaBackdrop {
    if (appState.isOnboardingComplete) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottom) {
                    LunaBottomNavigation(items, currentRoute, showBanner = true, onItemSelected = { item ->
                        nav.navigate(item.route) {
                            popUpTo(nav.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
                }
            }
        ) { pad ->
            Box(
                modifier = Modifier.fillMaxSize().padding(pad),
                contentAlignment = Alignment.TopCenter
            ) {
            LaunchedEffect(deepLinkRoute) {
                if (deepLinkRoute != null) {
                    nav.navigate(deepLinkRoute) { launchSingleTop = true }
                    onDeepLinkHandled()
                }
            }
            NavHost(nav, Routes.HOME, Modifier.widthIn(max = 840.dp).fillMaxSize()) {
                composable(Routes.HOME) {
                    HomeScreen(
                        onNavigateToFortune = { nav.navigate(Routes.FORTUNE) },
                        onNavigateToBirthChart = { nav.navigate(Routes.BIRTH_CHART) },
                        onNavigateToDailyCard = { nav.navigate(Routes.DAILY_CARD) },
                        onNavigateToCompatibility = { nav.navigate(Routes.COMPATIBILITY) },
                        onNavigateToAngelNumbers = { nav.navigate(Routes.ANGEL_NUMBERS) },
                        onNavigateToLuckyNumbers = { nav.navigate(Routes.LUCKY_NUMBERS) },
                        onNavigateToAffirmations = { nav.navigate(Routes.AFFIRMATIONS) },
                        onNavigateToYesNoOracle = { nav.navigate(Routes.YES_NO_ORACLE) },
                        onNavigateToDreamInterpreter = { nav.navigate(Routes.DREAM_INTERPRETER) },
                        onNavigateToTarotReading = { nav.navigate(Routes.TAROT_READING) },
                        onNavigateToZodiacGuide = { nav.navigate(Routes.ZODIAC_GUIDE) }
                    )
                }
                composable(Routes.FORTUNE) { FortuneScreen() }
                composable(Routes.SOCIAL) { SocialScreen() }
                composable(Routes.PROFILE) { ProfileScreen() }
                composable(Routes.BIRTH_CHART) {
                    BirthChartScreen(onBack = { nav.popBackStack() })
                }
                composable(Routes.DAILY_CARD) {
                    DailyCardScreen(onBack = { nav.popBackStack() })
                }
                composable(Routes.COMPATIBILITY) {
                    CompatibilityScreen(onBack = { nav.popBackStack() })
                }
                composable(Routes.ANGEL_NUMBERS) {
                    AngelNumbersScreen(onBack = { nav.popBackStack() })
                }
                composable(Routes.LUCKY_NUMBERS) {
                    LuckyNumbersScreen(onBack = { nav.popBackStack() })
                }
                composable(Routes.AFFIRMATIONS) {
                    AffirmationsScreen(onBack = { nav.popBackStack() })
                }
                composable(Routes.YES_NO_ORACLE) {
                    YesNoOracleScreen(onBack = { nav.popBackStack() })
                }
                composable(Routes.DREAM_INTERPRETER) {
                    DreamInterpreterScreen(onBack = { nav.popBackStack() })
                }
                composable(Routes.TAROT_READING) {
                    TarotReadingScreen(onBack = { nav.popBackStack() })
                }
                composable(Routes.ZODIAC_GUIDE) {
                    ZodiacGuideScreen(onBack = { nav.popBackStack() })
                }
                composable(
                    route = "${Routes.FORTUNE}/{fortuneId}",
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "https://luna.cynora.com/fortune/{fortuneId}" }
                    ),
                    arguments = listOf(navArgument("fortuneId") { defaultValue = "" })
                ) { backStackEntry ->
                    val fortuneId = backStackEntry.arguments?.getString("fortuneId")
                    FortuneScreen(initialFortuneId = fortuneId)
                }
            }
            }
        }
    } else {
        OnboardingScreen(onComplete = {
            appVM.onIntent(AppIntent.CompleteOnboarding)
        })
    }
    }
}
