package com.simenko.qmapp.utils

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

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
        return when (parameter.name) {
//            Fields added only in domain model
            "channelsVisibility" -> {
                false
            }
            "linesVisibility" -> {
                false
            }
            "operationVisibility" -> {
                false
            }
            "detailsVisibility" -> {
                false
            }
            "subOrdersVisibility" -> {
                false
            }
            "tasksVisibility" -> {
                false
            }
            "measurementsVisibility" -> {
                false
            }
            "isExpanded" -> {
                false
            }
            "isSelected" -> {
                false
            }
            else -> return inPropertiesByName[parameter.name]?.get(data)
        }
    }
}

class ListTransformer<in T : Any, out R : Any> constructor(
    inClass: KClass<T>, outClass: KClass<R>
) {
    private var inputList: List<T> = listOf()

    constructor(inList: List<T>, inClass: KClass<T>, outClass: KClass<R>) : this(
        inClass,
        outClass
    ) {
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
        return when (parameter.name) {
//            Fields added only in domain model
            "channelsVisibility" -> {
                false
            }
            "linesVisibility" -> {
                false
            }
            "operationVisibility" -> {
                false
            }
            "detailsVisibility" -> {
                false
            }
            "subOrdersVisibility" -> {
                false
            }
            "tasksVisibility" -> {
                false
            }
            "measurementsVisibility" -> {
                false
            }
            "isExpanded" -> {
                false
            }
            "isSelected" -> {
                false
            }
            else -> return inPropertiesByName[parameter.name]?.get(data)
        }
    }

    fun generateList(): List<R> {
        return inputList.map() {
            transform(it)
        }
    }
}

open class ItemTransformer<in T : Any, out R : Any>
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

    fun argFor(parameter: KParameter, data: T): Any? {
        return when (parameter.name) {
            "itemDesignation" -> {
                when {
                    inPropertiesByName["productDesignation"] != null -> inPropertiesByName["productDesignation"]?.get(data)
                    inPropertiesByName["componentDesignation"] != null -> inPropertiesByName["componentDesignation"]?.get(data)
                    inPropertiesByName["componentInStageDescription"] != null -> inPropertiesByName["componentInStageDescription"]?.get(data)
                    else -> {}
                }

            }
            "itemId" -> {
                when {
                    inPropertiesByName["productId"] != null -> inPropertiesByName["productId"]?.get(data)
                    inPropertiesByName["componentId"] != null -> inPropertiesByName["componentId"]?.get(data)
                    inPropertiesByName["componentInStageId"] != null -> inPropertiesByName["componentInStageId"]?.get(data)
                    else -> {}
                }

            }
            else -> inPropertiesByName[parameter.name]?.get(data)
        }
    }
}