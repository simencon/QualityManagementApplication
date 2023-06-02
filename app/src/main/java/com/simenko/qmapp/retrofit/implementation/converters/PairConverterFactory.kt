package com.simenko.qmapp.retrofit.implementation.converters

import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PairParam

class PairConverterFactory : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        if (annotations.any { it.annotationClass == PairParam::class }) {
            return Converter<Pair<*, *>, String> { pair ->
                "${pair.first}/${pair.second}"
            }
        }
        return super.stringConverter(type, annotations, retrofit)
    }
}