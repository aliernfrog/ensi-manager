package com.aliernfrog.ensimanager.ui.composable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.aliernfrog.ensimanager.data.ApiRouteOption

@Composable
fun ManagerRouteOption(route: ApiRouteOption, url: String, onValueChange: (String) -> Unit) {
    ManagerTextField(
        value = url,
        onValueChange = onValueChange,
        label = { Text("${route.name}\n${route.description}") },
        placeholder = { Text("METHOD ## URL") },
        singleLine = true,
        rounded = false
    )
}