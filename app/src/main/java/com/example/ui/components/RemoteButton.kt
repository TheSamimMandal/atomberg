package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PowerRed
import com.example.ui.theme.PowerRedPressed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Red circular Power Button positioned at the upper right of the remote.
 */
@Composable
fun PowerButton(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "power_scale"
    )

    val buttonBg = when {
        isPressed -> Brush.radialGradient(listOf(PowerRedPressed, Color(0xFF991B1B)))
        isActive -> Brush.radialGradient(listOf(Color(0xFFFF5252), PowerRed, Color(0xFFB71C1C)))
        else -> Brush.radialGradient(listOf(Color(0xFFFF453A), PowerRed, Color(0xFF991B1B)))
    }

    Box(
        modifier = modifier
            .size(52.dp)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 2.dp else 6.dp,
                shape = CircleShape,
                ambientColor = PowerRedPressed,
                spotColor = PowerRed
            )
            .clip(CircleShape)
            .background(buttonBg)
            .border(
                width = 1.2.dp,
                color = if (isActive) Color(0xFFFFCDD2) else Color(0x66FFFFFF),
                shape = CircleShape
            )
            .semantics {
                contentDescription = "Power"
                role = Role.Button
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("power_button"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PowerSettingsNew,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}

/**
 * Bottom tactical button (TIMER, LED, SLEEP) containing an icon with label underneath.
 */
@Composable
fun BottomControlButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    testTag: String,
    contentDescriptionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "bottom_button_scale_$label"
    )

    val buttonBrush = when {
        isActive -> Brush.radialGradient(listOf(Color(0xFF3A3E4D), Color(0xFF282B36)))
        isPressed -> Brush.radialGradient(listOf(Color(0xFF191A20), Color(0xFF121317)))
        else -> Brush.radialGradient(listOf(Color(0xFF2B2E38), Color(0xFF1F2128)))
    }

    Column(
        modifier = modifier
            .semantics {
                contentDescription = contentDescriptionText
                role = Role.Button
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .scale(scale)
                .shadow(
                    elevation = if (isPressed) 1.dp else 4.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(CircleShape)
                .background(buttonBrush)
                .border(
                    width = if (isActive) 1.5.dp else 1.dp,
                    color = if (isActive) Color(0x99FFFFFF) else Color(0x33FFFFFF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) Color.White else TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = label,
            color = if (isActive) Color.White else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}
