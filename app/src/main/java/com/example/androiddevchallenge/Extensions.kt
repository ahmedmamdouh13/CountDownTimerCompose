package com.example.androiddevchallenge


fun Int.toDigitalFormat(): String {
    return if (this > 9)
        this.toString()
    else
        "0$this"
}


fun CounterType.getInterval(): Long {
   return when(this){
        CounterType.HourMinute -> Constants.hourInMillis
        CounterType.MinuteSecond -> Constants.minuteInMillis
        CounterType.SecondMillisecond -> Constants.secondInMillis
    }
}