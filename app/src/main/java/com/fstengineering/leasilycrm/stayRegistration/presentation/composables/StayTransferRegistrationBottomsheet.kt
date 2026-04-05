package com.fstengineering.leasilycrm.stayRegistration.presentation.composables

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StayTransferRegistrationBottomsheet(
//    sheetState: SheetState,
//    transferForm: StayTransfer,
//    onTransferFormChange: (StayTransfer) -> Unit,
//    formErrors: ImmutableMap<StayTransferField, Int?>,
//    onDismissRequest: () -> Unit,
//    onSave: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    val focusManager = LocalFocusManager.current
//
//    val transferAmountFieldFocusRequester = remember { FocusRequester() }
//    val transferDescriptionFieldFocusRequester = remember { FocusRequester() }
//
//    val initialDateSelection = remember {
//        val calendar = Calendar.getInstance()
//
//        Triple(
//            calendar.get(Calendar.DAY_OF_MONTH),
//            (calendar.get(Calendar.MONTH) + 1),
//            calendar.get(Calendar.YEAR)
//        )
//    }
//
//    var transferAmountInput by rememberSaveable(transferForm.transferAmount) {
//        mutableStateOf(transferForm.transferAmount?.toString() ?: "")
//    }
//
//    var mustShowDatePickerDialog by rememberSaveable { mutableStateOf(false) }
//
//    ModalBottomSheet(
//        onDismissRequest = onDismissRequest,
//        sheetState = sheetState,
//        modifier = modifier
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//                .navigationBarsPadding()
//                .pointerInput(Unit) {
//                    detectTapGestures { focusManager.clearFocus() }
//                }
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Text(
//                    text = stringResource(
//                        when {
//                            transferForm.id == null -> R.string.stay_registration_transfer_registration_title_create
//                            else -> R.string.stay_registration_transfer_registration_title_edit
//                        }
//                    ),
//                    style = MaterialTheme.typography.titleLarge,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    modifier = Modifier
//                        .weight(1f)
//                )
//                OutlinedButton(
//                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
//                    onClick = {
//                        val amount = transferAmountInput.replace(',', '.').toDecimalOrNull()
//                        onTransferFormChange(transferForm.copy(transferAmount = amount))
//
//                        onSave()
//                    },
//                ) {
//                    Text(
//                        text = stringResource(R.string.save_action_button_text),
//                        style = MaterialTheme.typography.titleSmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            _root_ide_package_.com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields.StayTransferDateFormField(
//                transferDate = transferForm.date,
//                error = formErrors[StayTransferField.TransferDate],
//                onTap = { mustShowDatePickerDialog = true },
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            _root_ide_package_.com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields.StayTransferAmountFormField(
//                transferAmount = transferAmountInput,
//                onTransferAmountChange = { transferAmountInput = it },
//                error = formErrors[StayTransferField.TransferAmount],
//                onDone = { transferDescriptionFieldFocusRequester.requestFocus() },
//                fieldFocusRequester = transferAmountFieldFocusRequester,
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            _root_ide_package_.com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields.StayTransferDescriptionFormField(
//                value = transferForm.transferDescription,
//                onValueChange = { onTransferFormChange(transferForm.copy(transferDescription = it)) },
//                fieldFocusRequester = transferDescriptionFieldFocusRequester,
//            )
//            Spacer(modifier = Modifier.height(72.dp))
//        }
//    }
//    if (mustShowDatePickerDialog) {
//        val (initialDayOfMonth, initialMonth, initialYear) = initialDateSelection
//
//        DatePickerDialog(
//            initialDisplayedMonthMillis = getMonthTimestamp(
//                month = initialMonth,
//                year = initialYear,
//            ),
//            selectableDates = getSelectableDates(
//                initialDayOfMonth = initialDayOfMonth,
//                initialMonth = initialMonth,
//                initialYear = initialYear,
//                forceBelow = true,
//                alreadySelectedDates = persistentSetOf(),
//            ),
//            onDateSelected = { selectedDate ->
//                onTransferFormChange(
//                    transferForm.copy(transferDate = selectedDate)
//                )
//
//                mustShowDatePickerDialog = false
//
//                transferAmountFieldFocusRequester.requestFocus()
//            },
//            onDismiss = { mustShowDatePickerDialog = false },
//        )
//    }
//}
