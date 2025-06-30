package com.alimapps.senbombardir.ui.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

const val NAVIGATION_REQUEST_RESULT_KEY = "navigation_request_result_key"

interface NavigationResultManager {

    fun observeResult(key: String = NAVIGATION_REQUEST_RESULT_KEY): Flow<Any>

    fun sendResult(result: Any, key: String = NAVIGATION_REQUEST_RESULT_KEY)
}

class DefaultNavigationResultManager : NavigationResultManager {

    private val results = ConcurrentHashMap<String, Any>()
    private val resultFlow = MutableSharedFlow<MutableMap<String, Any>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override fun observeResult(key: String): Flow<Any> =
        resultFlow.map { it.remove(key) }.filterNotNull()

    override fun sendResult(result: Any, key: String) {
        results[key] = result
        resultFlow.tryEmit(results)
    }
}