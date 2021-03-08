/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme() {
                MyApp()


            }
        }
    }
}

private val timeSeconds: MutableState<Float> = mutableStateOf(0.0f)
private val timeMillis: MutableState<Float> = mutableStateOf(0.0f)
private var l1 = 10 * 1000L
private var value: CountDownTimer? = null

fun getCountDownTimer(time: Long, interval: Long): CountDownTimer {
    return object : CountDownTimer(time, interval) {
        override fun onTick(p0: Long) {
            seconds -= 1f

            if (timeSeconds.value.toInt() >= -1 && timeMillis.value.toInt() <= 1) {
                this.onFinish()
                this.cancel()
                return
            }

            if (seconds <= 0) {
                seconds = 59f
                timeSeconds.value += (2f)
            } else {
                timeSeconds.value += (1f)
            }
            timeMillis.value = seconds

        }

        override fun onFinish() {
            reset()
        }
    }

}

private var seconds = 1f

// Start building your app here!
@Composable
fun MyApp() {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {


        Timer(timeLarge = timeSeconds,
            timeSmall = timeMillis,
            onChangeLarge = { timeSeconds.value += it },
            onChangeSmall = { timeMillis.value = it },
            onPlayPauseClicked = { isNotPlaying, time, secondsRemainder, interval ->

                seconds = secondsRemainder
                l1 = abs(time.toLong()) * 1000L

                if (!isNotPlaying && l1 != 0L) {
                    value = getCountDownTimer(l1, interval)
                    value?.start()
                } else {
                    value?.cancel()
                }
            })

        ThemeChanger()



    }

}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme() {
        MyApp()
    }
}

@Composable
fun DarkPreview() {
    MyTheme() {
        MyApp()
    }
}
