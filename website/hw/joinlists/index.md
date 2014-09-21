---
layout: hw
title: Join Lists
---


Lists, as you've seen them until now, permit only sequential access. You can't
get to the third element until you've been through the first two. This means
that when you recur, you do so on a list that is only one element smaller. If
you could recur on sub-lists that were significantly smaller (by half, say), and
could run each recursive call on a different processor core, you might obtain
significant speedup. That requires a different data structure, which is what
we're going to explore.

## Preliminaries

<img src="http://imgs.xkcd.com/comics/manuals.png">

In addition, you're going to start using Scala's standard library.




