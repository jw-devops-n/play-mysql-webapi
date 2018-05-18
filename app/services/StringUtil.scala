package services

import java.security.MessageDigest
import java.sql.{Date, Timestamp}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object StringUtil {
  def parseToMD5(raw: String): String = {
    val md5 = MessageDigest.getInstance("MD5")
    val bytes = md5.digest(raw.getBytes())
    val stringBuffer = new StringBuffer()
    for (b <- bytes) {
      val bt = b & 0xff
      if (bt < 16) {
        stringBuffer.append(0)
      }
      stringBuffer.append(Integer.toHexString(bt))
    }

    stringBuffer.toString.toUpperCase()
  }

  /**
    * 字符串增强类，隐式扩展为String类型增加数据类型转换扩展方法，返回Option类型对象。
    *
    * 适用于字符串，到以下类型的转换：
    *
    * Int，Long，Boolean，BigDecimal，Joda DateTime，java.sql.Timestamp，java.sql.Date
    *
    * 除toIntOptZero，在转换失败时返回Some(0),其他方法均返回None
    *
    * 调用方法： "xxxxx".扩展方法，如"999.99".toDecimalOpt，返回：Some(BigDecimal(999.99)); "99x.99".toBigDecimalOpt，返回None
    *
    * @param s 扩展的字符串实例对象
    */
  implicit class StringEnhancements(val s: String) {

    import scala.util.control.Exception._
    private val localDateTimeFormatter =DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss")

    def toIntOpt: Option[Int] = catching(classOf[NumberFormatException]) opt s.toInt

    def toLongOpt: Option[Long] = catching(classOf[NumberFormatException]) opt s.toLong

    def toDoubleOpt: Option[Double] = catching(classOf[NumberFormatException]) opt s.toDouble

    def toIntOptZero: Option[Int] = {
      val i = toIntOpt
      if (i.isDefined) {
        i
      } else {
        Some(0)
      }
    }

    def toBooleanOpt: Option[Boolean] = catching(classOf[IllegalArgumentException]) opt s.toBoolean

    def toBigDecimalOpt: Option[BigDecimal] = catching(classOf[NumberFormatException]) opt new BigDecimal(new java.math.BigDecimal(s))

    def toJodaDateTimeOpt: Option[DateTime] = {
      val l = toLongOpt
      if (l.isDefined && l.get > 0) {
        Some(new DateTime(l.get))
      } else {
        try {
          Some(DateTime.parse(s,localDateTimeFormatter))
        } catch {
          case _: Any =>
            None
        }
      }
    }

    def toTimestampOpt: Option[Timestamp] = {
      val l = toLongOpt
      if (l.isDefined && l.get > 0) {
        Some(new java.sql.Timestamp(l.get))
      } else {
        None
      }
    }

    def toSqlDateOpt: Option[Date] = {
      val l = toLongOpt
      if (l.isDefined) {
        Some(new java.sql.Date(l.get))
      } else {
        None
      }
    }

    def toNone: Option[String] = {
      if (s.nonEmpty)
        Some(s)
      else
        None
    }

    //    def toOptBSOBObjectId = {
    //      if (s.nonEmpty) {
    //        if (BSONObjectID.parse(s).getOrElse(BSONObjectID.generate()).stringify == s) {
    //          Option(BSONObjectID.parse(s).get)
    //        } else {
    //          None
    //        }
    //      } else {
    //        None
    //      }
    //    }
  }

  implicit class OptionStringEnhancements(val s: Option[String]) {
    def toRealNone: Option[String] = {
      s match {
        case Some(str) ⇒
          if (str.nonEmpty) {
            s
          } else {
            None
          }
        case None ⇒
          None
      }
    }

    def toOptDouble: Option[Double] = {
      s match {
        case Some(str) ⇒
          if (str.nonEmpty) {
            str.toDoubleOpt
          } else {
            None
          }
        case None ⇒
          None
      }
    }

    def toOptInt: Option[Int] = {
      s match {
        case Some(str) ⇒
          if (str.nonEmpty)
            str.toIntOpt
          else {
            None
          }
        case None ⇒
          None
      }
    }

    def toOptBigDecimal: Option[BigDecimal] = {
      s match {
        case Some(str) ⇒
          if (str.nonEmpty) {
            str.toDoubleOpt match {
              case Some(dbl) ⇒ Option(BigDecimal(dbl))
              case None ⇒ None
            }
          } else {
            None
          }
        case None ⇒
          None
      }
    }
  }
}


