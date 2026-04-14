package com.fstengineering.leasilycrm.stayDetails.presentation.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconValueDetailsItem
import com.fstengineering.leasilycrm.core.presentation.utils.orNotRegistered

@Composable
fun StayTenantVehicleDataDetailsItem(
    vehicleMake: String?,
    vehicleColor: String?,
    vehicleLicensePlate: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val notRegisteredString =
        remember { context.getString(R.string.not_registered_info_var_2_body_text).lowercase() }

    IconValueDetailsItem(
        icon = Icons.Outlined.DirectionsCar,
        value = stringResource(
            id = R.string.stay_details_tenant_vehicle_data_body_text,
            vehicleMake.orNotRegistered(notRegisteredString),
            vehicleColor.orNotRegistered(notRegisteredString),
            vehicleLicensePlate.orNotRegistered(notRegisteredString),
        ),
        modifier = modifier
    )
}
