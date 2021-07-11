@file:Suppress("detekt.FunctionNaming", "detekt.MagicNumber")

package com.majorkik.core.ui.utils

import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb

interface SystemUIController {
    fun setStatusBarColor(
        color: Color,
        darkIcons: Boolean = color.luminance() > 0.5f,
        transformColorForLightContent: (Color) -> Color = BlackScrimmed
    )

    fun setNavigationBarColor(
        color: Color,
        darkIcons: Boolean = color.luminance() > 0.5f,
        transformColorForLightContent: (Color) -> Color = BlackScrimmed
    )

    fun setSystemBarsColor(
        color: Color,
        darkIcons: Boolean = color.luminance() > 0.5f,
        transformColorForLightContent: (Color) -> Color = BlackScrimmed
    )
}

fun SystemUIController(window: Window): SystemUIController {
    return SystemUiControllerImpl(window)
}

/**
 * A helper class for setting the navigation and status bar colors for a [Window], gracefully
 * degrading behavior based upon API level.
 */
private class SystemUiControllerImpl(private val window: Window) : SystemUIController {

    /**
     * Set the status bar color.
     *
     * @param color The **desired** [Color] to set. This may require modification if running on an
     * API level that only supports white status bar icons.
     * @param darkIcons Whether dark status bar icons would be preferable. Only available on
     * API 23+.
     * @param transformColorForLightContent A lambda which will be invoked to transform [color] if
     * dark icons were requested but are not available. Defaults to applying a black scrim.
     */
    override fun setStatusBarColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) {
        val statusBarColor = when {
            darkIcons -> transformColorForLightContent(color)
            else -> color
        }
        window.statusBarColor = statusBarColor.toArgb()

        @Suppress("DEPRECATION")
        if (darkIcons) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    /**
     * Set the navigation bar color.
     *
     * @param color The **desired** [Color] to set. This may require modification if running on an
     * API level that only supports white navigation bar icons. Additionally this will be ignored
     * and [Color.Transparent] will be used on API 29+ where gesture navigation is preferred or the
     * system UI automatically applies background protection in other navigation modes.
     * @param darkIcons Whether dark navigation bar icons would be preferable. Only available on
     * API 26+.
     * @param transformColorForLightContent A lambda which will be invoked to transform [color] if
     * dark icons were requested but are not available. Defaults to applying a black scrim.
     */
    @Suppress("detekt.MagicNumber")
    override fun setNavigationBarColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) {
        val navBarColor = when {
            Build.VERSION.SDK_INT >= 29 -> Color.Transparent // For gesture nav
            darkIcons && Build.VERSION.SDK_INT < 26 -> transformColorForLightContent(color)
            else -> color
        }
        window.navigationBarColor = navBarColor.toArgb()

        if (Build.VERSION.SDK_INT >= 26) {
            @Suppress("DEPRECATION")
            if (darkIcons) {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
        }
    }

    /**
     * Set the status and navigation bars to [color].
     *
     * @see setStatusBarColor
     * @see setNavigationBarColor
     */
    override fun setSystemBarsColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) {
        setStatusBarColor(color, darkIcons, transformColorForLightContent)
        setNavigationBarColor(color, darkIcons, transformColorForLightContent)
    }
}

/**
 * An [androidx.compose.runtime.CompositionLocal] holding the current [LocalSysUiController]. Defaults to a
 * no-op controller; consumers should [provide][androidx.compose.runtime.CompositionLocalProvider] a real one.
 */
val LocalSysUiController = staticCompositionLocalOf<SystemUIController> {
    FakeSystemUiController
}

@Suppress("detekt.MagicNumber")
private val BlackScrim = Color(0f, 0f, 0f, 0.2f) // 20% opaque black

private val BlackScrimmed: (Color) -> Color = { original ->
    BlackScrim.compositeOver(original)
}

/**
 * A fake implementation, useful as a default or used in Previews.
 */
private object FakeSystemUiController : SystemUIController {
    override fun setStatusBarColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) = Unit

    override fun setNavigationBarColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) = Unit

    override fun setSystemBarsColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) = Unit
}
