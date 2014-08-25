//
// All this code is provided
//

import cmpsci220._
import cmpsci220.graphics._

case class Time(hours : Int, minutes : Int, seconds : Int)

val ex1 = Time(0, 1, 0)

val ex2 = Time(7, 23, 19)
val ex3 = Time(23, 50, 0) // We are using military time

val ex5 = Time(0, -1, 0)
val ex6 = Time(7, 0, 200)

//
// Student written functions
//

// The parentheses are *necessary*. Otherwise, Scala parses each line as an
// independent expression. Fucking stupid.
def isValidTime(time : Time) : Boolean =
  (time.hours >= 0 && time.hours <= 23 &&
   time.minutes >= 0 && time.minutes <= 59 &&
   time.seconds >= 0 && time.seconds <= 59)

def tick(time : Time) : Time = time match {
  case Time(23, 59, 59) => Time(0, 0, 0)
  case Time(h, 59, 59)  => Time(h + 1, 0, 0)
  case Time(h, m, 59)   => Time(h, m + 1, 0)
  case Time(h, m, s)    => Time(h, m, s + 1)
}

//
// These tests are provided
//

test("8:30:01 is after 8:30:00") {
  assert(tick(Time(8, 30, 0)) == Time(8, 30, 1))
}

test("8:30:00 is after 8:29:59") {
  assert(tick(Time(8, 29, 59)) == Time(8, 30, 0))
}

//
// I expect students to write more tests!
//

// FILL(arjun): Some tests

//
// Student-written type and functions
//

case class AlarmClock(now : Time, alarm : Option[Time], alarmOn : Boolean)

def toAlarmClock(time : Time) : AlarmClock = AlarmClock(time, None, false)

def getTime(clock : AlarmClock) : Time = clock.now


def isAlarmOn(clock : AlarmClock) : Boolean = clock.alarmOn

// Produces a new AlarmClock with the alarm set to the specified time
def setAlarm(clock : AlarmClock, alarmTime : Time) : AlarmClock = {
  AlarmClock(clock.now, Some(alarmTime), false)
}

def tickAlarmClock(clock : AlarmClock) : AlarmClock = {
  val next = tick(clock.now)
  if (clock.alarm == Some(next)) {
    AlarmClock(next, clock.alarm, true)
  }
  else {
    // Careful not to turn off the alarm after one second!
    AlarmClock(next, clock.alarm, clock.alarmOn)
  }
}

def drawHands(time : Time) : Image = {
  // angle is specified in degrees off the horizontal axis
  val h = 90 - (time.hours % 12) * 30
  val m = 90 - time.minutes * 6
  val s = 90 - time.seconds * 6
  overlay(overlay(angle(s, 100, red),
                  move(overlay(angle(m, 70, black, 5),
                               move(angle(h, 50, black, 10), 20, 20)),
                       30, 30)),
          oval(200, 200, blue))
}

def animateClock(startTime : Time) = {
  animate(startTime,
          width = 400,
          height = 400,
          draw = drawHands,
          tick = tick,
          refreshRate = 1)
}

