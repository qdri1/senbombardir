package com.alimapps.senbombardir.data.repository

import com.alimapps.senbombardir.data.source.Prefs
import com.alimapps.senbombardir.domain.model.AppLanguage

class LanguageRepository(
    private val prefs: Prefs,
) {

    fun getSelectedLanguage(): AppLanguage? {
        val languageCode = prefs.selectedLanguage ?: return null
        return AppLanguage.entries.find { it.code == languageCode }
    }
    
    fun saveLanguage(language: AppLanguage) {
        prefs.selectedLanguage = language.code
    }
}
