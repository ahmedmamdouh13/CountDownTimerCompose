package com.example.androiddevchallenge

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs


var isLocked: Boolean = true
const val margin: Float = 16.0f
var square = mutableStateOf(150f)
var counterType = mutableStateOf(CounterType.MinuteSecond)


@Composable
fun Timer(
    timeMinutes: Float,
    timeSeconds: Float,
    onChangeMinutes: (Float) -> Unit,
    onChangeSeconds: (Float) -> Unit,
    onStartClick: (Boolean, Float, Float, Long) -> Unit
) {


    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

        val color1 = colorResource(id = R.color.purple_200)
        val color2 = colorResource(id = R.color.purple_500)
        val color3 = colorResource(id = R.color.purple_700)

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(), onDraw = {


            drawLayer(this, timeMinutes, timeSeconds, arrayOf(color1, color2, color3))


//            drawCircle(Color.Magenta, radius = size.minDimension / 3f)


//            translate(left, top) {
//
//                drawLine(
//                    Color.Black,
//                    start = Offset.Zero,
//                    end = Offset(
//                        (this.size.width - margin) * sin(normalValue),
//                        (this.size.height - margin) * cos(normalValue)
//                    ),
//                    strokeWidth = 10f,
//                    pathEffect = PathEffect.cornerPathEffect(5f),
//
//                    cap = StrokeCap.Round
//                )
//
//            }

        })


        CustomTimeSelector(timeMinutes, timeSeconds, onChangeMinutes, onChangeSeconds)

        StartButton(onStartClick, timeMinutes, timeSeconds)

        CounterTypeChooser(onTypeClicked = onTypeClicked, onStartClick)

    }


}

val onTypeClicked: (CounterType) -> Unit = {

    counterType.value = it

    when(it){
        CounterType.HourMinute -> {
            square.value = 100f
        }
        CounterType.MinuteSecond -> {
            square.value = 125f
        }
        CounterType.SecondMillisecond -> {
            square.value = 150f
        }
    }
}

//val colorIndexMemory = arrayListOf<Int>()

fun drawLayer(drawScope: DrawScope, timeMinutes: Float, timeSeconds: Float, colors: Array<Color>) {
    var colorIndex: Int

    val minutes = (timeMinutes / 60) - 1
    val normalValue = timeSeconds / 60
//    for (t in minutes.toInt() .. 0) {
//        colorIndexMemory.add()
//    }


    val widthProg = (square.value * (abs(minutes) - 1f)) + (normalValue)
    val heightProg = (square.value * (abs(minutes) - 1f)) + (normalValue)

    val leftProg = widthProg / 2f
    val topProg = heightProg / 2f
    colorIndex = when {
        (minutes.toInt() - 1) % 3 == 0 -> 0
        (minutes.toInt() - 1) % 2 == 0 -> 1
        else -> 2
    }


    drawScope.drawArc(
        colors[colorIndex],
        0f,
        360 * normalValue,
        true,
        size = Size(widthProg, heightProg),
        topLeft = Offset(
            (drawScope.size.width / 2) - leftProg,
            (drawScope.size.height / 2) - topProg
        )
    )


    for (t in minutes.toInt() until 0) {

//        drawScope.drawArc(color1, 0f, 360f * normalValue, true)
        val fl = 1f + t

        val width = (square.value * fl)
        val height = (square.value * fl)

        val left = width / 2f
        val top = height / 2f


        colorIndex = when {
            t % 3 == 0 -> 0
            t % 2 == 0 -> 1
            else -> 2
        }


        if (t > minutes - 1)
            drawScope.drawArc(
                colors[colorIndex],
                0f,
                360f,
                true,
                size = Size(width, height),
                topLeft = Offset(
                    (drawScope.size.width / 2) - left,
                    (drawScope.size.height / 2) - top
                )
            )


//        drawScope.drawArc(
//            color3,
//            0f,
//            360f * normalValue,
//            true,
//            size = Size(drawScope.size.width / 3f, drawScope.size.height / 3f),
//            topLeft = Offset(drawScope.size.width / 3f, drawScope.size.height / 3f)
//        )
//
    }

}

var secondsCnt = 0f

@Composable
fun CustomTimeSelector(
    timeMinutes: Float,
    timeSeconds: Float,
    onChangeMinutes: (Float) -> Unit,
    onChangeSeconds: (Float) -> Unit
) {

    val fling = object : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {

            if (isNotPlaying) {
                if (initialVelocity < 0) {
                    onChangeMinutes(-60f)
                } else {
                    if (timeMinutes.toInt() <= -1)
                        onChangeMinutes(60f)
                }
            }
            return 0f
        }
    }

    val minutesBehavior = Modifier.verticalScroll(
        ScrollState(0), flingBehavior = fling

    )
