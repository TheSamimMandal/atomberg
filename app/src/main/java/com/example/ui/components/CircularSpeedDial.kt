package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ActiveSpeedAccent
import com.example.ui.theme.BoostButtonActive
import com.example.ui.theme.BoostButtonSurface
import com.example.ui.theme.DialBorder
import com.example.ui.theme.DialSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Large circular control section reproducing the physical Atomberg remote.
 *
 * Speeds 1 to 5 are arranged radially around the outer perimeter:
 * - Top (12 o'clock): 1
 * - Upper-right: 2
 * - Lower-right: 3
 * - Lower-left: 4
 * - Upper-left: 5
 *
 * Center concentric button: B (BOOST)
 */
@Composable
fun CircularSpeedDial(
    selectedSpeed: Int?,
    isBoostActive: Boolean,
    onSpeedClick: (Int) -> Unit,
    onBoostClick: () -> Unit,
    modifier: Modifier = Modifier,
    dialDiameter: Dp = 230.dp
) {
    Box(
        modifier = modifier
            .size(dialDiameter)
            .shadow(elevation = 8.dp, shape = CircleShape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF282A33),
                        DialSurface,
                        Color(0xFF141518)
                    )
                )
            )
            .border(width = 1.5.dp, color = DialBorder, shape = CircleShape)
            .drawBehind {
                // Subtle concentric groove dividing inner boost area and outer ring
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val innerRadius = size.width * 0.28f
                drawCircle(
                    color = Color(0x33000000),
                    radius = innerRadius + 1f,
                    center = centerOffset
                )
                drawCircle(
                    color = Color(0x33FFFFFF),
                    radius = innerRadius,
                    center = centerOffset,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val centerPx = maxWidth.value / 2f
            // Distance from dial center to speed button center (~68% of radius)
            val orbitRadiusDp = (maxWidth * 0.335f).value

            // 5 speed buttons evenly spaced by 72 degrees starting from top (-90 degrees)
            val speeds = listOf(
                1 to -90.0,
                2 to -18.0,
                3 to 54.0,
                4 to 126.0,
                5 to 198.0
            )

            speeds.forEach { (speedNum, angleDeg) ->
                val angleRad = Math.toRadians(angleDeg)
                val xOffsetDp = (orbitRadiusDp * cos(angleRad)).roundToInt()
                val yOffsetDp = (orbitRadiusDp * sin(angleRad)).roundToInt()

                SpeedDialButton(
                    speedNumber = speedNum,
                    isSelected = selectedSpeed == speedNum,
                    onClick = { onSpeedClick(speedNum) },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset { IntOffset(xOffsetDp.dp.roundToPx(), yOffsetDp.dp.roundToPx()) }
                )
            }

            // Center BOOST "B" Button
            BoostCenterButton(
                isActive = isBoostActive,
                onClick = onBoostClick,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun SpeedDialButton(
    speedNumber: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "speed_scale_$speedNumber"
    )

    val textColor = when {
        isSelected -> ActiveSpeedAccent
        isPressed -> TextPrimary
        else -> TextSecondary
    }

    Box(
        modifier = modifier
            .size(52.dp)
            .scale(scale)
            .clip(CircleShape)
            .semantics {
                contentDescription = "Fan Speed $speedNumber"
                role = Role.Button
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("speed_${speedNumber}_button"),
        contentAlignment = Alignment.Center
    ) {
        // Active or pressed background ring highlight
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
                    .border(width = 1.dp, color = Color(0x66FFFFFF), shape = CircleShape)
            )
        } else if (isPressed) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF))
            )
        }

        Text(
            text = speedNumber.toString(),
            color = textColor,
            fontSize = 20.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun BoostCenterButton(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "boost_scale"
    )

    val buttonBg = when {
        isActive -> Brush.radialGradient(listOf(Color(0xFF3E4352), BoostButtonActive))
        isPressed -> Brush.radialGradient(listOf(Color(0xFF1D1F26), Color(0xFF16171C)))
        else -> Brush.radialGradient(listOf(Color(0xFF2E313D), BoostButtonSurface))
    }

    Box(
        modifier = modifier
            .size(76.dp)
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 2.dp else 5.dp,
                shape = CircleShape,
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(CircleShape)
            .background(buttonBg)
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = if (isActive) Color(0x99FFFFFF) else Color(0x33FFFFFF),
                shape = CircleShape
            )
            .semantics {
                contentDescription = "Boost"
                role = Role.Button
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("boost_button"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "B",
            color = if (isActive) Color.White else TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
