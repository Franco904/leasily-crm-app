package com.fstengineering.leasilycrm.stayDetails.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem
import java.math.BigDecimal

@Composable
fun StayTotalPriceDetailsItem(
    totalPrice: BigDecimal?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        icon = Icons.Outlined.AttachMoney,
        label = stringResource(R.string.stay_details_total_price_label),
        value = when {
            totalPrice != null -> {
                stringResource(
                    R.string.reais_currency_value,
                    totalPrice.toString(),
                )
            }

            else -> stringResource(R.string.not_registered_info_body_text)
        },
        isEmptyText = totalPrice == null,
        modifier = modifier
    )
}
