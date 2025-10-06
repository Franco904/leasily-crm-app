package com.fstengineering.leasilycrm.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fstengineering.leasilycrm.core.application.theme.AppTheme
import com.fstengineering.leasilycrm.home.models.BottomNavBarDestination

@Composable
fun HomeBottomNavigationBar(
    currentDestination: BottomNavBarDestination,
    onDestinationSelect: (BottomNavBarDestination) -> Unit,
    upcomingStaysCount: Int,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        windowInsets = WindowInsets(0),
        modifier = modifier
    ) {
        for (navItem in BottomNavBarDestination.entries) {
            val isSelected = navItem == currentDestination

            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelect(navItem) },
                icon = {
                    Box {
                        when {
                            navItem.icon != null && navItem.selectedIcon != null -> {
                                Icon(
                                    imageVector = when {
                                        isSelected -> navItem.selectedIcon
                                        else -> navItem.icon
                                    },
                                    contentDescription = stringResource(navItem.textId),
                                    tint = when {
                                        isSelected -> MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }

                            navItem.iconDrawable != null && navItem.selectedIconDrawable != null -> {
                                Icon(
                                    painter = when {
                                        isSelected -> painterResource(navItem.selectedIconDrawable)
                                        else -> painterResource(navItem.iconDrawable)
                                    },
                                    contentDescription = stringResource(navItem.textId),
                                    tint = when {
                                        isSelected -> MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                        if (navItem == BottomNavBarDestination.Stays && upcomingStaysCount > 0) {
                            NumberRedBadge(
                                number = upcomingStaysCount,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = stringResource(navItem.textId),
                        style = when {
                            isSelected -> MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )

                            else -> MaterialTheme.typography.labelMedium
                        },
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            )
        }
    }
}

@Composable
fun NumberRedBadge(
    number: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error)
        )
        Text(
            text = if (number > 9) "9+" else number.toString(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onError,
            modifier = Modifier
                .fillMaxHeight()
        )
    }
}

@Preview
@Composable
private fun HomeBottomNavigationBarPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        HomeBottomNavigationBar(
            currentDestination = BottomNavBarDestination.Dashboard,
            onDestinationSelect = {},
            upcomingStaysCount = 17,
            modifier = modifier
        )
    }
}
