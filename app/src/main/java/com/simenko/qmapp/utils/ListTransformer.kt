package com.simenko.qmapp.utils

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class ListTransformer<in T : Any, out R : Any> constructor(
    inClass: KClass<T>, outClass: KClass<R>
) {
    private var inputList: List<T> = listOf()
    constructor(inList: List<T>, inClass: KClass<T>, outClass: KClass<R>) : this(inClass, outClass) {
        inputList = inList
    }

    private val outConstructor = outClass.primaryConstructor!!
    private val inPropertiesByName by lazy {
        inClass.memberProperties.associateBy { it.name }
    }

    fun transform(data: T): R = with(outConstructor) {
        callBy(parameters.associate { parameter ->
            parameter to argFor(parameter, data)
        })
    }

    fun argFor(parameter: KParameter, data: T): Any? {
        return inPropertiesByName[parameter.name]?.get(data)
    }

    fun generateList(): List<R> {
        return inputList.map() {
            transform(it)
        }
    }
}

open class ObjectTransformer<in T : Any, out R : Any>
constructor(inClass: KClass<T>, outClass: KClass<R>) {
    private val outConstructor = outClass.primaryConstructor!!
    private val inPropertiesByName by lazy {
        inClass.memberProperties.associateBy { it.name }
    }

    fun transform(data: T): R = with(outConstructor) {
        callBy(parameters.associate { parameter ->
            parameter to argFor(parameter, data)
        })
    }

    open fun argFor(parameter: KParameter, data: T): Any? {
        return inPropertiesByName[parameter.name]?.get(data)
    }
}