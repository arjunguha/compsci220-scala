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
find the path from the start of the maze to the exit. We'll focus on two
algorithms: Depth-first Search and Dijkstra's Algorithm.

**We provide overviews of the graph algorithms below. We strongly suggest
research these algorithms on your own for additional information.** (Wikipedia
is a great resource for this!)

## Part 0: Importing

We'll be using a library called "edgemaze" for this.

{% highlight scala %}
import edgemaze.\_
{% endhighlight %}

It consists of the Graph package (defines Node and Edge), MazeGraph (defines the
actual maze representation) and util (contains the minMember function, discussed
below). There's also the TestGraphs package, which contains sample graphs for
you to test your functions on.

You should configure your sbt file accordingly.

## Part 1: Depth-first Search: Reachability

The basic depth-first search (DFS) algorithm takes some graph *G* and a starting
node *v*, then does the following:

1. Label *v* as discovered
2. Find all nodes that are adjacent to *v*
3. For all adjacent nodes *w* that are undiscovered, recusively call DFS(*G*, *w*)

There's also a way to do it iteratively instead of recursively:

1. Push *v* to a stack *S*
2. If *S* is empty, we're done iterating
3. Otherwise, pop the top element off the stack. We'll call it *w*
4. If *w* is not marked as visited, mark it and push all nodes adjacent to *w*
   onto the stack.
5. If *w* has already been visited, ignore it and iterate.

DFS requires some way to mark the nodes of the graph *G*, which is usually done
with some field in the node or a global list of discovered nodes. We can't use
the first way because our nodes are just Ints. The second way requires a list
that is accessible to all of the calls to DFS, which is definitely doable. A
normal Scala List can be used, but a Set makes more sense.

The DFS algorithm returns the list of visited nodes. This is called the set of
*reachable nodes* from the starting node *v*.

Write the function reachable() that performs a depth-first search from a given
node:

{% highlight scala %}
def reachable(graph: EdgeGraph, start: Graph.Node): Set[Graph.Node]
{% endhighlight %}

You may do this recursively (by writing a helper function) or iteratively.

## Part 2: Depth-first Search: Paths

You'll notice that reachable() doesn't actually tell us the path from one node
to another. To do that, we need to modify our code and keep track of which nodes
are reachable from each other.

We need some way to keep track of which nodes are next to each other. If we have
a graph where 0 (our starting node) is connected to 1 and 2, and 2 is connected
to 3:

<pre>
0 --> 1
|
v
2 --> 3
</pre>

Then our path from 0 to 3 is (0 -> 2 -> 3). DFS will start at 0, then process 1
and 2, then process 3. To keep track of the path, we can map the *destination*
node to the *previous* node. For this graph:

