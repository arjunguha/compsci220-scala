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
below). There's also the Driver class, which is used in Part 4.

You should configure your sbt file accordingly.

## Part 1: Depth-first Search

The basic depth-first search (DFS) algorithm takes some graph *G* and a starting
node *v*, then does the following:

1. Label *v* as discovered
2. Find all nodes that are adjacent to *v*
3. For all adjacent nodes *w* that are undiscovered, recusively call DFS(*G*, *w*)

This requires some way to mark the nodes of the graph *G*, which is usually done
with some field in the node or a global list of discovered nodes. We can't use
the first way because our nodes are just Ints. The second way would require
a mutable list that is accessible to all of the calls to DFS, which would be
impossible to do with Scala's recursion.

Instead, we'll use a stack to keep track of the nodes we need to visit and a set
to keep track of all the nodes we've already visited. Scala provides a Set class
for us, and we can use Scala's List type as a stack.

Again, we start with the graph *G* and the starting node *v*. We also have a set
of discovered nodes *S*, which is initially empty, and a list of nodes to visit,
L, which we will treat as a stack.

0. Push the initial node onto the stack *L*
1. If *L* is empty, return the set of visited nodes *S*
2. Pop *w*, the first element of *L*
3. If *w* has not been discovered, mark it as discovered, then push all nodes
   adjacent to *w* onto the stack.
4. If *w* has already been discovered, iterate.

This can be implemented as a while loop or a recursive function with a helper. I
suggest you do it recursively. Marking a node as discovered means adding it the
the set *S* of discovered nodes.

In both cases, DFS returns the set of nodes that were visited from the starting
node.

Write the function depthFirstSearch() that performs a depth-first search from
the start node of the graph maze to the finish node:

{% highlight scala %}
def dfs(maze: MazeGraph): Set[Graph.Node]
{% endhighlight %}

*If you choose to do this recursively, you'll need a helper function that takes
the graph, the set of discovered nodes, and the list of nodes to visit.*

## Part 2: Depth-first Search with Paths

Unfortunately, the vanilla DFS algorithm only tells us which nodes we can reach
from the start node. It doesn't give us an actual path from the start to the
finish. In order to do this, we'll need to keep track of when each node was
added to the stack.

One way to do this is to keep track of pairs of nodes. Instead of just pushing
the adjacent nodes to the stack, we'll push the adjacent node and the current
node as a pair to the stack. When we pop a value from a stack, we'll be getting
a pair that contains the top node and the node that pushed it (the "pusher").
Instead of tracking the discovered nodes with a set, we'll use a map that uses
the top node as the key and the pusher as the value.

In pseudocode:
<pre>
// Popping from the stack gives us the top node and the node that pushed it
(top, pusher) = stack.pop
// Our key map maps the top node and the node that pushed it
map[top] = pusher
</pre>

You may discover other ways to do this. The important thing is that, when the
depth-first search is finished, you have a way to connect the nodes together. To
actually get the path from start to finish, you need to process the connections
found by the DFS by starting from the maze's end node (maze.finish) and working
your way backwards. For example:

{% highlight scala %}
// This isn't quite Scala syntax. Don't worry about it.
map = {start => -1, 1 => start, 2 => 1, finish => 2}
// Start at the finish node, work backward
curr = finish
while (map[curr] != -1)
    println(curr)
    curr = map[curr]
// Prints:
// finish
// 2
// 1
// start
{% endhighlight %}

Write the functions depthFirstSearchPath() that finds the path from the maze's
start to its finish using the DFS algorithm. Also write a function called
dfsWalk that produces (*does not* print) the path from start to finish.

{% highlight scala %}
def depthFirstSearchPath(maze: MazeGraph): List[Graph.Node]

def dfsWalk(path: ??? ): List[Graph.Node]
{% endhighlight %}

*depthFirstSearchPath() should call dfsWalk after running the DFS algorithm in
order to produce the correct path as a list. The argument type for dfsWalk is up
to you, depending on your implementation of the DFS path algorithm.*

*You **cannot** solve this by calling your previous DFS function. But you can easily
adapt it to solve this problem.*

### Optional: Breadth-First Search (Read it anyway)

Unfortunately, the DFS algorithm is not guaranteed to find the shortest path in
the maze. We could instead use the breadth-first search (BFS) algorithm, which
is guaranteed. Because this section is optional, you should look up how BFS
differs from DFS on your own.

Your implementation of DFS can easily be converted to BFS by using a queue
instead of a stack to track nodes to check. Scala's Queue type doesn't
follow the same conventions as List, so you'll have to change a bit more code to
get it to work. Try it out by implementing `breadthFirstSearch' and
`breadthFirstSearchPath'.

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

## Part 4: Driving Directions
