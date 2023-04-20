package com.aliernfrog.ensimanager.data

data class EnsiAPIData(
    val getStatus: EnsiAPIEndpoint,
    val destroyProcess: EnsiAPIEndpoint,
    val getWords: EnsiAPIEndpoint,
    val addWord: EnsiAPIEndpoint,
    val deleteWord: EnsiAPIEndpoint,
    val getVerbs: EnsiAPIEndpoint,
    val addVerb: EnsiAPIEndpoint,
    val deleteVerb: EnsiAPIEndpoint,
    val postEnsicordAddon: EnsiAPIEndpoint
)
