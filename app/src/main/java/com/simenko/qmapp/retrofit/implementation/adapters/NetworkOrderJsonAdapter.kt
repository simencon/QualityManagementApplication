package com.simenko.qmapp.retrofit.implementation.adapters

import com.simenko.qmapp.retrofit.entities.NetworkOrder
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.utils.StringUtils.FormatForRestService
import com.simenko.qmapp.utils.StringUtils.getStringDate
import com.squareup.moshi.*
import com.squareup.moshi.internal.Util
import java.lang.NullPointerException
import java.lang.reflect.Constructor

class NetworkOrderJsonAdapter : JsonAdapter<NetworkOrder>() {
    private val options: JsonReader.Options = JsonReader.Options.of(
        "id",
        "orderTypeId",
        "reasonId",
        "orderNumber",
        "customerId",
        "orderedById",
        "statusId",
        "createdDate",
        "completedDate"
    )

    private val moshi = Moshi.Builder().build()

    private val intAdapter: JsonAdapter<Int> = moshi
        .adapter(Int::class.java, emptySet(), "id")

    private val nullableIntAdapter: JsonAdapter<Int?> = moshi
        .adapter(Int::class.javaObjectType, emptySet(), "orderNumber")

    private val longAdapter: JsonAdapter<Long> = moshi
        .adapter(Long::class.java, emptySet(), "createdDate")

    private val nullableStringAdapter: JsonAdapter<String?> = moshi
        .adapter(String::class.java, emptySet(), "completedDate")

    @Volatile
    private var constructorRef: Constructor<NetworkOrder>? = null

    override fun toString(): String = buildString(34) {
        append("GeneratedJsonAdapter(").append("NetworkOrder").append(')')
    }

    @FromJson
    override fun fromJson(reader: JsonReader): NetworkOrder {
        var id: Int? = 0
        var orderTypeId: Int? = null
        var reasonId: Int? = null
        var orderNumber: Int? = null
        var customerId: Int? = null
        var orderedById: Int? = null
        var statusId: Int? = null
        var createdDate: Long? = null
        var completedDate: String? = null
        var mask0 = -1
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> {
                    id = intAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                        "id", "id", reader
                    )
                    // $mask = $mask and (1 shl 0).inv()
                    mask0 = mask0 and 0xfffffffe.toInt()
                }
                1 -> orderTypeId = intAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                    "orderTypeId", "orderTypeId", reader
                )
                2 -> reasonId = intAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                    "reasonId", "reasonId", reader
                )
                3 -> {
                    orderNumber = nullableIntAdapter.fromJson(reader)
                    mask0 = mask0 and 0xfffffff7.toInt()
                }
                4 -> customerId = intAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                    "customerId", "customerId", reader
                )
                5 -> orderedById = intAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                    "orderedById", "orderedById", reader
                )
                6 -> statusId = intAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                    "statusId", "statusId", reader
                )
                7 -> createdDate = StringUtils.getMillisecondsDate(
                    nullableStringAdapter.fromJson(reader) ?: ""
                )
                8 -> {
                    completedDate = nullableStringAdapter.fromJson(reader)
                    mask0 = mask0 and 0xfffffeff.toInt()
                }
                -1 -> {
                    // Unknown name, skip it.
                    reader.skipName()
                    reader.skipValue()
                }
            }
        }
        reader.endObject()
        if (mask0 == 0xfffffef6.toInt()) {
            // All parameters with defaults are set, invoke the constructor directly
            return NetworkOrder(
                id = id as Int,
                orderTypeId = orderTypeId ?: throw Util.missingProperty(
                    "orderTypeId", "orderTypeId", reader
                ),
                reasonId = reasonId ?: throw Util.missingProperty(
                    "reasonId", "reasonId", reader
                ),
                orderNumber = orderNumber,
                customerId = customerId ?: throw Util.missingProperty(
                    "customerId", "customerId", reader
                ),
                orderedById = orderedById ?: throw Util.missingProperty(
                    "orderedById", "orderedById", reader
                ),
                statusId = statusId ?: throw Util.missingProperty(
                    "statusId", "statusId", reader
                ),
                createdDate = createdDate ?: throw Util.missingProperty(
                    "createdDate", "createdDate", reader
                ),
                completedDate = completedDate
            )
        } else {
            // Reflectively invoke the synthetic defaults constructor
            @Suppress("UNCHECKED_CAST")
            val localConstructor: Constructor<NetworkOrder> =
                this.constructorRef ?: NetworkOrder::class.java.getDeclaredConstructor(
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    Int::class.javaObjectType,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    Long::class.javaPrimitiveType,
                    String::class.java,
                    Int::class.javaPrimitiveType,
                    Util.DEFAULT_CONSTRUCTOR_MARKER
                ).also { this.constructorRef = it }
            return localConstructor.newInstance(
                id,
                orderTypeId ?: throw Util.missingProperty("orderTypeId", "orderTypeId", reader),
                reasonId ?: throw Util.missingProperty("reasonId", "reasonId", reader),
                orderNumber,
                customerId ?: throw Util.missingProperty("customerId", "customerId", reader),
                orderedById ?: throw Util.missingProperty("orderedById", "orderedById", reader),
                statusId ?: throw Util.missingProperty("statusId", "statusId", reader),
                createdDate ?: throw Util.missingProperty("createdDate", "createdDate", reader),
                completedDate,
                mask0,
                /* DefaultConstructorMarker */ null
            )
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value_: NetworkOrder?): Unit {
        if (value_ == null) {
            throw NullPointerException("value_ was null! Wrap in .nullSafe() to write nullable values.")
        }
        writer.beginObject()
        writer.name("id")
        intAdapter.toJson(writer, value_.id)
        writer.name("orderTypeId")
        intAdapter.toJson(writer, value_.orderTypeId)
        writer.name("reasonId")
        intAdapter.toJson(writer, value_.reasonId)
        writer.name("orderNumber")
        nullableIntAdapter.toJson(writer, value_.orderNumber)
        writer.name("customerId")
        intAdapter.toJson(writer, value_.customerId)
        writer.name("orderedById")
        intAdapter.toJson(writer, value_.orderedById)
        writer.name("statusId")
        intAdapter.toJson(writer, value_.statusId)
        writer.name("createdDate")
        nullableStringAdapter.toJson(writer, getStringDate(value_.createdDate, FormatForRestService.num))
        writer.name("completedDate")
        nullableStringAdapter.toJson(writer, value_.completedDate)
        writer.endObject()
    }
}