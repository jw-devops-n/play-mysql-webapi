package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/10.
  */
case class Customer(
                     var CustomerNo: Int,
                     var CustomerName: String,
                     var CustomerTel: Option[String],
                     var Creator: Option[Int] = None,
                     var CreateTime: Option[DateTime] = None,
                     var Updater: Option[Int] = None,
                     var UpdateTime: Option[DateTime] = None
                   )

object Customer {
  implicit val customerFormat: OFormat[Customer] = Json.format[Customer]
}
