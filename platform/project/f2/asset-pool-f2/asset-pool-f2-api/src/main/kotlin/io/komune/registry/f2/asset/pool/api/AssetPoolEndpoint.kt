package io.komune.registry.f2.asset.pool.api

import f2.dsl.cqrs.filter.ExactMatch
import f2.dsl.cqrs.filter.StringMatch
import f2.dsl.cqrs.filter.StringMatchCondition
import f2.dsl.cqrs.page.OffsetPagination
import f2.dsl.fnc.f2Function
import io.komune.fs.s2.file.client.FileClient
import io.komune.fs.spring.utils.serveFile
import io.komune.registry.api.commons.utils.anyNotNull
import io.komune.registry.f2.asset.pool.api.service.AssetPoolF2AggregateService
import io.komune.registry.f2.asset.pool.api.service.AssetPoolF2FinderService
import io.komune.registry.f2.asset.pool.api.service.AssetPoolPoliciesEnforcer
import io.komune.registry.f2.asset.pool.api.service.AssetTransactionF2FinderService
import io.komune.registry.f2.asset.pool.domain.AssetPoolApi
import io.komune.registry.f2.asset.pool.domain.command.AssetIssueFunction
import io.komune.registry.f2.asset.pool.domain.command.AssetIssuedEventDTOBase
import io.komune.registry.f2.asset.pool.domain.command.AssetOffsetFunction
import io.komune.registry.f2.asset.pool.domain.command.AssetOffsettedEventDTOBase
import io.komune.registry.f2.asset.pool.domain.command.AssetPoolCloseFunction
import io.komune.registry.f2.asset.pool.domain.command.AssetPoolClosedEventDTOBase
import io.komune.registry.f2.asset.pool.domain.command.AssetPoolCreateFunction
import io.komune.registry.f2.asset.pool.domain.command.AssetPoolCreatedEventDTOBase
import io.komune.registry.f2.asset.pool.domain.command.AssetPoolHeldEventDTOBase
import io.komune.registry.f2.asset.pool.domain.command.AssetPoolHoldFunction
import io.komune.registry.f2.asset.pool.domain.command.AssetPoolResumeFunction
import io.komune.registry.f2.asset.pool.domain.command.AssetPoolResumedEventDTOBase
import io.komune.registry.f2.asset.pool.domain.command.AssetRetireFunction
import io.komune.registry.f2.asset.pool.domain.command.AssetRetiredEventDTOBase
import io.komune.registry.f2.asset.pool.domain.command.AssetTransferFunction
import io.komune.registry.f2.asset.pool.domain.command.AssetTransferredEventDTOBase
import io.komune.registry.f2.asset.pool.domain.query.AssetCertificateDownloadQuery
import io.komune.registry.f2.asset.pool.domain.query.AssetPoolGetFunction
import io.komune.registry.f2.asset.pool.domain.query.AssetPoolGetResultDTOBase
import io.komune.registry.f2.asset.pool.domain.query.AssetPoolPageFunction
import io.komune.registry.f2.asset.pool.domain.query.AssetPoolPageResult
import io.komune.registry.f2.asset.pool.domain.query.AssetStatsGetFunction
import io.komune.registry.f2.asset.pool.domain.query.AssetTransactionGetFunction
import io.komune.registry.f2.asset.pool.domain.query.AssetTransactionGetResult
import io.komune.registry.f2.asset.pool.domain.query.AssetTransactionPageFunction
import io.komune.registry.f2.asset.pool.domain.query.AssetTransactionPageResultDTOBase
import io.komune.registry.s2.asset.domain.automate.AssetPoolState
import io.komune.registry.s2.asset.domain.automate.AssetTransactionId
import io.komune.registry.s2.asset.domain.model.AssetTransactionType
import jakarta.annotation.security.PermitAll
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Configuration
@RestController
class AssetPoolEndpoint(
    private val assetPoolF2AggregateService: AssetPoolF2AggregateService,
    private val assetPoolF2FinderService: AssetPoolF2FinderService,
    private val assetTransactionF2FinderService: AssetTransactionF2FinderService,
    private val fileClient: FileClient,
    private val assetPoolPoliciesEnforcer: AssetPoolPoliciesEnforcer,
    ): AssetPoolApi {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    override fun assetPoolGet(): AssetPoolGetFunction = f2Function { query ->
        logger.info("assetPoolGet: $query")
        assetPoolF2FinderService.getOrNull(query.id).let(::AssetPoolGetResultDTOBase)
    }

    @Bean
    override fun assetPoolPage(): AssetPoolPageFunction = f2Function { query ->
        logger.info("projectPage: $query")
        assetPoolPoliciesEnforcer.checkList()
        val pagination = OffsetPagination(
            offset = query.offset ?: 0,
            limit = query.limit ?: 10,
        )

        assetPoolF2FinderService.page(
            status = query.status?.let { ExactMatch(AssetPoolState.valueOf(it)) },
            vintage = query.vintage?.ifEmpty { null }?.let { StringMatch(it, StringMatchCondition.CONTAINS) },
            offset = pagination
        ).let { page ->
            AssetPoolPageResult(
                items = page.items,
                total = page.total,
                pagination = pagination
            )
        }
    }

    @Bean
    override fun assetPoolCreate(): AssetPoolCreateFunction = f2Function { command ->
        logger.info("assetPoolCreate: $command")
        assetPoolPoliciesEnforcer.checkCreate()
        assetPoolF2AggregateService.create(command)
            .let { AssetPoolCreatedEventDTOBase(it.id) }
    }

    @Bean
    override fun assetPoolHold(): AssetPoolHoldFunction = f2Function { command ->
        logger.info("assetPoolHold: $command")
        assetPoolPoliciesEnforcer.checkHold(command.id)
        assetPoolF2AggregateService.hold(command)
            .let { AssetPoolHeldEventDTOBase(it.id) }
    }

    @Bean
    override fun assetPoolResume(): AssetPoolResumeFunction = f2Function { command ->
        logger.info("assetPoolResume: $command")
        assetPoolPoliciesEnforcer.checkResume(command.id)
        assetPoolF2AggregateService.resume(command)
            .let { AssetPoolResumedEventDTOBase(it.id) }
    }

    @Bean
    override fun assetPoolClose(): AssetPoolCloseFunction = f2Function { command ->
        logger.info("assetPoolClose: $command")
        assetPoolPoliciesEnforcer.checkClose(command.id)
        assetPoolF2AggregateService.close(command)
            .let { AssetPoolClosedEventDTOBase(it.id) }
    }

    @Bean
    override fun assetIssue(): AssetIssueFunction = f2Function { command ->
        logger.info("assetIssue: $command")
        assetPoolPoliciesEnforcer.checkIssue()
        assetPoolF2AggregateService.issue(command)
            .let {
                AssetIssuedEventDTOBase(
                    id = it.id,
                    transactionId = it.transactionId
                )
            }
    }

    @Bean
    override fun assetTransfer(): AssetTransferFunction = f2Function { command ->
        logger.info("assetTransfer: $command")
        assetPoolPoliciesEnforcer.checkTransfer()
        assetPoolF2AggregateService.transfer(command)
            .let { AssetTransferredEventDTOBase(it.id) }
    }

    @Bean
    override fun assetOffset(): AssetOffsetFunction = f2Function { command ->
        logger.info("assetOffset: $command")
        assetPoolPoliciesEnforcer.checkOffset()
        assetPoolF2AggregateService.offset(command)
            .let { AssetOffsettedEventDTOBase(it.id) }
    }

    @Bean
    override fun assetRetire(): AssetRetireFunction = f2Function { command ->
        logger.info("assetRetire: $command")
        assetPoolPoliciesEnforcer.checkRetire()
        assetPoolF2AggregateService.retire(command)
            .let { AssetRetiredEventDTOBase(it.id) }
    }

    @PermitAll
    @Bean
    override fun assetTransactionPage(): AssetTransactionPageFunction = f2Function { query ->
        logger.info("assetTransactionPage: $query")
        assetTransactionF2FinderService.page(
            projectId = query.projectId?.let(::ExactMatch),
            poolId = query.poolId?.let(::ExactMatch),
            transactionId = query.transactionId?.let { StringMatch(it, StringMatchCondition.CONTAINS) },
            transactionFrom = query.transactionFrom?.let(::ExactMatch),
            transactionTo = query.transactionTo?.let(::ExactMatch),
            type = query.type?.let { ExactMatch(AssetTransactionType.valueOf(it)) },
            offset = takeIf { anyNotNull(query.limit, query.offset) }?.let {
                OffsetPagination(
                    offset = query.offset ?: 0,
                    limit = query.limit ?: Int.MAX_VALUE
                )
            }
        ).let {
            AssetTransactionPageResultDTOBase(
                items = it.items,
                total = it.total
            )
        }
    }

    @Bean
    @PermitAll
    override fun assetTransactionGet(): AssetTransactionGetFunction = f2Function { query ->
        logger.info("assetTransactionGet: $query")
        assetTransactionF2FinderService.getTransaction(query.transactionId).let(::AssetTransactionGetResult)
    }

    @Bean
    @PermitAll
    override fun assetStatsGet(): AssetStatsGetFunction = f2Function { query ->
        logger.info("assetStatsGet: $query")
        assetTransactionF2FinderService.getAssetStats(query.projectId)
    }

//    @PermitAll
//    @PostMapping("/assetCertificateDownload")
//    suspend fun assetCertificateDownload(
//        @RequestBody query: AssetCertificateDownloadQuery,
//        response: ServerHttpResponse
//    ): ResponseEntity<InputStreamResource> {
//        logger.info("assetCertificateDownload: $query")
//        val stream = assetCertificationF2FinderService.assetCertificateDownload(query.transactionId, response)
//        return ResponseEntity.ok()
//            .body(InputStreamResource(stream))
//    }
//
//    @PermitAll
//    @GetMapping("/assetCertificateDownload")
//    suspend fun assetCertificateDownload(
//        @RequestParam transactionId: AssetTransactionId,
//        response: ServerHttpResponse
//    ): ResponseEntity<InputStreamResource> {
//        logger.info("assetCertificateDownload: $transactionId")
//        val stream = assetCertificationF2FinderService.assetCertificateDownload(transactionId, response)
//        return ResponseEntity.ok()
//            .body(InputStreamResource(stream))
//    }

    @PermitAll
    @PostMapping("/assetCertificateDownload")
    suspend fun assetCertificateDownload(
        @RequestBody query: AssetCertificateDownloadQuery,
        response: ServerHttpResponse
    ): ResponseEntity<InputStreamResource> = serveFile(fileClient) {
        logger.info("assetCertificateDownload: $query")
        assetTransactionF2FinderService.getTransaction(query.transactionId).file
    }

    @PermitAll
    @GetMapping("/assetCertificateDownload", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun assetCertificateDownload(
        @RequestParam transactionId: AssetTransactionId,
        response: ServerHttpResponse
    ): ResponseEntity<InputStreamResource> = serveFile(fileClient) {
        logger.info("assetCertificateDownload: $transactionId")
        assetTransactionF2FinderService.getTransaction(transactionId).file
    }

}
