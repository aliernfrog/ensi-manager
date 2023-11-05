package com.aliernfrog.ensimanager.enum

import com.aliernfrog.ensimanager.R

enum class ChatFilterType(
    val type: String,
    val titleId: Int,
    val countTextId: Int,
    val addWordTitleId: Int,
    val addWordPlaceholderId: Int
) {
    WORDS(
        type = "word",
        titleId = R.string.chat_words,
        countTextId = R.string.chat_words_count,
        addWordTitleId = R.string.chat_words_add,
        addWordPlaceholderId = R.string.chat_words_add_placeholder
    ),

    VERBS(
        type = "verb",
        titleId = R.string.chat_verbs,
        countTextId = R.string.chat_words_count,
        addWordTitleId = R.string.chat_verbs_add,
        addWordPlaceholderId = R.string.chat_verbs_add_placeholder
    )
}