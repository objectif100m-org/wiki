package io.komune.registry.f2.license.api

import f2.dsl.fnc.f2Function
import io.komune.registry.f2.license.api.service.LicenseF2FinderService
import io.komune.registry.f2.license.api.service.LicensePoliciesEnforcer
import io.komune.registry.f2.license.domain.LicenseApi
import io.komune.registry.f2.license.domain.command.LicenseCreateFunction
import io.komune.registry.f2.license.domain.command.LicenseCreatedEventDTOBase
import io.komune.registry.f2.license.domain.command.LicenseUpdateFunction
import io.komune.registry.f2.license.domain.command.LicenseUpdatedEventDTOBase
import io.komune.registry.f2.license.domain.query.LicenseGetByIdentifierFunction
import io.komune.registry.f2.license.domain.query.LicenseGetByIdentifierResult
import io.komune.registry.f2.license.domain.query.LicenseGetFunction
import io.komune.registry.f2.license.domain.query.LicenseGetResult
import io.komune.registry.f2.license.domain.query.LicenseListFunction
import io.komune.registry.f2.license.domain.query.LicenseListResult
import io.komune.registry.s2.license.api.LicenseAggregateService
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import s2.spring.utils.logger.Logger

@RestController
@RequestMapping
class LicenseEndpoint(
    private val licenseAggregateService: LicenseAggregateService,
    private val licenseF2FinderService: LicenseF2FinderService,
    private val licensePoliciesEnforcer: LicensePoliciesEnforcer
) : LicenseApi {

    private val logger by Logger()

    @Bean
    override fun licenseGet(): LicenseGetFunction = f2Function { query ->
        logger.info("licenseGet: $query")
        licenseF2FinderService.getOrNull(query.id)
            .let(::LicenseGetResult)
    }

    @Bean
    override fun licenseGetByIdentifier(): LicenseGetByIdentifierFunction = f2Function { query ->
        logger.info("licenseGetByIdentifier: $query")
        licenseF2FinderService.getByIdentifierOrNull(query.identifier)
            .let(::LicenseGetByIdentifierResult)
    }

    @Bean
    override fun licenseList(): LicenseListFunction = f2Function {
        logger.info("licenseList")
        licenseF2FinderService.list()
            .let(::LicenseListResult)
    }

    @Bean
    override fun licenseCreate(): LicenseCreateFunction = f2Function { command ->
        logger.info("licenseCreate: $command")
        licensePoliciesEnforcer.checkCreate()
        licenseAggregateService.create(command).let {
            LicenseCreatedEventDTOBase(
                id = it.id,
                identifier = it.identifier
            )
        }
    }

    @Bean
    override fun licenseUpdate(): LicenseUpdateFunction = f2Function { command ->
        logger.info("licenseUpdate: $command")
        licensePoliciesEnforcer.checkUpdate()
        licenseAggregateService.update(command)
            .let { LicenseUpdatedEventDTOBase(id = it.id) }
    }
}
