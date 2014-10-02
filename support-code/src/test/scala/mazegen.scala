object MazeGenerator {

  private var width:   Int = 0
  private var height:  Int = 0
  private var maze:    Array[Array[Int]] = null

  /* Show the maze. */
  def show {
    var blocks = "\u2588\u2588"
    maze.foreach(row => {
      row.foreach(block => print(if(block == 1) blocks else "  "))
      println
    })
  }

  def asText : String = {
    var blocks = "\u2588\u2588"
    var res = "";
    maze.foreach(row => {
      row.foreach(block => res += (if(block == 1) blocks else "  "))
      res += "\n"
    })
    res
  }

  def genGraph {
    maze.foreach(row => {
      row.foreach(block => print(if(block == 1) "1" else "0"))
      println
    })
  }

  def asGraph : String = {
    var res = "";
    maze.foreach(row => {
      row.foreach(block => res += (if(block == 1) "1" else "0"))
      res += "\n"
    })
    res
  }

  /* Start carving at x, y. */
  private def carve(x: Int, y: Int) {

    def update_pos(dir: Int, x: Int, y: Int): (Int, Int) = dir match {
      case 0 => (x + 1, y + 0)
      case 1 => (x + 0, y + 1)
      case 2 => (x - 1, y + 0)
      case _ => (x + 0, y - 1)
    }

    var dir:    Int = (scala.math.random * 4.0).toInt
    var count:  Int = 0
    while(count < 4) {
      val (x1, y1) = update_pos(dir, x, y)
      val (x2, y2) = update_pos(dir, x1, y1)
      if(x2 > 0 && x2 < width && y2 > 0 && y2 < height) {
        if(maze(y1)(x1) == 1 && maze(y2)(x2) == 1) {
          maze(y1)(x1) = 0
          maze(y2)(x2) = 0
          carve(x2, y2)
        }
      }
      count += 1
      dir = (dir + 1) % 4
    }

  }

  /* Generate a maze. */
  def generate(w: Int, h: Int) {
    width = w
    height = h
    maze = Array.fill[Int](height, width)(1)
    maze(1)(1) = 0
    carve(1, 1)
    maze(0)(1) = 0
    maze(height - 1)(width - 2) = 0
  }

  def usage {
    println("usage: scala mazegen.scala MAXWIDTH MAXHEIGHT graph|pretty");
    System.exit(1);
  }

  /* Generate and display a random maze. */
  def main(args: Array[String]) {
    if (args.length != 3) {
      usage
    }

    var maxwidth  = args(0).toInt
    var maxheight = args(1).toInt
    var output = args(2)

    import scala.util.Random
    val r = new Random

    var width  = -1
    var height = -1

    while (width == -1 || (width % 2) == 0 || maxwidth < 21) {
      width = r.nextInt(200)
    }

    while (height == -1 || (height % 2) == 0 || maxheight < 21) {
      height = r.nextInt(200)
    }

    generate(width, height)

    output match {
      case "graph"  => genGraph
      case "pretty" => show
      case _ => usage
    }
  }

}
