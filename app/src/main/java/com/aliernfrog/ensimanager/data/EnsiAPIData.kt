package com.aliernfrog.ensimanager.data

data class EnsiAPIData(
    val getStatus: EnsiAPIEndpoint,
    val getLogs: EnsiAPIEndpoint,
    val getChatCategories: EnsiAPIEndpoint,
    val addChatCategory: EnsiAPIEndpoint,
    val deleteChatCategory: EnsiAPIEndpoint,
    val destroyProcess: EnsiAPIEndpoint,
    val postEnsicordAddon: EnsiAPIEndpoint,

    /**
     * If this is not null, app will automatically set the endpoints URL to the URL specified here.
     */
    val migration: EnsiAPIEndpoint? = null
)
