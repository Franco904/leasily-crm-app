package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.application.theme.AppTheme
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.StayCalendarDate
import com.fstengineering.leasilycrm.core.presentation.utils.daysOfWeek
import com.fstengineering.leasilycrm.core.presentation.utils.getFirstDayOfWeekOfMonth
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelByNumber
import com.fstengineering.leasilycrm.core.presentation.utils.getNumberOfDaysOfMonth
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun StayPeriodsCalendar(
    month: Int,
    year: Int,
    modifier: Modifier = Modifier,
    stayCalendarDates: Pair<CalendarMonthYear, ImmutableList<StayCalendarDate>>? = null,
    singleStayPeriodColor: Color? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    val context = LocalContext.current

    val monthLabel = remember(month) { context.getMonthLabelByNumber(monthNumber = month) }
    val numberOfDaysOfMonth =
        remember(month, year) { getNumberOfDaysOfMonth(month, year) }
    val firstDayOfWeek =
        remember(month, year) { getFirstDayOfWeekOfMonth(month, year) }

    val daysOfWeekLabels = remember {
        daysOfWeek.map { context.getString(it) }.toImmutableList()
    }
    val daysOfMonth = remember(numberOfDaysOfMonth) {
        List(numberOfDaysOfMonth) { day -> (day + 1).toString() }.toImmutableList()
    }

    AppCard(
        modifier = modifier
            .widthIn(max = 400.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    end = 16.dp,
                    bottom = if (actions != null) 16.dp else 24.dp,
                )
        ) {
            MonthLabelSection(
                monthLabel = monthLabel,
                year = year,
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (stayCalendarDates?.second.isNullOrEmpty()) {
                CurrentMonthStaysEmptySection(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .padding(vertical = 64.dp)
                )
            } else {
                StayDatesSection(
                    daysOfWeekLabels = daysOfWeekLabels.toPersistentList(),
                    firstDayOfWeek = firstDayOfWeek,
                    daysOfMonth = daysOfMonth.toPersistentList(),
                    selectedDatesWithMonthYear = stayCalendarDates,
                    singleStayPeriodColor = singleStayPeriodColor,
                )
            }
            if (actions != null) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    actions()
                }
            }
        }
    }
}

@Composable
fun MonthLabelSection(
    monthLabel: String,
    year: Int?,
    modifier: Modifier = Modifier,
) {
    Text(
        if (year != null) "$monthLabel $year" else monthLabel,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .padding(start = 16.dp)
    )
}

@Composable
fun StayDatesSection(
    daysOfWeekLabels: ImmutableList<String>,
    firstDayOfWeek: Int,
    daysOfMonth: ImmutableList<String>,
    selectedDatesWithMonthYear: Pair<CalendarMonthYear, ImmutableList<StayCalendarDate>>?,
    singleStayPeriodColor: Color?,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(daysOfWeekLabels.size),
        modifier = modifier
            .padding(start = 13.dp)
            .wrapContentSize()
            .heightIn(max = 500.dp)
    ) {
        items(daysOfWeekLabels) { dayOfWeek ->
            CalendarDate(
                dayText = dayOfWeek,
                isWeekDayLabel = true,
                singleStayPeriodColor = singleStayPeriodColor,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 24.dp)
            )
        }

        items(firstDayOfWeek - 1) {
            CalendarDate(
                dayText = "N",
                mustHideText = true,
                singleStayPeriodColor = singleStayPeriodColor,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 24.dp)
            )
        }

        // TODO: Improve logic to add a isSelected attr to CalendarSelectedDate and use it here
        items(daysOfMonth) { day ->
            val selectedDaysOfMonth = selectedDatesWithMonthYear?.second?.map { it.dayOfMonth }

            if (selectedDatesWithMonthYear == null || day !in selectedDaysOfMonth!!) {
                val paddingBottom = if (day in daysOfMonth.takeLast(7)) 0.dp else 24.dp

                CalendarDate(
                    dayText = day,
                    singleStayPeriodColor = singleStayPeriodColor,
                    modifier = Modifier
                        .padding(bottom = paddingBottom)
                        .offset(
                            x = if (day.length == 1) (-0.5).dp else 0.dp,
                        )
                )
            } else {
                val selectedDate = selectedDatesWithMonthYear.second.find { it.dayOfMonth == day }

                val paddingBottom = when {
                    day in daysOfMonth.takeLast(7) -> 0.dp
                    selectedDate != null -> 16.dp
                    else -> 24.dp
                }

                CalendarDate(
                    dayText = day,
                    selectedDate = selectedDate,
                    singleStayPeriodColor = singleStayPeriodColor,
                    modifier = Modifier
                        .padding(bottom = paddingBottom)
                        .offset(
                            x = if (day.length == 1) (-0.5).dp else 0.dp,
                            y = if (selectedDate != null) (-5.5).dp else 0.dp,
                        )
                )
            }
        }
    }
}

