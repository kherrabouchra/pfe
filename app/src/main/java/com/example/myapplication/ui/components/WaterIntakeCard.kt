package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A component that displays water intake with a semi-circular progress indicator
 * with numbered cup indicators and buttons to increment/decrement the amount.
 */
@Composable
fun WaterIntakeCard(
    modifier: Modifier = Modifier,
    currentIntake: Int = 0,
    targetIntake: Int = 2500,
    onIntakeChange: (Int) -> Unit = {}
) {
    val waterColor = MaterialTheme.colorScheme.primary // Light blue color for water
    val progress = currentIntake.toFloat() / targetIntake.toFloat()
    val cupCount = 8 // Number of cup indicators to display around the arc
    
    Card(
        modifier = modifier
            .fillMaxWidth() ,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.3.dp, Color.LightGray)
    ) {  Spacer(modifier = Modifier.height(8.dp))
        Column (
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {

            Row (verticalAlignment = Alignment.CenterVertically){

                Icon(
                    painter = painterResource(id = R.drawable.ic_water),
                    contentDescription = "Water Cup",
                    modifier = Modifier.size(24.dp),
                 )
                Text(
                    text = "Water Intake",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,

                    )
            }

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                // Progress arc
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp).padding(horizontal=12.dp)
                ) {
                    // Background arc
                    drawArc(
                        color = Color(0xFFE0E0E0),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = Stroke(
                            width = 24.dp.toPx(),
                            cap = StrokeCap.Round
                        ),
                        size = Size(size.width, size.height * 2f),
                        topLeft = Offset(0f, 0f)
                    )
                    
                    // Progress arc
                    drawArc(
                        color = waterColor,
                        startAngle = 180f,
                        sweepAngle = 180f * progress,
                        useCenter = false,
                        style = Stroke(
                            width = 24.dp.toPx(),
                            cap = StrokeCap.Round
                        ),
                        size = Size(size.width, size.height * 2f),
                        topLeft = Offset(0f, 0f)
                    )
                    
                    // Draw numbered cup indicators around the arc
                    val radius = (size.width / 2f)
                    val centerX = size.width / 2f
                    val centerY = size.height
                    
                    // We're removing the circle indicators as requested
                    // The numbers will be drawn as text overlays instead
                }
                

                // Water cup icon in the center
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(22.dp))
                    // Title and water intake value moved to bottom
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Water intake value
                    Text(
                        text = "${currentIntake}ml",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )


                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            

            

            
            // Increment/Decrement buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrement button
                IconButton(
                    onClick = {
                        val newIntake = (currentIntake - 250).coerceAtLeast(0)
                        onIntakeChange(newIntake)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = Color.Gray
                    )
                }
                
                // Water glass icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(waterColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_water),
                        contentDescription = "Water Cup",
                        modifier = Modifier.size(24.dp),
                        tint = waterColor
                    )
                }
                
                // Increment button
                IconButton(
                    onClick = {
                        val newIntake = currentIntake + 250
                        onIntakeChange(newIntake)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WaterIntakeCardPreview() {
    var waterIntake by remember { mutableIntStateOf(0) }
    
    WaterIntakeCard(
        currentIntake = waterIntake,
        targetIntake = 2500,
        onIntakeChange = { waterIntake = it }
    )
}