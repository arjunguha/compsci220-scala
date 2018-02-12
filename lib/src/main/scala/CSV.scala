package hw.csv

import java.io.File
import com.github.tototoshi.csv.{CSVReader => TotoshiCSVReader}


object CSVReader {

  /** Reads a CSV file.  */
  def fromFile(name: String): List[List[String]] = {
    val reader = TotoshiCSVReader.open(new File(name))
    try {
      reader.all()
    }
    finally {
      reader.close()
    }
  }

}