//
//    val minutesBehavior = Modifier.scrollable(ScrollableState {
//
//        if (it < 0) {
//            onChangeMinutes(-60f)
//        } else {
//            onChangeMinutes(60f)
//        }
//
//        0f
//    }, Orientation.Vertical)


    val secondsBehavior = Modifier.scrollable(ScrollableState {
        if (isNotPlaying) {
            if (it < 0) {
                secondsCnt += 1f
                if (secondsCnt >= 60f) {
                    secondsCnt = 1f
                    onChangeMinutes(-2f)
                } else {
                    onChangeMinutes(-1f)
                }
                onChangeSeconds(secondsCnt)


            } else  {
                secondsCnt -= 1f

                if (timeMinutes.toInt() >= 0 && secondsCnt.toInt() <= 0) {
                    secondsCnt = 0f
                    onChangeSeconds(0f)
                    onChangeMinutes(0f)
                    return@ScrollableState 0f
                } else {

                    if (secondsCnt <= 0) {
                        secondsCnt = 59f
                        onChangeMinutes(2f)
                    } else {
                        onChangeMinutes(1f)
                    }
                    onChangeSeconds(secondsCnt)
                }
            }
        }
        0f
    }, Orientation.Vertical)


    Row(
        modifier = Modifier
            .background(
                Brush.horizontalGradient(listOf(Color.Black, Color.Black)),
                shape = CircleShape,
                0.5f
            )
            .padding(5.dp)
            .width(150.dp)
            .height(150.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {


        Text(
            text = (abs(timeMinutes) / 60).toInt().toDigitalFormat(),
            fontSize = 50.sp,
            modifier = minutesBehavior
            ,color = Color.White
        )
        Text(text = ":", fontSize = 50.sp,color = Color.White)
        Text(
            text = (timeSeconds).toInt().toDigitalFormat(),
            fontSize = 50.sp,
            modifier = secondsBehavior
            ,color = Color.White)


    }

}

var playPauseState = mutableStateOf(R.drawable.ic_baseline_play_arrow_24)
var isNotPlaying = true

@Composable
fun StartButton(onClick: (Boolean, Float, Float, Long) -> Unit, timeMinutes: Float, timeSeconds: Float) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .alpha(0.8f)
            .padding(16.dp)
        , contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = playPauseState.value),
            contentDescription = "play",
            modifier = Modifier
                .background(Color.Black, shape = CircleShape)
                .clip(CircleShape)

                .toggleable(value = isNotPlaying, onValueChange = {
                    onClick(it, timeMinutes, timeSeconds, counterType.value.getInterval())
                    if (!it) {
                        playPauseState.value = R.drawable.ic_baseline_pause_24
                        isNotPlaying = false
                    } else {
                        playPauseState.value = R.drawable.ic_baseline_play_arrow_24
                        isNotPlaying = true

                    }
                })
                .padding(10.dp)


        )


    }

}




val enabledColorHours = mutableStateOf(android.R.color.transparent)
val enabledColorMinutes = mutableStateOf(R.color.purple_500)
val enabledColorSeconds = mutableStateOf(android.R.color.transparent)

@Composable
fun CounterTypeChooser(
    onTypeClicked: (CounterType) -> Unit,
    onStartClick: (Boolean, Float, Float, Long) -> Unit
) {
    val color1 = colorResource(id = enabledColorHours.value)
    val color2 = colorResource(id = enabledColorMinutes.value)
    val color3 = colorResource(id = enabledColorSeconds.value)
    val enabledColor = R.color.purple_500
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
            .alpha(0.8f), contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            modifier = Modifier
                .width(50.dp)
                .background(
                    Brush.horizontalGradient(listOf(Color.Black, Color.Black)),
                    shape = RoundedCornerShape(50),
                    0.8f
                ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "h:m", Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topEndPercent = 50, topStartPercent = 50))
                .background(Brush.horizontalGradient(listOf(color1, color1)))
                .toggleable(true, role = Role.RadioButton) {
                    onTypeClicked(CounterType.HourMinute)

                    pause(onStartClick)

                    enabledColorHours.value = enabledColor
                    enabledColorMinutes.value = android.R.color.transparent

                    enabledColorSeconds.value = android.R.color.transparent
                }
                .padding(vertical = 16.dp), textAlign = TextAlign.Center
                ,color = Color.White
            )
            Text(text = "m:s", Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(color2, color2)))
                .toggleable(true, role = Role.RadioButton) {
                    onTypeClicked(CounterType.MinuteSecond)
                    pause(onStartClick)

                    enabledColorHours.value = android.R.color.transparent
                    enabledColorMinutes.value = enabledColor

                    enabledColorSeconds.value = android.R.color.transparent
                }
                .padding(vertical = 16.dp), textAlign = TextAlign.Center,color = Color.White)

            Text(text = "s:ms", Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomEndPercent = 50, bottomStartPercent = 50))
                .background(Brush.horizontalGradient(listOf(color3, color3)))
                .toggleable(true, role = Role.Checkbox) {
                    onTypeClicked(CounterType.SecondMillisecond)
                    pause(onStartClick)


                    enabledColorHours.value = android.R.color.transparent
                    enabledColorMinutes.value = android.R.color.transparent

                    enabledColorSeconds.value = enabledColor

                }
                .padding(vertical = 16.dp), textAlign = TextAlign.Center
            ,color = Color.White)
        }
    }

}

fun pause(onStartClick: (Boolean, Float, Float, Long) -> Unit) {
    if (!isNotPlaying) {
        onStartClick(true, 0f, 0f, 0L)
        isNotPlaying = true
        playPauseState.value = R.drawable.ic_baseline_play_arrow_24
    }

}


fun reset(){

    timeMillis.value = 0.0f
    timeSeconds.value = 0.0f
    secondsCnt = 0f
    isNotPlaying = true
    playPauseState.value = R.drawable.ic_baseline_play_arrow_24

}
