---
layout: default
title: Home
---

Welcome to the {{ site.data.config.semester }} offering of COMPSCI220 Programming Methodology.

**Textbook**: There is **no required textbook**. Instead, you must read the
lecture notes posted on the schedule, which includes references to other online
resources when appropriate.

If you want to read a book on Scala programming, we recommend the following book:

[Programming in Scala: A Comprehensive Step-by-Step Guide (2nd Edition), Odersky, Spoon, Venners][textbook]

You must read the entire [Course Policy] document.

***

## Course Locations

<table class="table table-striped">
<tr>
  <th>What</th>
  <th>When</th>
  <th>Where</th>
</tr>
{% for ix in site.data.course_locations %}
<tr>
  <td>{{ix.what}} </td>
  <td>{{ix.when}}</td>
  <td>{{ix.where}}</td>
</tr>
{% endfor %}
</table>

***

## Office Hours

<table class="table table-striped">
<tr>
  <th>When</th>
  <th>Where</th>
  <th>Who</th>
</tr>
{% for ix in site.data.office_hours %}
<tr>
  <td>{{ix.when}} </td>
  <td>{{ix.where}}</td>
  <td>{{ix.who}}</td>
</tr>
{% endfor %}
</table>

***

## Schedule

<!-- NOTE(rachit): The date should be in the format Weekday, Month (first three
letters) Day. The default layout expects this format in the schedule table. -->

| Date| Notes|
|:--|--|
| Tuesday, Sep 6    | Out: [HW1]. [Lecture Notes](../reading/lecture1.pdf).                                           |
| Wednesday, Sep 7  | *Lab:* Scala technical support. No need to attend if you've setup Scala already.                |
| Thursday, Sep 8   | [Lecture Notes](../reading/lecture2.pdf).                                                       |
| Tuesday, Sep 13   | Due: [HW1]. Out: [HW2]. [Lecture Notes](../reading/lecture3.pdf).                               |
| Wednesday, Sep 14 | *Lab:* Sketch of Steps 1 and 2 of [HW2].                                                        |
| Thursday, Sep 15  | [Lecture Notes](../reading/lecture4.pdf).                                                       |
| Tuesday, Sep 20   | Due: [HW2]. Out: [HW3]. [Lecture Notes](../reading/lecture5.pdf).                               |
| Wednesday, Sep 21 | *Lab*: Sketch of Steps 1 and 4 of [HW3].                                                        |
| Thursday, Sep 22  | [Lecture Notes](../reading/lecture6.pdf).                                                       |
| Tuesday, Sep 27   | Due: [HW3]. Out: [HW4]. [Lecture Notes](../reading/gc.pdf)                                      |
| Wednesday, Sep 28 | *Lab*:                                                                                          |
| Thursday, Sep 29  | [Lecture Notes](../reading/lecture8.pdf)                                                        |
| Tuesday, Oct 4    | Due: [HW4]. Out: [HW5]. [Lecture Notes](../reading/lecture9.pdf).                               |
| Wednesday, Oct 5  | *Lab:*                                                                                          |
| Thursday, Oct 6   | [Lecture Notes](../reading/lecture10.pdf)                                                       |
| Tuesday, Oct 11   | **No class.** (Monday schedule.)                                                                |
| Wednesday, Oct 12 | **No lab.** (Continue working on Tic Tac Toe.)                                                  |
| Thursday, Oct 13  | [Lecture Notes](../reading/lecture11.pdf)                                                       |
| Tuesday, Oct 18   | Due: [HW5]. Out: [HW6]. [Lecture Notes](../reading/lecture12.pdf)                               |
| Wednesday, Oct 19 | *Lab:*                                                                                          |
| Thursday, Oct 20  | [Lecture Notes](../reading/lecture13.pdf)                                                       |
| Tuesday, Oct 25   | Due: [HW6]. Out: [HW7]. [Lecture Notes](../reading/lecture14.pdf)                               |
| Wednesday, Oct 26 | *Lab:*                                                                                          |
| Thursday, Oct 27  | **Class cancelled. Friday office hours cancelled.**                                             |
| Tuesday, Nov 1    | [Lecture Notes](../reading/lecture16.pdf)                                                       |
| Wednesday, Nov 2  | **No lab.** (Continue working on Sudoku.)                                                       |
| Thursday, Nov 3   | [Lecture Notes](../reading/lecture17.pdf)                                                       |
| Tuesday, Nov 8    | Due: [HW7]. Out: [HW8]. [Lecture Notes](../reading/lecture18.pdf).                              |
| Wednesday, Nov 9  | *Lab:*                                                                                          |
| Thursday, Nov 10  | [Lecture Notes](../reading/lecture19.pdf)                                                       |
| Tuesday, Nov 15   | Due: [HW8]. [Lecture Notes](../reading/lecture20.pdf)                                           |
| Wednesday, Nov 16 | **No lab.** (Friday schedule.)                                                                  |
| Thursday, Nov 17  |                                                                                                 |
| Tuesday, Nov 22   | **No class.** (Thanksgiving Recess.)                                                            |
| Wednesday, Nov 23 | **No lab.** (Thanksgiving Recess.)                                                              |
| Thursday, Nov 24  | **No class.** (Thanksgiving Recess.)                                                            |
| Tuesday, Nov 29   | Reading: [Parser Combinators]. Out: [HW9].                                                      |
| Wednesday, Nov 30 | *Lab:*                                                                                          |
| Thursday, Dec 1   |                                                                                                 |
| Tuesday, Dec 6    | Due: [HW9]. Out: [HW10].                                                                        |
| Wednesday, Dec 7  | *Lab:*                                                                                          |
| Thursday, Dec 8   |                                                                                                 |
| Tuesday, Dec 13   | Due: [HW10].                                                                                    |
| Wednesday, Dec 14 | **No lab.** (No assignment due.)                                                                |
{: .table .table-striped #schedule-table :}
***

[HW1]: ../hw/hw1.pdf
[HW2]: ../hw/hw2.pdf
[HW3]: ../hw/hw3.pdf
[HW4]: ../hw/hw4.pdf
[HW5]: ../hw/hw5.pdf
[HW6]: ../hw/hw6.pdf
[HW7]: ../hw/hw7.pdf
[HW8]: ../hw/hw8.pdf
[HW9]: ../hw/hw9.pdf
[HW10]: ../hw/hw10.pdf

[Parser Combinators]: http://www.artima.com/pins1ed/combinator-parsing.html
[Java Regular Expressions]: http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
[Scala Regular Expressions]: http://www.scala-lang.org/api/current/index.html#scala.util.matching.Regex
[Course Policy]: ../policies
[textbook]: http://www.amazon.com/Programming-Scala-Comprehensive-Step-Step/dp/0981531644