0 -> -1 (There's no node before 0, so we give it the "no node" index)
1 -> 0
2 -> 0
3 -> 2

Since our Nodes are just Ints, we can use a Scala Array or List, where the
index is the destination and the value at that index is the "previous" node.
This list should have an entry for every node in the graph, even if it isn't
reached. Unreached nodes will have -1 as their previous node. A node should have
its entry in the list filled only if it isn't already marked. That is, if we
previously found that 3 is reachable through 2, and 3 is also reachable by 1,
3's entry should *not* be updated.

Modify the code of reachable() to create dfsPath(), which takes an EdgeGraph and
a start node. It should return an Array[Graph.Node], which contains the list of
nodes that are adjacent to each other, as described above.

{% highlight scala %}
def dfsPath(graph: EdgeGraph, start: Graph.Node): Array[Graph.Node]
{% endhighlight %}

*Hints:*
- *You **cannot** solve this by calling your previous DFS function. But you can easily
adapt it to solve this problem.*
- *Scala Arrays are mutable. This means if you have an Array[Int] arr, arr(i) =
  j will update the array. You still declare Arrays with **val**.*

### Optional: Breadth-First Search (Read it anyway)

Unfortunately, the DFS algorithm is not guaranteed to find the shortest path in
the maze. We could instead use the breadth-first search (BFS) algorithm, which
is guaranteed to do so. Because this section is optional, you should look up how
BFS differs from DFS on your own.

Your implementation of DFS can easily be converted to BFS by using a queue
instead of a stack to track nodes to check. Scala's Queue type doesn't
follow the same conventions as List, so you'll have to change a bit more code to
get it to work. Try it out by implementing `bfsPath()` using a queue. You'll
have to import scala.collection.immutable.Queue to use queues.

Do note that BFS finds the shortest path *in terms of nodes to traverse*.
In the next section, we'll talk about another algorithm for finding a path in a
graph. Unlike DFS, it's designed to find the shortest path. Unlike BFS, it cares
about the *distance between nodes*.

## Part 3: Dijkstra's Algorithm

Previously, I mentioned that the Edges in our graph contain the distance between
the two nodes. Typical mazes are stored as grids with cell marked as "walls" or
"open spaces". Each of these cells could be stored as a node in the graph, but
that would greatly increase the number of nodes and edges we'd have to store,
even if we ignored all the wall cells. Instead, the graph just encodes the
intersections of the graph, requiring us to store the distance between nodes to
remain faithful to the graph.

The DFS algorithm will find us any path from start to finish. The optional BFS
algorithm will find the path from start to finish with the fewest number of
nodes. **GO READ THE OPTIONAL SECTION IF YOU HAVEN'T YET.**

With this next algorithm, we'll take the edge distance into account and find the
shortest path from start to finish in terms of distance.

Dijkstra's Algorithm was invented, perhaps unsurprisingly, by Edsger Dijkstra, a
Dutch computer scientist. He's a fascinating figure and really quite important
to the history of the field. The algorithm takes a weighted graph and finds the
shortest path from a starting to every other node, though it can be easily
adapted to find the path to a target node. It tracks the distance between a node
and the starting node and which nodes are closest together.

The steps of the algorithm are fairly simple, but there's a bit of set up.
As usual, we start with a graph *G* and a starting node *v*.

1. Assign every node a tentative initial distance
    - For the initial node, the distance is 0
    - For every other node it is infinity (or some arbitrarily high value)
2. Mark all nodes as unvisited
3. Create a set of all unvisited nodes, which will be processed during the
   algorithm. This initially contains all of the nodes.
4. For the current node *v*: For each neighbor *w*, calculate the tentative
   distance. If this is smaller than the current distance, store it.
    - Tentative distance = distance of *v* + length from *v* to *w*
5. Mark the current node as visited and remove it from the unvisited set.
6. Select the unvisited node that has the smallest tentative distance and use it
   as the next current node.

Implement the function dijkstra(), which finds the shortest path through the
maze using Dijkstra's Algorithm. Also implement dijkstraWalk(), which takes the
list of previous nodes created by the algorithm and generates the path from the
start to the finish. dijkstra() should return the correct path, i.e. call
dijkstraWalk to get the final result from inside dijkstra().

{% highlight scala %}
def dijkstra(maze: MazeGraph): List[Graph.Node]

def dijkstraWalk(prev: Array[Graph.Node], curr: Node): List[Graph.Node]
{% endhighlight %}

Some hints:

- As before, we can use a set to track the unvisited nodes.
- The distance to each node can be stored in an array. The indices of the array
  are the nodes, and the values are the distance.
- If you look up Dijkstra's Algorithm, you will see that this is a slightly
  simplified version.
    - In the original, only the unvisited neighbors have their distances
      calculated. This is unnecessary and only slightly changes the runtime.
    - Often, a priority queue is used instead of a set. This adds unnecessary
      complexity, since Scala doesn't have the necessary features in its
      PriorityQueue class.
- For step 6, we provide a function called minMember(). This takes an array of
  values (e.g. the distances) and a set of integers (e.g. the set of unvisited
  nodes) and returns the member of the set that corresponds to the lowest value.
- You don't have to use infinity for the initial distance. Something like 10000
  will be more than enough. (Our mazes won't be THAT big!)
- Arrays in Scala are mutable. This means if you write `array(0) = 100`, the
  array will update in place. *You still declare them using val.*
- If you'd rather use immutable data structures like Lists, you should check out
  the updated() function.
- You can definitely do this recursively! It'll be neat.

## Part 4: Evaluating the Paths
