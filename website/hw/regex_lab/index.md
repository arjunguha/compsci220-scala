---
layout: hw
title: Recursion lab
---

## Preliminaries

Save your work in a file called `regexlab.scala`. Use this template:

{% highlight scala %}
import scala.util.matching.Regex

object RegexLab {

  def negate_alphanumeric: Regex = """<your regex>""".r

  def email: Regex = """<your regex>""".r

  def real_numbers: Regex = """<your regex>""".r

  def landlines: Regex = """<your regex>""".r

  def name: Regex = """<your regex>""".r

  def all_vowels: Regex = """<your regex>""".r

  def string_in_singleline_comment:Regex = """<your regex>""".r

}
{% endhighlight %}

## Programming Task

Your task is to define the regular expression for the following attributes. For
all the methods defined above:

`yourregex.findAllIn(str).toList` must return all the matches found.

Rules for the regular expression:

1. `negate_alphanumeric`: Match any string that is not a character or a digit

2. `email`: (e.g. xxx_xx@xxxx.xxx, xxxx@xxxx.xx)

    ­-  must contain ‘@‘ symbol
    ­-  allowed characters for username are alphabets, digits and underscore
    ­-  must end with .xx or .xxx
    ­-  must contain a domain name

3. `real_numbers` (e.g ­1.0, 2, 1.0, 0.1) all real numbers

4. `landlines` ( e.g. +1­-413­-413-­4133, 413­-413-­4133)

­    - Country code is optional (+1), if present must be accompanied by "-"

­    - The area code and local number is of the form (XXX-XXX-XXXX)

5. `name` (e.g. Mr. Xxx Xxx, Ms. Xxxx Xxxx)

    -­ Names must contain a Title (Mr or Mrs or Ms)
    -­ The first letter in a first name and the last name should be capital letters
    -­ No digits or special characters are allowed

6. `all_vowels` (e.g. aeiouXXX, aXeXiouX )

­   - All words in a string that contains all the vowels

7. `string_in_singleline_comment` (e.g. /* String */)

­   - Match string that are enclosed in /* */

## Submit Your Work

Upload this file to Moodle.