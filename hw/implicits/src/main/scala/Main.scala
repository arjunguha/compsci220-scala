object PathImplicits {

  import java.nio.file.{Path, Paths, Files, StandardOpenOption}
  import StandardOpenOption.{CREATE, APPEND}

  implicit class RichString(str: String) {
    def /(other: Path): Path = Paths.get(str).resolve(other)
    def /(other: String): Path = Paths.get(str).resolve(other)
  }

  implicit class RichPath(path: Path) {
    def /(other: Path): Path = path.resolve(other)
    def /(other: String): Path = path.resolve(other)

    def write(data: String): Unit = Files.write(path, data.getBytes)
    def read(): String = new String(Files.readAllBytes(path))
    def append(line: String): Unit = Files.write(path, line.getBytes, CREATE, APPEND)
  }

}

object TimeImplicits {

  import java.time.LocalDate

  class Day(val n: Int)
  class Month(val n: Int)
  class Year(val n: Int)

  implicit class RichInt(n: Int) {
    def jan(): LocalDate = LocalDate.of(LocalDate.now.getYear, 1, n)
    def feb(): LocalDate = LocalDate.of(LocalDate.now.getYear, 2, n)
    def mar(): LocalDate = LocalDate.of(LocalDate.now.getYear, 3, n)
    def apr(): LocalDate = LocalDate.of(LocalDate.now.getYear, 4, n)
    def may(): LocalDate = LocalDate.of(LocalDate.now.getYear, 5, n)
    def jun(): LocalDate = LocalDate.of(LocalDate.now.getYear, 6, n)
    def jul(): LocalDate = LocalDate.of(LocalDate.now.getYear, 7, n)
    def aug(): LocalDate = LocalDate.of(LocalDate.now.getYear, 8, n)
    def sep(): LocalDate = LocalDate.of(LocalDate.now.getYear, 9, n)
    def oct(): LocalDate = LocalDate.of(LocalDate.now.getYear, 10, n)
    def nov(): LocalDate = LocalDate.of(LocalDate.now.getYear, 11, n)
    def dec(): LocalDate = LocalDate.of(LocalDate.now.getYear, 12, n)

    def jan(year: Int): LocalDate = LocalDate.of(year, 1, n)
    def feb(year: Int): LocalDate = LocalDate.of(year, 2, n)
    def mar(year: Int): LocalDate = LocalDate.of(year, 3, n)
    def apr(year: Int): LocalDate = LocalDate.of(year, 4, n)
    def may(year: Int): LocalDate = LocalDate.of(year, 5, n)
    def jun(year: Int): LocalDate = LocalDate.of(year, 6, n)
    def jul(year: Int): LocalDate = LocalDate.of(year, 7, n)
    def aug(year: Int): LocalDate = LocalDate.of(year, 8, n)
    def sep(year: Int): LocalDate = LocalDate.of(year, 9, n)
    def oct(year: Int): LocalDate = LocalDate.of(year, 10, n)
    def nov(year: Int): LocalDate = LocalDate.of(year, 11, n)
    def dec(year: Int): LocalDate = LocalDate.of(year, 12, n)

    def days() = new Day(n)
    def months() = new Month(n)
    def years() = new Year(n)
  }

  implicit class RichLocalDate(date: LocalDate) {
    def +(day: Day): LocalDate = date.plusDays(day.n)
    def +(month: Month): LocalDate = date.plusMonths(month.n)
    def +(year: Year): LocalDate = date.plusYears(year.n)
  }

}