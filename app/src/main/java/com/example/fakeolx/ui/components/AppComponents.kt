package com.example.fakeolx.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun FakeOLXLogo(
    modifier: Modifier = Modifier,
    tagline: String? = "ogłoszenia po swojemu",
    centered: Boolean = false
) {
    val alignment = if (centered) Alignment.CenterHorizontally else Alignment.Start
    val textAlign = if (centered) TextAlign.Center else TextAlign.Start
    Column(modifier = modifier, horizontalAlignment = alignment) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append("fake")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("OLX")
                }
            },
            style = MaterialTheme.typography.displayLarge,
            textAlign = textAlign
        )
        if (tagline != null) {
            Text(
                text = tagline,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                textAlign = textAlign
            )
        }
    }
}

@Composable
fun PricePill(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun formatPrice(amount: Double): String {
    val formatted = if (amount % 1.0 == 0.0) {
        amount.toInt().toString()
    } else {
        String.format(Locale("pl", "PL"), "%.2f", amount)
    }
    return "$formatted zł"
}
