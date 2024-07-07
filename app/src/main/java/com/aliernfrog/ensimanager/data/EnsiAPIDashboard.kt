package com.aliernfrog.ensimanager.data

data class EnsiAPIDashboard(
    val name: String,
    val avatar: String,
    val status: String,
    val info: List<EnsiAPIDashboardInfo>,
    val actions: List<EnsiAPIDashboardAction>
)

data class EnsiAPIDashboardInfo(
    val title: String,
    val value: String?
)

data class EnsiAPIDashboardAction(
    val label: String,
    val description: String?,
    val icon: String?,
    val destructive: Boolean = false,
    val endpoint: EnsiAPIEndpoint
)