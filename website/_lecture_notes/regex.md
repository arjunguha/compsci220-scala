---
layout: lecture
title: Regular Expressions
---

## Regular Expression pragmatics

Motivation: text processing is everywhere

   - Form validation (phone numbers, emails, URLs)
   - Search and replace
   - Cleaning data
   - Virus scanners, packet filters
   - etc.

As a pattern:

   - Constants: a b c d
   - Sequences of constants (obvious)
   - Alternation: a | b
   - zero or more: a*
   - One or zero: a?
   - one or more: a+
   - Metacharater "." (a | b | c | ...)
   - Ranges: [a-z] == a | b | ... | z
   - Character classes: \d == [0-9]

In Java/Scala:

    import java.util.regex._
    Pattern.matches("a*b","aaaab")

Finding multiple matches:

    "a*b".findAllIn(str).toList

Grouping (e.g., dates):

    val date = """(\d\d\d\d)-(\d\d)-(\d\d)""".r
    "2004-01-20" match {
      case date(year, month, day) => s"$year was a good year for PLs."
    }

Show grep (on sheet.csv)

## Regular Expression Principles

- Reduce everything to core regex via identities
- As an ADT
- Show Scala infix operators and implicits
- Build backtracking matcher





