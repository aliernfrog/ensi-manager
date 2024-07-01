package com.aliernfrog.ensimanager.data

data class EnsiAPIDashboard(
    val name: String,
    val avatar: String,
    val status: String,
    val actions: List<EnsiAPIDashboardAction>
)

data class EnsiAPIDashboardAction(
    val label: String,
    val description: String?,
    val icon: String?,
    val destructive: Boolean = false,
    val endpoint: EnsiAPIEndpoint
)