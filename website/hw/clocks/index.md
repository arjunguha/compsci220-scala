---
layout: hw
title: Clocks
---

<div>
<a href="http://xkcd.com/1359/"><img src="http://imgs.xkcd.com/comics/phone_alarm.png"></a>
</div>

Introduction
------------

For this assignment, you will develop types and functions to represent an alarm
clock. You will first write functions over a `Time` type that we have defined
for you. You will then use this type to define the `AlarmClock` type yourself
and several functions over `AlarmClock`. Finally, you will develop functions to
draw `AlarmClock`s using a simple [graphics] graphics library.

We've broken the assignment into several parts. You may submit each part to the
grading system to be tested independently. Since this is the first assignment,
you can re-submit up to *twenty times* before the due date. We will not be this
lenient for future assignments.

Setup
-----

Before you start programming, you need to complete a few preliminary steps.

<ol>

<li>First, you need to <a href="software">download and start</a> the course
    virtual machine. You will need it for all the assignments in this
    class.</li>

<li>Next, you need to prepare a directory that will store your assignment
   files. To do so, enter the following commands into a terminal window:

{% highlight console %}
mkdir clock
cd clock
{% endhighlight %}

   The first command creates the <code>clock</code> directory and changes the
   current directory to <code>clock</code>.</li>

<li>Using a text editor, create a file called <b>Clock.scala</b> in the
   <code>clock</code> directory. At the top of the file, add the following two
   lines:

{% highlight scala %}
import cmpsci220.testing._
import cmpsci220.graphics._
{% endhighlight %}

   These lines will let you use several testing and graphics functions that
   you need for this assignment.</li>

</ol>

Time
----

To build the alarm clock, we first need a type to represent the current time.
Your program should use the following case class to do so:

{% highlight scala %}
case class Time(hours : Int, minutes : Int, seconds : Int)
{% endhighlight %}

The `Time` type has three members that represent time in hour, minutes, and
seconds, using integers. However, not all `Time` values are meaningful
representations of time. Here are some examples of valid times:

{% highlight scala %}
val ex1 = Time(0, 1, 0)
val ex2 = Time(7, 23, 19)
val ex3 = Time(23, 50, 0) // We are using military time
{% endhighlight %}

But, the following values are invalid:

{% highlight scala %}
val ex5 = Time(0, -1, 0)
val ex6 = Time(7, 0, 200)
{% endhighlight %}

<ol start="4">

<li>Write a function called <code>isValidTime</code> that returns
  <code>true</code> if the time is valid and <code>false</code> otherwise. The
  <code>isValidTime</code> function must have the following type:

{% highlight scala %}
def isValidTime(time : Time) : Boolean
{% endhighlight %}</li>

<li>Write a function called <code>tick</code> that takes a time-value as an
  argument and evaluates to a new time that is one second in the future. The
  <code>tick</code> function must have the following type:

{% highlight scala %}
def tick(time : Time) : Time
{% endhighlight %}

  Here are two test cases that the function should pass. You <i>must</i> write
  more tests yourself:

{% highlight scala %}
test("8:30:01 is after 8:30:00") {
  assert(tick(Time(8, 30, 0)) == Time(8, 30, 1))
}

test("8:30:00 is after 8:29:59") {
  assert(tick(Time(8, 29, 59)) == Time(8, 30, 0))
}
{% endhighlight %}

  You should assume that the argument to <code>tick</code> is a valid time and
  ensure that the result of <code>tick</code> is also a valid time. (Write test
  cases!)
  </li>

</ol>

Alarms
------

<ol start="6">

<li>An alarm clock must keep track of (1) the current time, (2) the alarm time,
  and (3) if the alarm is on. Design a case class called
  <code>AlarmClock</code> to do so:

{% highlight scala %}
case class AlarmClock(/*... add members here ... */)
{% endhighlight %}
</li>

<li>Write and <i>test</i> the following functions that manipuate <code>AlarmClock</code> values.</li>

{% highlight scala %}
// Maps a time to an AlarmClock with the alarm turned off
def toAlarmClock(time : Time) : AlarmClock

// Produces the time stored in the alarm clock
def getTime(clock : AlarmClock) : Time

// Produces true if the alarm is on
def isAlarmOn(clock : AlarmClock) : Boolean

// Produces a new AlarmClock with the alarm set to the specified time
def setAlarm(clock : AlarmClock, alarmTime : Time) : AlarmClock

// Produces a new AlarmClock that (1) has advanced the current time by one
// second and (2) has the alarm turned on
def tickAlarmClock(clock : AlarmClock) : AlarmClock
{% endhighlight %}

  You should use the <code>tick</code> function you wrote earlier as a helper
  function.</li>

</ol>

Drawing Clock Hands
-------------------

Write a function called `drawHands` that takes a `Time` and evaluates to an
`Image` of clock hands. (Ignore the clock face for the moment). The `drawHands`
function must have the following type:

{% highlight scala %}
def drawHands(time : Time) : Image
{% endhighlight %}


You will need to use the following functions from the [graphics] library:

<ul>

<li>The <code>angle</code> function draws a line at an angle, measured in
  degrees from the horizontal axis. It has parameters that determine the length,
  width, and color of the line. You should use it to draw the hands of the
  clock.</li>

<li>The <code>overlay</code> function places one image on top of another. You
  should use it to combine the images of each hand into a composite image of the
  clock. Clocks typically have the second hand on top of the minute hand and the
  minute hand on top of the hour hand.</li>

<li>The <code>move</code> function moves an image. The lengths of clocks hands
  are different: the second hand is longer than the minute hand, which is longer
  than the hour hand. If you only use <code>angle</code> and
  <code>overlay</code>, the hands will not be centered. You will need to use
  <code>move</code> to move the hour and minute hands onto the second hand.</li>

<li>The <code>show</code> function displays an image. For example, the following
expression

{% highlight scala %}
show(drawHands(Time(7, 23, 19)))
{% endhighlight %}

  may draw a clock that looks like this:<br>

  <img alt="Clock showing 7:23:19" src="clock-ex2-drawhands.png"><br>

  You don't have to exactly reproduce this image, but your clock must look
  "reasonable": each hand should have a different length and all arms should
  be centered.</li>

</ul>

Animate the Clock
-----------------

Now that you have a function to draw a clock, you can write a function to tick
the clock every second. You can use the `animate` function in the graphics
library. Here is a function that animates `Time` values.

{% highlight scala %}
def animateClock(startTime : Time) : Time = {
  animate(startTime,
          width = 400,
          height = 400,
          draw = drawHands,
          tick = tick,
          refreshRate = 1)
}
{% endhighlight %}



**TODO(arjun):** Essentially give students the code to do this. Rendering
the image is hard enough to start. Let students write the function to turn
off the alarm, since it depends on their type definitions.

- Use `animate` to start the clock

- Play 'beep-beep-beep-beep' if isAlarm is true

- Snooze on key-press

Extra Credit: Draw the Clock Face
---------------------------------

**TODO(arjun):** Guide students toward writing a `drawTickMark` function.
  They will have to use `math.sin` and `math.cos` to position the tick.
  The `angle` library function can be used to draw the tick, but will not
   help position it. Turn `overlay` into a variable-arity function and
   have them just overlay 12 invocations of `drawTickMark` instead of
   trying to fold over overlay.


[graphics]: lib/api/#cmpsci220.graphics.package

