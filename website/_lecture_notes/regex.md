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
   - Alternation: `a | b`
   - Sequencing: `a*`, `a?`, `a+`, `a{m,n}`
   - Metacharaters `.`, `^`, `$`
   - Ranges: `[a-z]`, range-complement, `[^ a-z]`
   - Escaping meta characters, e.g., `\.`, `\$`, etc.
   - Character classes: `\d`


Basic version:

    import java.util.regex._
    Pattern.matches("a*b","aaaab")

Finding multiple matches:

    "a*b".findAllIn(str).toList

I'll use `$` and `^` to match the whole string.

Grouping (e.g., dates):

    val date = """(\d\d\d\d)-(\d\d)-(\d\d)""".r
    "2004-01-20" match {
      case date(year, month, day) => s"$year was a good year for PLs."
    }

Replacement:

    "Jan|January".r.replaceAllIn(str, "01")

Show grep (on sheet.csv)

## Regular Expression Principles

- Reduce everything to core regex via identities
- As an ADT
- Show Scala infix operators and implicits
- Build backtracking matcher





