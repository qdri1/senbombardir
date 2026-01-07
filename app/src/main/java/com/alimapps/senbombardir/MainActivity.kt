package com.alimapps.senbombardir

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alimapps.senbombardir.data.repository.LanguageRepository
import com.alimapps.senbombardir.ui.navigation.AppNavigation
import com.alimapps.senbombardir.ui.screen.language.AppLanguage
import com.alimapps.senbombardir.ui.theme.AppTheme
import org.koin.android.ext.android.inject
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val languageRepository: LanguageRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val selectedLanguage = languageRepository.getSelectedLanguage()
        selectedLanguage?.let { setLocale(this, it) }

        setContent {
            AppTheme {
                AppNavigation(
                    showLanguage = selectedLanguage == null,
                    onLanguageSelected = { appLanguage ->
                        languageRepository.saveLanguage(appLanguage)
                        setLocale(this, appLanguage)
                    }
                )
            }
        }
    }

    private fun setLocale(context: Context, language: AppLanguage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            localeManager.applicationLocales = LocaleList.forLanguageTags(language.code)
        } else {
            setLocaleLegacy(context, language)
        }
    }

    @Suppress("DEPRECATION")
    private fun setLocaleLegacy(context: Context, language: AppLanguage) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }
}