package com.example.androiddevchallenge

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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


private var square = mutableStateOf(125f)
private var counterType = mutableStateOf(CounterType.MinuteSecond)
private var timerTimeLarge = mutableStateOf(0f)
private var timerTimeSmall = mutableStateOf(0f)

@Composable
fun Timer(
    timeLarge: MutableState<Float>,
    timeSmall: MutableState<Float>,
    onChangeLarge: (Float) -> Unit,
    onChangeSmall: (Float) -> Unit,
    onPlayPauseClicked: (isNotPlaying: Boolean, timeLargeValue: Float, timeSmallValue: Float, timeInterval: Long) -> Unit
) {

    timerTimeLarge = timeLarge
    timerTimeSmall = timeSmall

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

        val color1 = colorResource(id = R.color.purple_200)
        val color2 = colorResource(id = R.color.purple_500)
        val color3 = colorResource(id = R.color.purple_700)

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(), onDraw = {

            drawLayers(
                this,
                timeLarge.value,
                timeSmall.value,
                arrayOf(color1, color2, color3)
            )

        })


        CustomTimeSelector(onChangeLarge, onChangeSmall)

        StartButton(onPlayPauseClicked)

        CounterTypeChooser(onPlayPauseClicked)

    }


}

private fun onTypeClicked(type: CounterType) {
    val enabledColor = R.color.purple_500
    val disabledColor = android.R.color.transparent
    counterType.value = type

    when (type) {
        CounterType.HourMinute -> {
            enabledColorHours.value = enabledColor
            enabledColorMinutes.value = disabledColor
            enabledColorSeconds.value = disabledColor
            square.value = 100f
        }
        CounterType.MinuteSecond -> {
            enabledColorHours.value = disabledColor
            enabledColorMinutes.value = enabledColor
            enabledColorSeconds.value = disabledColor
            square.value = 125f
        }
        CounterType.SecondMillisecond -> {
            enabledColorHours.value = disabledColor
            enabledColorMinutes.value = disabledColor
            enabledColorSeconds.value = enabledColor
            square.value = 150f
        }
    }
}


private fun drawLayers(
    drawScope: DrawScope,
    timeMinutes: Float,
    timeSeconds: Float,
    colors: Array<Color>
) {
    var colorIndex: Int

    val minutes = (timeMinutes / 60) - 1
    val normalValue = timeSeconds / 60


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


    }

}

private var secondsCnt = 0f

@Composable
private fun CustomTimeSelector(
    onChangeLarge: (Float) -> Unit,
    onChangeSmall: (Float) -> Unit
) {

    val fling = object : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {

            if (isNotPlaying) {
                if (initialVelocity < 0) {
                    onChangeLarge(-60f)
                } else {
                    if (timerTimeLarge.value.toInt() <= -1)
                        onChangeLarge(60f)
                }
            }
            return 0f
        }
    }

    val minutesBehavior = Modifier.verticalScroll(
        ScrollState(0), flingBehavior = fling

    )

    val secondsBehavior = Modifier.scrollable(ScrollableState {
        if (isNotPlaying) {
            if (it < 0) {
                secondsCnt += 1f
                if (secondsCnt >= 60f) {
                    secondsCnt = 1f
                    onChangeLarge(-2f)
                } else {
                    onChangeLarge(-1f)
                }
                onChangeSmall(secondsCnt)


            } else {
                secondsCnt -= 1f

                if (timerTimeLarge.value.toInt() >= 0 && secondsCnt.toInt() <= 0) {
                    secondsCnt = 0f
                    onChangeSmall(0f)
                    onChangeLarge(0f)
                    return@ScrollableState 0f
                } else {

                    if (secondsCnt <= 0) {
                        secondsCnt = 59f
                        onChangeLarge(2f)
                    } else {
                        onChangeLarge(1f)
                    }
                    onChangeSmall(secondsCnt)
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
            text = (abs(timerTimeLarge.value) / 60).toInt().toDigitalFormat(),
            fontSize = 50.sp,
            modifier = minutesBehavior, color = Color.White
        )
        Text(text = ":", fontSize = 50.sp, color = Color.White)
        Text(
            text = (timerTimeSmall.value).toInt().toDigitalFormat(),
            fontSize = 50.sp,
            modifier = secondsBehavior, color = Color.White
        )


    }

}

private val playPauseState = mutableStateOf(R.drawable.ic_baseline_play_arrow_24)
private var isNotPlaying = true

@Composable
private fun StartButton(onPlayPauseClicked: (Boolean, Float, Float, Long) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .alpha(0.8f)
            .padding(16.dp), contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = playPauseState.value),
            contentDescription = "play",
            modifier = Modifier
                .background(Color.Black, shape = CircleShape)
                .clip(CircleShape)

                .toggleable(value = isNotPlaying, onValueChange = {
                    if (!it) {
                        play(onPlayPauseClicked)
                    } else {
                        pause(onPlayPauseClicked)
                    }
                })
                .padding(10.dp)


        )


    }

}

// Counter type chooser for h:m

private val enabledColorHours = mutableStateOf(android.R.color.transparent)
private val enabledColorMinutes = mutableStateOf(R.color.purple_500)
private val enabledColorSeconds = mutableStateOf(android.R.color.transparent)

@Composable
private fun CounterTypeChooser(
    onStartClick: (Boolean, Float, Float, Long) -> Unit
) {
    val color1 = colorResource(id = enabledColorHours.value)
    val color2 = colorResource(id = enabledColorMinutes.value)
    val color3 = colorResource(id = enabledColorSeconds.value)
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
                }
                .padding(vertical = 16.dp), textAlign = TextAlign.Center, color = Color.White
            )
            Text(text = "m:s", Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(color2, color2)))
                .toggleable(true, role = Role.RadioButton) {
                    onTypeClicked(CounterType.MinuteSecond)
                    pause(onStartClick)
                }
                .padding(vertical = 16.dp), textAlign = TextAlign.Center, color = Color.White)

            Text(text = "s:ms", Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomEndPercent = 50, bottomStartPercent = 50))
                .background(Brush.horizontalGradient(listOf(color3, color3)))
                .toggleable(true, role = Role.Checkbox) {
                    onTypeClicked(CounterType.SecondMillisecond)
                    pause(onStartClick)
                }
                .padding(vertical = 16.dp), textAlign = TextAlign.Center, color = Color.White)
        }
    }

}


// controlling play, pause and reset

private fun pause(onPlayPauseClicked: (Boolean, Float, Float, Long) -> Unit) {
    if (!isNotPlaying) {
        onPlayPauseClicked(true, 0f, 0f, 0L)
        isNotPlaying = true
        playPauseState.value = R.drawable.ic_baseline_play_arrow_24
    }
}

private fun play(onPlayPauseClicked: (Boolean, Float, Float, Long) -> Unit) {
    if (isNotPlaying) {
        onPlayPauseClicked(
            false,
            timerTimeLarge.value,
            timerTimeSmall.value,
            counterType.value.getInterval()
        )
        playPauseState.value = R.drawable.ic_baseline_pause_24
        isNotPlaying = false
    }
}

fun reset() {
    timerTimeLarge.value = 0.0f
    timerTimeSmall.value = 0.0f
    secondsCnt = 0f
    isNotPlaying = true
    playPauseState.value = R.drawable.ic_baseline_play_arrow_24
}
