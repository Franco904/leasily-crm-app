package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.theme.AppTheme
import com.fstengineering.leasilycrm.core.application.theme.amber
import com.fstengineering.leasilycrm.core.application.theme.blueGrey
import com.fstengineering.leasilycrm.core.application.theme.brown
import com.fstengineering.leasilycrm.core.application.theme.cyan
import com.fstengineering.leasilycrm.core.application.theme.deepOrange
import com.fstengineering.leasilycrm.core.application.theme.grey
import com.fstengineering.leasilycrm.core.application.theme.indigo
import com.fstengineering.leasilycrm.core.application.theme.lightBlue
import com.fstengineering.leasilycrm.core.application.theme.lime
import com.fstengineering.leasilycrm.core.application.theme.orange
import com.fstengineering.leasilycrm.core.application.theme.pink
import com.fstengineering.leasilycrm.core.application.theme.purple
import com.fstengineering.leasilycrm.core.application.theme.teal
import com.fstengineering.leasilycrm.core.domain.constants.UNAVAILABLE_PERIOD_INDEX
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.StayCalendarDate
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import com.fstengineering.leasilycrm.core.presentation.utils.daysOfWeek
import com.fstengineering.leasilycrm.core.presentation.utils.getFirstDayOfWeekOfMonth
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelByNumber
import com.fstengineering.leasilycrm.core.presentation.utils.getNumberOfDaysOfMonth
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate

val stayPeriodColors
    @Composable get() = listOf(
        cyan,
        deepOrange,
        purple,
        lime,
        pink,
        indigo,
        teal,
        brown,
        lightBlue,
        orange,
        blueGrey,
        amber,
        grey,
    )

