package io.komune.registry.s2.asset.domain.automate

import io.komune.registry.s2.asset.domain.command.transaction.AssetTransactionEmitCommand
import io.komune.registry.s2.asset.domain.command.transaction.TransactionEmittedEvent
import io.komune.registry.s2.commons.model.S2SourcingEvent
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlinx.serialization.Serializable
import s2.dsl.automate.S2Command
import s2.dsl.automate.S2InitCommand
import s2.dsl.automate.S2Role
import s2.dsl.automate.S2State
import s2.dsl.automate.builder.s2Sourcing

val s2AssetTransaction = s2Sourcing {
    name = "AssetTransactionS2"
    init<AssetTransactionEmitCommand, TransactionEmittedEvent> {
        to = AssetTransactionState.EMITTED
        role = AssetTransactionRole.Emitter
    }
}

/**
 * @d2 hidden
 * @visual json "142e9880-1da2-4c42-b121-de5d150ca848"
 */
typealias AssetTransactionId = String

/**
 * @d2 automate
 * @visual platform/s2/asset/asset-domain/build/s2-documenter/AssetTransactionS2.json
 * @order 100
 * @title Transaction States
 */
@Serializable
enum class AssetTransactionState(override val position: Int): S2State {
    EMITTED(0),    CANCELLED(1),
}

enum class AssetTransactionRole(val value: String): S2Role {
    Emitter("Emitter");
    override fun toString() = value
}

@JsExport
@JsName("AssetTransactionInitCommand")
interface AssetTransactionInitCommand: S2InitCommand

@JsExport
@JsName("AssetTransactionCommand")
interface AssetTransactionCommand: S2Command<AssetTransactionId>

@JsExport
@JsName("AssetTransactionEvent")
interface AssetTransactionEvent: S2SourcingEvent<AssetTransactionId>
