public class Vector2D {
  private Double x;
  private Double y;

  public Vector2D(Double x, Double y) {
    this.x = x;
    this.y = y;
  }

  public Vector2D mul(Double factor) {
    return new Vector2D(factor * x, factor * y);
  }

  public Vector2D add(Vector2D other) {
    return new Vector2D(this.x + other.x,
                        this.y + other.y);
  }

  public Vector2D neg() {
    return new Vector2D(-x, -y);
  }
}