@Composable
fun StayPeriodsCalendar(
    month: Int,
    year: Int,
    modifier: Modifier = Modifier,
    selectedDatesWithMonthYear: Pair<CalendarMonthYear, ImmutableList<StayCalendarDate>>? = null,
    singleStayPeriodColor: Color? = null,
    onViewStay: ((Long) -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    val context = LocalContext.current

    val now = remember { LocalDate.now() }

    val monthYearLabel = remember(month) {
        val monthLabel = context.getMonthLabelByNumber(monthNumber = month)
        val isCurrentYear = year == now.year

        "$monthLabel ${if (!isCurrentYear) year else ""}"
    }
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
            MonthYearLabelSection(monthYearLabel = monthYearLabel)
            Spacer(modifier = Modifier.height(16.dp))
            if (selectedDatesWithMonthYear?.second.isNullOrEmpty()) {
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
                    selectedDatesWithMonthYear = selectedDatesWithMonthYear,
                    now = now,
                    month = month,
                    year = year,
                    singleStayPeriodColor = singleStayPeriodColor,
                    onViewStay = onViewStay,
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
fun MonthYearLabelSection(
    monthYearLabel: String,
    modifier: Modifier = Modifier,
) {
    Text(
        monthYearLabel,
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
    now: LocalDate,
    month: Int,
    year: Int,
    onViewStay: ((Long) -> Unit)?,
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

        items(daysOfMonth) { day ->
            val selectedDaysOfMonth = selectedDatesWithMonthYear?.second?.map { it.dayOfMonth }

            val isToday = day.toIntOrNull() == now.dayOfMonth
                    && month == now.monthValue
                    && year == now.year

            if (selectedDatesWithMonthYear == null || day !in selectedDaysOfMonth!!) {
                UnselectedCalendarDate(
                    day = day,
                    daysOfMonth = daysOfMonth,
                    isToday = isToday,
                    singleStayPeriodColor = singleStayPeriodColor,
                )
            } else {
                SelectedCalendarDate(
                    selectedDate = selectedDatesWithMonthYear.second.find { it.dayOfMonth == day },
                    day = day,
                    daysOfMonth = daysOfMonth,
                    isToday = isToday,
                    singleStayPeriodColor = singleStayPeriodColor,
                    onViewStay = onViewStay,
                )
            }
        }
    }
}

@Composable
fun UnselectedCalendarDate(
    day: String,
    daysOfMonth: ImmutableList<String>,
    isToday: Boolean,
    singleStayPeriodColor: Color?,
    modifier: Modifier = Modifier,
) {
    val paddingBottom = when {
        day in daysOfMonth.takeLast(7) || isToday -> 0.dp
        else -> 24.dp
    }

    CalendarDate(
        dayText = day,
        isToday = isToday,
        singleStayPeriodColor = singleStayPeriodColor,
        modifier = modifier
            .padding(bottom = paddingBottom)
            .offset(
                x = if (day.length == 1) (-0.5).dp else 0.dp,
                y = when {
                    isToday -> (-8).dp
                    else -> 0.dp
                },
            )
    )
}

@Composable
fun SelectedCalendarDate(
    selectedDate: StayCalendarDate?,
    day: String,
    daysOfMonth: ImmutableList<String>,
    isToday: Boolean,
    singleStayPeriodColor: Color?,
    onViewStay: ((Long) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    var isDropdownMenuVisible by remember { mutableStateOf(false) }

    var datePosition by remember { mutableStateOf(Offset.Zero) }
    var dateSize by remember { mutableStateOf(IntSize.Zero) }

    val paddingBottom = when {
        day in daysOfMonth.takeLast(7) || isToday -> 0.dp
        selectedDate != null -> 16.dp
        else -> 24.dp
    }

    CalendarDate(
        dayText = day,
        isToday = isToday,
        selectedDate = selectedDate,
        singleStayPeriodColor = singleStayPeriodColor,
        modifier = modifier
            .padding(bottom = paddingBottom)
            .offset(
                x = if (day.length == 1) (-0.5).dp else 0.dp,
                y = when {
                    isToday -> (-8).dp
                    selectedDate != null -> (-4).dp
                    else -> 0.dp
                },
            )
            .onGloballyPositioned { coords ->
                datePosition = coords.positionInWindow()
                dateSize = coords.size
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    isDropdownMenuVisible = true
                }
            }
    )
    if (onViewStay != null) {
        val menuOffset = with(density) {
            val dateCenterX = datePosition.x + (dateSize.width / 2)

            DpOffset(
                x = (dateCenterX.toDp() - 64.dp) / 2,
                y = 0.dp,
            )
        }

        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f),
        ) {
            AppMoreOptionsDropdownMenu(
                options = persistentListOf(
                    MoreOptionsMenuOption(
                        iconDrawable = R.drawable.calendar_view,
                        text = stringResource(
                            when {
                                selectedDate?.firstHalfPeriodIndex == UNAVAILABLE_PERIOD_INDEX -> {
                                    R.string.view_unvailable_period_action_button_text
                                }

                                else -> R.string.view_property_stay_action_button_text
                            }
                        ),
                        onClick = {
                            if (selectedDate != null) onViewStay(selectedDate.stayId)
                        },
                    ),
                ),
                isMenuExpanded = isDropdownMenuVisible,
                onMenuExpanded = { isDropdownMenuVisible = it },
                offset = menuOffset,
            )
        }
    }
}

@Composable
fun CalendarDate(
    dayText: String,
    singleStayPeriodColor: Color?,
    modifier: Modifier = Modifier,
    isToday: Boolean = false,
    mustHideText: Boolean = false,
    isWeekDayLabel: Boolean = false,
    selectedDate: StayCalendarDate? = null,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (isToday) {
            Surface(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                ),
                color = Color.Transparent,
                shape = CircleShape,
                modifier = Modifier
                    .size(36.dp)
            ) {}
        }
        if (selectedDate != null) {
            val isAllUnavailableDate = selectedDate.firstHalfPeriodIndex == UNAVAILABLE_PERIOD_INDEX
                    && selectedDate.secondHalfPeriodIndex == UNAVAILABLE_PERIOD_INDEX

            Box(
                contentAlignment = Alignment.Center,
            ) {
                if (isAllUnavailableDate) UnavailablePeriodDateCircle(sizeDp = 28.dp)
                else {
                    StayDateCircle(
                        firstHalfPeriodIndex = selectedDate.firstHalfPeriodIndex,
                        secondHalfPeriodIndex = selectedDate.secondHalfPeriodIndex,
                        singleStayPeriodColor = singleStayPeriodColor,
                    )
                }
            }
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
    firstHalfPeriodIndex: Int?,
    secondHalfPeriodIndex: Int?,
    singleStayPeriodColor: Color?,
    modifier: Modifier = Modifier,
) {
    val firstHalfStayPeriodColor = firstHalfPeriodIndex?.let {
        if (it == UNAVAILABLE_PERIOD_INDEX) return@let MaterialTheme.colorScheme.outlineVariant
        singleStayPeriodColor ?: stayPeriodColors[it % stayPeriodColors.size]
    }

    val secondHalfStayPeriodColor = secondHalfPeriodIndex?.let {
        if (it == UNAVAILABLE_PERIOD_INDEX) return@let MaterialTheme.colorScheme.outlineVariant
        singleStayPeriodColor ?: stayPeriodColors[it % stayPeriodColors.size]
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Box(modifier = modifier.size(28.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                when (firstHalfPeriodIndex) {
                    null -> {}
                    UNAVAILABLE_PERIOD_INDEX -> {
                        unavailablePeriodDateSemiCircleFirstHalf(outlineVariantColor = firstHalfStayPeriodColor!!)
                    }

                    else -> stayDateSemiCircleFirstHalf(firstHalfColor = firstHalfStayPeriodColor!!)
                }
                when (secondHalfPeriodIndex) {
                    null -> {}
                    UNAVAILABLE_PERIOD_INDEX -> {
                        unavailablePeriodDateSemiCircleSecondHalf(outlineVariantColor = secondHalfStayPeriodColor!!)
                    }

                    else -> stayDateSemiCircleSecondHalf(secondHalfColor = secondHalfStayPeriodColor!!)
                }
            }
        }
    }
}

fun DrawScope.stayDateSemiCircleFirstHalf(
    firstHalfColor: Color,
) {
    // arc 0° angle starts at 3 o'clock
    drawArc(
        color = firstHalfColor,
        startAngle = 90f,
        sweepAngle = 180f,
        useCenter = true,
        style = Fill,
    )
}

fun DrawScope.stayDateSemiCircleSecondHalf(
    secondHalfColor: Color,
) {
    drawArc(
        color = secondHalfColor,
        startAngle = 270f,
        sweepAngle = 180f,
        useCenter = true,
        style = Fill,
    )
}

@Composable
fun UnavailablePeriodDateCircle(
    sizeDp: Dp,
    modifier: Modifier = Modifier,
) {
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    Box(modifier = modifier.size(sizeDp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val strokeWidth = 6f

            // Outer circle
            drawCircle(
                color = outlineVariantColor,
                center = center,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Diagonal line (from top-right to bottom-left)
            val offset = radius * 0.7f
            drawLine(
                color = outlineVariantColor,
                start = Offset(center.x + offset, center.y - offset),
                end = Offset(center.x - offset, center.y + offset),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

fun DrawScope.unavailablePeriodDateSemiCircleFirstHalf(
    outlineVariantColor: Color,
) {
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val strokeWidth = 6f

    // Outer semicircle
    drawArc(
        color = outlineVariantColor,
        startAngle = 90f,
        sweepAngle = 180f,
        useCenter = true,
        style = Stroke(width = strokeWidth)
    )

    // Diagonal slash
    val offset = radius * 0.7f
    drawLine(
        color = outlineVariantColor,
        start = center,
        end = Offset(center.x - offset, center.y + offset),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
}

fun DrawScope.unavailablePeriodDateSemiCircleSecondHalf(
    outlineVariantColor: Color,
) {
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val strokeWidth = 6f

    // Outer semicircle
    drawArc(
        color = outlineVariantColor,
        startAngle = 270f,
        sweepAngle = 180f,
        useCenter = true,
        style = Stroke(width = strokeWidth)
    )

    // Diagonal slash
    val offset = radius * 0.7f
    drawLine(
        color = outlineVariantColor,
        start = Offset(center.x + offset, center.y - offset),
        end = center,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
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
            selectedDatesWithMonthYear = Pair(
                CalendarMonthYear(-1, 1, 2025),
                persistentListOf(
                    StayCalendarDate(
                        dayOfMonth = "4",
                        firstHalfPeriodIndex = null,
                        secondHalfPeriodIndex = -1,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "5",
                        firstHalfPeriodIndex = -1,
                        secondHalfPeriodIndex = -1,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "6",
                        firstHalfPeriodIndex = -1,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "7",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "8",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "9",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "10",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "11",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "12",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "13",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "14",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "15",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "16",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "17",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "18",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "19",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "20",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "21",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "22",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "23",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "24",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "25",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "26",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "27",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "28",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "29",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = 0,
                        stayId = 0,
                    ),
                    StayCalendarDate(
                        dayOfMonth = "30",
                        firstHalfPeriodIndex = 0,
                        secondHalfPeriodIndex = null,
                        stayId = 0,
                    ),
                ),
            ),
            singleStayPeriodColor = MaterialTheme.colorScheme.secondaryContainer,
            modifier = modifier,
        )
    }
}
