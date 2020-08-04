package com.example.eventbus

import java.lang.reflect.Method

/**
 * Created by dengchong on 2020/8/4
 */
data class EventMethodMgr(
        val any: Any,
        val methods: List<Method>
)