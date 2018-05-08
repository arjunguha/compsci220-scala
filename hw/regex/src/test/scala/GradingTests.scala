class GradingTests extends org.scalatest.FunSuite {
  import Regexes._

  def check(re: scala.util.matching.Regex, str: String): Boolean = {
    re.pattern.matcher(str).matches()
    }

  test("notAlphanumeric -- symbols and spaces") {
    
    assert(check(notAlphanumeric, " #$!$$"))
    }

  test("notAlphanumeric -- letters and numbers") {
    
    assert(!check(notAlphanumeric, "kmafnFSF2323"))
    }

  test("notAlphanumeric -- mix of letters and symbols") {
    
    assert(check(notAlphanumeric, " #$!asdsd$$") == false)
    }

  test("time -- midnight") {
    
    assert(check(time, "00:00") == true)
    }

  test("time -- after noon") {
    
    assert(check(time, "15:34") == true)
    }

  test("time -- bad minute") {
    
    assert(check(time, "15:60") == false)
    }

  test("time -- bad hour") {
    
    assert(check(time, "25:01") == false)
    }

  test("phone number -- okay") {
    
    assert(check(phone, "(641) 275-1531"))
    }

  test("phone number -- bad paren") {
    
    assert(check(phone, "(641( 275-1531") == false)
    }

  test("phone number -- letters") {
    
    assert(check(phone, "(64A) 275-1531") == false)
    }

  test("zip -- five digit") {
    
    assert(check(zip, "02915"))
    }

  test("zip -- nine digit") {
    
    assert(check(zip, "02915-9232"))
    }

  test("zip -- too long") {
    
    assert(check(zip, "02915-92322") == false)
    }

  test("comment -- short") {
    
    assert(check(comment, "/* */"))
    }

  test("comment -- /**********/") {
    
    assert(check(comment, "/****************/"))
    }

  test("comment -- no closing *") {
    
    assert(check(comment, "/* /") == false)
    }

  test("roman -- four Is") {
    
    assert(check(roman, "IIII") == false)
    }

  test("roman -- four Is after V") {
    
    assert(check(roman, "VIIII") == false)
    }

  test("roman -- LL") {
    
    assert(check(roman, "LL") == false)
    }

  test("roman -- XV") {
    
    assert(check(roman, "XV") == true)
    }

  test("date -- not leap year") {
    
    assert(check(date, "2015-02-29") == false)
    }

  test("date -- leap year") {
    
    assert(check(date, "2016-02-29") == true)
    }

  test("date -- month ok") {
    
    assert(check(date, "2015-03-31") == true)
    }

  test("date -- month zero") {
    
    assert(check(date, "2015-00-01") == false)
    }

  test("date -- month bad") {
    
    assert(check(date, "2015-04-31") == false)
    }

  test("evenParity -- even") {
    
    assert(check(evenParity, "222222222222222") == true)
    }

  test("evenParity -- odd") {
    
    assert(check(evenParity, "9999999") == false)
    }

  test("evenParity -- odd can sum to even") {
    
    assert(check(evenParity, "9991") == true)
    }
}
