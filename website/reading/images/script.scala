import cmpsci220._
import cmpsci220.graphics._
val rect1 = rect(100, 300)
val rect2 = rect(200, 300, red)
val oval1 = oval(300, 100, blue)
saveImage("rect1.png", rect1)

val twoRects = overlay(rect1, rect2)
saveImage("twoRects.png", twoRects)

def manyRectangles(length: Int): Image = {
  if (length <= 0) {
    blank
  }
  else {
    overlay(rect(length, length), manyRectangles(length - 10))
  }
}

val many1 = manyRectangles(200)

saveImage("many1.png", manyRectangles(200))

saveImage("moved.png", move(rect(100, 100, red), 50, 200))

def manyCenteredRects(length: Int): Image = {
  if (length <= 0) {
    blank
  }
  else {
    overlay(rect(length, length), move(manyCenteredRects(length - 10), 5, 5))
  }
}

saveImage("centered.png", manyCenteredRects(200))
