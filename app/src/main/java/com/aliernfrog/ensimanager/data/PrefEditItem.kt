package com.aliernfrog.ensimanager.data

import com.aliernfrog.ensimanager.util.manager.PreferenceManager

data class PrefEditItem(
    val labelResourceId: Int,
    val getValue: (prefs: PreferenceManager) -> String,
    val setValue: (newValue: String, prefs: PreferenceManager) -> Unit,
    val default: String = ""
)