@Composable
fun CalendarDate(
    dayText: String,
    singleStayPeriodColor: Color?,
    modifier: Modifier = Modifier,
    mustHideText: Boolean = false,
    isWeekDayLabel: Boolean = false,
    selectedDate: StayCalendarDate? = null,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AnimatedVisibility(selectedDate != null) {
            if (selectedDate == null) return@AnimatedVisibility

            StayDateCircle(
//                selectionRange = selectedDate.rangeSelectionLabel,
                singleStayPeriodColor = singleStayPeriodColor,
                isCheckInDate = selectedDate.isCheckOutDate,
            )
        }
        Text(
            text = dayText,
            color = if (mustHideText) Color.Transparent else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isWeekDayLabel) FontWeight.Medium else null,
            ),
        )
    }
}

@Composable
fun StayDateCircle(
    isCheckInDate: Boolean,
    singleStayPeriodColor: Color?,
    modifier: Modifier = Modifier,
//    selectionRange: Pair<RangeSelectionLabel, RangeSelectionLabel>,
) {
//    val (firstCircleHalfRange, secondCircleHalfRange) = selectionRange

//    val firstCircleHalfColor = firstCircleHalfRange.color()
//    val secondCircleHalfColor = secondCircleHalfRange.color()

    val secondaryContainerColor = singleStayPeriodColor ?: MaterialTheme.colorScheme.secondaryContainer
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        StayDateSemiCircle(
            size = 28.dp,
            firstHalfColor = when {
                isCheckInDate -> secondaryContainerColor
                else -> backgroundColor
            },
            secondHalfColor = when {
                isCheckInDate -> backgroundColor
                else -> secondaryContainerColor
            },
        )
//        SelectedDateSubCircle(
//            size = 22.dp,
//            firstCircleHalfColor = if (firstCircleHalfRange == RangeSelectionLabel.First) {
//                firstCircleHalfColor
//            } else backgroundColor,
//            secondCircleHalfColor = if (secondCircleHalfRange == RangeSelectionLabel.First) {
//                secondCircleHalfColor
//            } else backgroundColor,
//        )
//        if (firstCircleHalfRange.count > RangeSelectionLabel.First.count && isRangeEnd) {
//            SelectedDateMiddleDivider(
//                firstCircleHalfColor = firstCircleHalfColor,
//            )
//        }
    }
}

@Composable
fun StayDateSemiCircle(
    size: Dp,
    firstHalfColor: Color,
    secondHalfColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // arc 0° angle starts at 3 o'clock
            drawArc(
                color = firstHalfColor,
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = true,
                style = Fill,
            )
            drawArc(
                color = secondHalfColor,
                startAngle = 270f,
                sweepAngle = 180f,
                useCenter = true,
                style = Fill,
            )
        }
    }
}

@Composable
fun SelectedDateMiddleDivider(
    firstCircleHalfColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(28.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawLine(
                color = firstCircleHalfColor,
                start = Offset(x = canvasWidth / 2, y = 0f), // Start from the top
                end = Offset(x = canvasWidth / 2, y = canvasHeight), // End at the bottom
                strokeWidth = 8f,
            )
        }
    }
}

@Preview
@Composable
fun StayPeriodsCalendarPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        StayPeriodsCalendar(
            month = 1,
            year = 2026,
            stayCalendarDates = Pair(
                CalendarMonthYear(-1, 1, 2025),
                persistentListOf(
                    StayCalendarDate(
                        "4",
                        isCheckInDate = true,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "5",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "6",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "7",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "8",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "9",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "10",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "11",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "12",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "13",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "14",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "15",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "16",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "17",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "18",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "19",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "20",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "21",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "22",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "23",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "24",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "25",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "26",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "27",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "28",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "29",
                        isCheckInDate = false,
                        isCheckOutDate = false,
                        isUnavailableDate = false,
                    ),
                    StayCalendarDate(
                        "30",
                        isCheckInDate = false,
                        isCheckOutDate = true,
                        isUnavailableDate = false,
                    ),
                ),
            ),
            singleStayPeriodColor = MaterialTheme.colorScheme.secondaryContainer,
            modifier = modifier,
        )
    }
}
