package io.komune.registry.s2.asset.api.exception

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import f2.spring.exception.F2HttpException
import io.komune.registry.s2.commons.exception.ExceptionCodes
import org.springframework.http.HttpStatus

class GranularityTooSmallException(
    transaction: BigDecimal,
    granularity: Double
): F2HttpException(
    status = HttpStatus.BAD_REQUEST,
    code = ExceptionCodes.Asset.GRANULARITY_TOO_SMALL,
    message = "Cannot emit a transaction with a granularity this small ($transaction) " +
            "in this asset pool (granularity: $granularity)",
    cause = null
)
