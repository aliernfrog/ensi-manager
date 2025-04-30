package com.aliernfrog.ensimanager.data.api

data class APIDashboard(
    val name: String,
    val avatar: String,
    val status: String,
    val info: List<APIDashboardInfo>,
    val actions: List<APIDashboardAction>
)

data class APIDashboardInfo(
    val title: String,
    val value: String?
)

data class APIDashboardAction(
    val label: String,
    val description: String?,
    val icon: String?,
    /**
     * Hex color code (#RRGGBBAA)
     */
    val iconContainerColor: String?,
    val destructive: Boolean = false,
    val endpoint: APIEndpoint?
)