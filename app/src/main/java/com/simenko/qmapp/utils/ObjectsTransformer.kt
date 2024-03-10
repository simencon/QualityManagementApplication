package com.simenko.qmapp.utils

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

open class ObjectTransformer<in T : Any, out R : Any>
constructor(inClass: KClass<T>, outClass: KClass<R>) {
    private val outConstructor = outClass.primaryConstructor!!
    private val inPropertiesByName by lazy { inClass.memberProperties.associateBy { it.name } }
    fun transform(data: T): R = with(outConstructor) { callBy(parameters.associateWith { parameter -> argFor(parameter, data) }) }
    open fun argFor(parameter: KParameter, data: T): Any? {
        return when (parameter.name) {
            "channelsVisibility" -> false
            "linesVisibility" -> false
            "operationVisibility" -> false
            "detailsVisibility" -> false
            "isExpanded" -> false
            "isSelected" -> false
            "isNewRecord" -> false
            "toBeDeleted" -> false

            "isLslError" -> false
            "isNominalError" -> false
            "isUslError" -> false

            else -> return inPropertiesByName[parameter.name]?.get(data)
        }
    }
}