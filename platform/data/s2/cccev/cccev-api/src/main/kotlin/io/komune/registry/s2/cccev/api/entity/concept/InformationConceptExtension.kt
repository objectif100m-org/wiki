package io.komune.registry.s2.cccev.api.entity.concept

import io.komune.registry.s2.cccev.domain.model.InformationConceptModel

fun InformationConceptEntity.toModel() = InformationConceptModel(
    id = id,
    identifier = identifier,
    name = name,
    unit = unit,
    aggregator = aggregator,
    themeIds = themeIds
)
