package com.alimapps.senbombardir.ui.utils

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce

interface DebounceEffect {
    val debounce: Long get() = 300
}

@OptIn(FlowPreview::class)
fun <T> Flow<T>.debounceEffect(): Flow<T> = debounce {
    if (it is DebounceEffect) it.debounce else 0
}