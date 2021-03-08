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
package com.example.androiddevchallenge.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

val DarkColorPalette = darkColors(

)

val LightColorPalette = lightColors(
    primary = purple500,
    primaryVariant = Color.White,
    secondary = teal200,

    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)
val colors = mutableStateOf(DarkColorPalette)

@Composable
fun MyTheme(content: @Composable() () -> Unit) {

    MaterialTheme(
        colors = colors.value,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

private val themeColorText = mutableStateOf("Light")
private val isChangeTheme = mutableStateOf(true)


@Composable
fun ThemeChanger() {

    Box(modifier = Modifier.fillMaxWidth()
        .fillMaxHeight().padding(10.dp)
        ,contentAlignment = Alignment.TopEnd

    ){
        Text(
            modifier = Modifier

                .background(
                    Brush.horizontalGradient(listOf(Color.Black, Color.Black)),
                    shape = CircleShape,
                    0.5f
                )
                .clip(CircleShape)
                .toggleable(isChangeTheme.value, onValueChange = {
                    if (!it){
                        themeColorText.value = "Dark"
                        isChangeTheme.value = false
                        colors.value = LightColorPalette
                    }
                    else{
                        themeColorText.value = "Light"
                        isChangeTheme.value = true
                        colors.value = DarkColorPalette
                    }
                }

                ).padding(8.dp)

            ,text = themeColorText.value
            ,textAlign = TextAlign.Center
        )

    }

}
