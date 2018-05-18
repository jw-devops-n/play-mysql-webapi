import java.sql.{Date, Timestamp}

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import play.api.libs.json._

/**
  * Created by TallSafe on 2018/5/10. 
  */
package object models {
  implicit val jodaDateTimeFormat: Format[DateTime] = new Format[DateTime] {
    val isoFmt: DateTimeFormatter = ISODateTimeFormat.dateTime()

    def reads(v: JsValue): JsResult[DateTime] = {
      v match {
        case JsNumber(num) =>
          JsSuccess(new DateTime(num.longValue()))
        case JsString(dstr) ⇒
          try {
            JsSuccess(isoFmt.parseDateTime(dstr))
          } catch {
            case _: Throwable ⇒
              JsError("Bad Format - Joda DateTime")
          }
        case _ =>
          JsError(s"Bad format-$v")
      }
    }

    def writes(v: DateTime): JsValue = {
      JsString(v.toString(isoFmt))
    }
  }

  implicit val sqlDateFormat: Format[Date] = new Format[java.sql.Date] {
    def reads(v: JsValue): JsResult[java.sql.Date] = {
      v match {
        case JsNumber(num) =>
          JsSuccess(new Date(num.longValue()))
        case _ =>
          JsError("Bad format")
      }
    }

    def writes(v: Date): JsValue = {
      JsNumber(v.getTime)
    }
  }

  implicit val sqlTimestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def reads(v: JsValue): JsResult[Timestamp] = {
      v match {
        case JsNumber(num) =>
          JsSuccess(new Timestamp(num.longValue()))
        case _ =>
          JsError("Bad format")
      }
    }

    def writes(v: Timestamp): JsValue = {
      JsNumber(v.getTime)
    }
  }
}