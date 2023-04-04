package com.aliernfrog.ensimanager.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aliernfrog.ensimanager.data.ApiRouteOption

@Composable
fun RouteOption(route: ApiRouteOption, url: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    TextField(
        value = url,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text("${route.name}\n${route.description}") },
        placeholder = { Text("METHOD ## URL") },
        singleLine = true,
        rounded = false
    )
}