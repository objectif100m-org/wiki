package io.komune.registry.f2.dataset.domain.query

import f2.dsl.fnc.F2Function
import io.komune.registry.s2.commons.model.DatasetId
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


/**
 * List all files of a project.
 * @d2 function
 * @parent [io.komune.registry.f2.dataset.domain.D2DatasetF2Page]
 * @order 30
 */
typealias DatasetDataFunction = F2Function<DatasetDataQuery, DatasetDataResult>

/**
 * @d2 query
 * @parent [DatasetDataFunction]
 */
@JsExport
@JsName("DatasetDataQueryDTO")
interface DatasetDataQueryDTO {
    /**
     * Id of the dataset to get.
     */
    val id: DatasetId
}

/**
 * @d2 inherit
 */
@Serializable
data class DatasetDataQuery(
    override val id: DatasetId
): DatasetDataQueryDTO

/**
 * @d2 event
 * @parent [DatasetDataFunction]
 */
@JsExport
@JsName("DatasetDataResultDTO")
interface DatasetDataResultDTO {
    /**
     * List of paths of all the files uploaded for the dataset.
     */
    val items: List<JsonElement>
}

/**
 * @d2 inherit
 */
@Serializable
data class DatasetDataResult(
    override val items: List<JsonElement>
): DatasetDataResultDTO
