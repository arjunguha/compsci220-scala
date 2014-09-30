---
layout: hw
title: Mazes
---

<a href="http://xkcd.com/246/">
<img src="http://imgs.xkcd.com/comics/labyrinth_puzzle.png">
</a>

For this homework, we're going to be working with mazes and algorithms for
finding the path through them. We're representing the maze as a graph, which is
a list of nodes and a list of edges. Nodes are a type alias of Ints. This is a
tweak that lets us use Nodes as indexes into arrays. Edges are just tuples that
store the two nodes, the distance between, and the direction from the first node
to the other. This lets us represent the maze as a graph with each intersection
as a node with edges connecting it to the other intersections.

By representing the maze as a graph, we can use graph processing algorithms to
find the path from the start of the maze to the exit.

- DFS
- DFS with Path
- Dijkstra (path built-in)
- Directions
