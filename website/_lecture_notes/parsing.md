---
layout: lecture
title: Parsing
---

## Motivation

Regular expressions are only good for small problems. You can't write a regular
expression to match more complex strings, e.g., arithmetic expressions, programs,
etc.

## Example Grammars (convert these to Scala)

Equivalent to `(a|b)`

    e ::= "a"
        | "b"

Equivalent to `(a|b)*`

    e ::= "a" e
        | "b" e
        | ""

Equivalent to `(ab)+`

    e ::= "ab" e
        | "ab"

Equivalent to `ab*`:

    e ::= "a" f

    f ::= "b" f
        | ""

Grammar of nested parentheses (no regex equivalent):

    e ::= ""
        | "(" e ")"

HTML fragment grammar:

    html ::= "<html"> head body "</html>

    head ::= ""
           | "<head>" title "</head>"

    body ::= "<body>" ps "</body>"

    ps ::= ""
            | p ps

    p ::= "<p>" text "</p>"

Grammar of integers `[0-9]+`:

    num ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
           | num num

Grammar of arithmetic expressions:

    e ::= num
        | e "+" e
        | e "*" e

Terminology: *terminals* and *non-terminals*.

## Parse trees

Terminology: ambiguous and unambiguous parses.

Disambiguation:

   atom ::= num

   add ::= atom
         | atom "+"" add

   mul ::= add
         | add "+" mul

   e ::= mul


## Parsing

- Deciding if a string is in the grammar is nice, but not very useful.
- Convert the grammar of regexes to code
