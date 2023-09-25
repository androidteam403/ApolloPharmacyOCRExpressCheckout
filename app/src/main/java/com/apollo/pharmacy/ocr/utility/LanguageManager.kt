package com.apollo.pharmacy.ocr.utility

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import java.util.*

class LanguageManager {

    companion object {
        @JvmField
        public val LANGUAGE_KEY = "language_key"
        val LANGUAGE_ENGLISH = "en"

        fun setLocale(c: Context): Context {
            val s = getLanguage(c)
            val locale = Locale(s)
            Locale.setDefault(locale)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                updateResourcesLocale(c, locale)
            } else updateResourcesLocaleLegacy(c, locale)
        }

        fun setNewLocale(c: Context, language: String): Context {
            persistLanguage(c, language)
            val locale = Locale(language)
            Locale.setDefault(locale)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                updateResourcesLocale(c, locale)
            } else updateResourcesLocaleLegacy(c, locale)
        }

        fun getLanguage(c: Context): String {
            val prefs = PreferenceManager.getDefaultSharedPreferences(c)
            return prefs.getString(LANGUAGE_KEY, LANGUAGE_ENGLISH)!!
        }

        @SuppressLint("ApplySharedPref")
        private fun persistLanguage(c: Context, language: String) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(c)
            prefs.edit().putString(LANGUAGE_KEY, language).commit()
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun updateResourcesLocale(context: Context, locale: Locale): Context {
            val configuration = context.getResources().getConfiguration()
            configuration.setLocale(locale)
            return context.createConfigurationContext(configuration)
        }

        fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
            val resources = context.getResources()
            val configuration = resources.getConfiguration()
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.getDisplayMetrics())
            return context
        }
    }
}