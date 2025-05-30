package io.komune.registry.s2.catalogue.domain.command

import io.komune.registry.s2.catalogue.domain.model.CatalogueAccessRight
import io.komune.registry.s2.commons.model.CatalogueId
import io.komune.registry.s2.commons.model.CatalogueIdentifier
import io.komune.registry.s2.commons.model.DatasetId
import io.komune.registry.s2.commons.model.Location
import io.komune.registry.s2.commons.model.OrganizationId
import io.komune.registry.s2.commons.model.UserId
import io.komune.registry.s2.concept.domain.ConceptId
import io.komune.registry.s2.license.domain.LicenseId
import io.komune.registry.s2.structure.domain.model.Structure
import kotlinx.serialization.Serializable

data class CatalogueCreateCommand(
    val identifier: CatalogueIdentifier,
    val title: String,
    val type: String,
    val language: String?,
    val description: String?,
    val themeIds: Set<ConceptId>,
    val homepage: String?,
    val ownerOrganizationId: OrganizationId?,
    val stakeholder: String?,
    val structure: Structure?,
    val isTranslationOf: CatalogueId?,
    val catalogueIds: Set<CatalogueId>,
    val datasetIds: Set<DatasetId>,
    val accessRights: CatalogueAccessRight?,
    val licenseId: LicenseId?,
    val location: Location?,
    val versionNotes: String?,
    val hidden: Boolean,
    val integrateCounter: Boolean?
): CatalogueInitCommand

sealed interface CatalogueDataEvent : CatalogueEvent {
    val title: String
    val language: String?
    val description: String?
    val themeIds: Set<ConceptId>
    val homepage: String?
    val ownerOrganizationId: OrganizationId?
    val stakeholder: String?
    val structure: Structure?
    val accessRights: CatalogueAccessRight
    val licenseId: LicenseId?
    val location: Location?
    val versionNotes: String?
    val hidden: Boolean
    val integrateCounter: Boolean?
}

@Serializable
data class CatalogueCreatedEvent(
    override val id: CatalogueId,
    val identifier: CatalogueIdentifier,
    override val title: String,
    val type: String,
    override val language: String?,
    override val description: String? = null,
    override val themeIds: Set<ConceptId> = emptySet(),
    override val homepage: String? = null,
    override val structure: Structure? = null,
    val isTranslationOf: CatalogueId?,
    val catalogueIds: Set<CatalogueId> = emptySet(),
    val datasetIds: Set<DatasetId> = emptySet(),
    val creatorId: UserId?,
    val creatorOrganizationId: OrganizationId?,
    override val ownerOrganizationId: OrganizationId?,
    override val stakeholder: String?,
    override val accessRights: CatalogueAccessRight,
    override val licenseId: LicenseId? = null,
    override val location: Location? = null,
    override val versionNotes: String? = null,
    override val hidden: Boolean = false,
    override val date: Long,
    override val integrateCounter: Boolean?
): CatalogueDataEvent
