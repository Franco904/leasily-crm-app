package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields

//@Composable
//fun StayTransfersFormField(
//    stayNotDeletedTransfers: PersistentList<StayTransfer>,
//    onAddTransfer: () -> Unit,
//    onEditTransfer: (StayTransfer) -> Unit,
//    onDeleteTransfer: (StayTransfer) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    Column(
//        modifier = modifier
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Text(
//                text = stringResource(R.string.stay_registration_transfers_title),
//                style = MaterialTheme.typography.titleMedium,
//                color = MaterialTheme.colorScheme.onSurface,
//                modifier = Modifier
//                    .weight(1f)
//            )
//            TextButton(onClick = onAddTransfer) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary,
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = stringResource(R.string.add_action_button_text),
//                    style = MaterialTheme.typography.labelLarge,
//                    color = MaterialTheme.colorScheme.primary,
//                )
//            }
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(
//            text = stringResource(R.string.stay_registration_transfers_body_text),
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        StayTransfersCard(
//            stayTransfers = stayNotDeletedTransfers,
//            onEditTransfer = onEditTransfer,
//            onDeleteTransfer = onDeleteTransfer,
//        )
//    }
//}
