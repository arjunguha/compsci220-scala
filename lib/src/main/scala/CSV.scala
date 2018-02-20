package hw.csv

import hw.Helpers
import java.io.File
import com.github.tototoshi.csv.{CSVReader => TotoshiCSVReader}


object CSVReader {

  /** Reads a CSV file.  */
  def fromFile(name: String): List[List[String]] = {
    if (Helpers.isCloudFunction) {
      return Nil
    }

    val reader = TotoshiCSVReader.open(new File(name))
    try {
      return reader.all()
    }
    finally {
      reader.close()
    }
  }

}
