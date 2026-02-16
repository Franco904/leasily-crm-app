package com.fstengineering.leasilycrm.stayDetails.composables.items

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun StayCommissionDetailsItem(
    commission: AppDecimal?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        iconDrawable = R.drawable.wallet,
        label = stringResource(R.string.stay_details_commission_label),
        value = when {
            commission != null -> {
                stringResource(
                    R.string.reais_currency_value,
                    commission
                        .toString()
                        .replace(".", ","),
                )
            }

            else -> stringResource(R.string.not_registered_info_body_text)
        },
        isEmptyText = commission == null,
        modifier = modifier
    )
}
