package com.aliernfrog.ensimanager.data

data class EnsiAPIData(
    val getStatus: EnsiAPIEndpoint,
    val getLogs: EnsiAPIEndpoint,
    val destroyProcess: EnsiAPIEndpoint,
    val getWords: EnsiAPIEndpoint,
    val addWord: EnsiAPIEndpoint,
    val deleteWord: EnsiAPIEndpoint,
    val getVerbs: EnsiAPIEndpoint,
    val addVerb: EnsiAPIEndpoint,
    val deleteVerb: EnsiAPIEndpoint,
    val postEnsicordAddon: EnsiAPIEndpoint,

    /**
     * If this is not null, app will automatically set the endpoints URL to the URL specified here.
     */
    val migration: EnsiAPIEndpoint? = null
)
