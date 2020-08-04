package com.example.eventbus

import java.lang.reflect.Method

/**
 * Created by dengchong on 2020/8/4
 */

object EventBus {
    private val methodMap: MutableMap<Class<*>, EventMethodMgr> = hashMapOf()

    fun register(any: Any) {
        val clazz = any::class.java
        var methodList = methodMap[clazz]
        if (methodList == null) {
            methodList = findMethodsWithAnnotation(any)
            methodMap[clazz] = methodList
        }
    }

    fun unregister(any: Any) {
        methodMap.remove(any.javaClass)
    }

    fun post(event: Any) {
        methodMap.keys.forEach { key ->
            val methodMgr = methodMap[key]
            methodMgr?.methods?.forEach { method ->
                if (event::class.java == method.parameterTypes[0]) {
                    method.invoke(methodMgr.any, event)
                }
            }
        }
    }

    private fun findMethodsWithAnnotation(any: Any): EventMethodMgr {
        val methods: MutableList<Method> = arrayListOf()
        var curClass: Class<*>? = any::class.java
        while (curClass != null) {
            val className = curClass.name
            if (className.startsWith("java.")
                    || className.startsWith("javax.")
                    || className.startsWith("android.")
                    || className.startsWith("androidx.")) {
                break
            }
            curClass.declaredMethods.forEach {
                if (it.isAnnotationPresent(Subscribe::class.java)
                        && it.parameterTypes.size == 1
                        && it.returnType.toString() == "void"
                ) {
                    it.isAccessible = true
                    methods.add(it)
                }
            }
            curClass = curClass.superclass
        }
        return EventMethodMgr(any, methods)
    }
}