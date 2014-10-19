Support code:

- [ ] Definition of Expr and generator

Assignment:

1. Implement the eval function
2. Implement the parse function
   - Read the ambiguous grammer
   - Read the unambiguous grammar
3. Implement the print function (no hints)
   - Give the property to test


Ambiguous grammar:

  number ::= ...

  expr ::= number
         | expr + expr
         | expr - expr
         | expr * expr
         | expr / expr
         | ( expr )

 Unambiguous grammar:

 atom ::= number
        | ( expr )

  add ::= atom
        | atom + add
        | atom - add

  mul ::= add
        | add * mul
        | add / mul

  expr ::= mul

