package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SelectButton(
    text: String,
    description: String? = null,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    val primaryBlue =  MaterialTheme.colorScheme.primary
    val selectedBg = MaterialTheme.colorScheme.primary.copy(alpha=0.1f)
    val hoverBg = Color(0xFFF5F7FF)
    val shape = RoundedCornerShape(12.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape) // optional — you can remove this if shape is already passed below
            .clickable(onClick = onSelect),
        shape = shape, // <- This is the key part
        color = if (isSelected) selectedBg else MaterialTheme.colorScheme.surface,
        border = if (isSelected) BorderStroke(1.dp, primaryBlue) else null,
        tonalElevation = if (isSelected) 0.dp else 1.dp,
        shadowElevation = if (isSelected) 0.dp else 1.dp
    ){
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {



            Column (    modifier = Modifier.fillMaxWidth()){

                Row(  horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()){
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) primaryBlue else MaterialTheme.colorScheme.onSurface
                    )

                        Icon(
                            imageVector = if (isSelected)  Icons.Default.CheckCircle else Icons.Default.AddCircleOutline,
                            contentDescription = "Selected",
                            tint = primaryBlue,
                            modifier = Modifier.size(24.dp)
                        )

                }

                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }


        }
    }
}

@Preview
@Composable
fun SelectButtonPreview(){

    SelectButton(
        text = "Select",
        description = "Description",
        isSelected = true,
        onSelect = {},

        )
}