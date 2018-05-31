package models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/30. 
  */
case class CustomerComBox(
                           var CustomerNo: Int=0,
                           var CustomerName: String="",
                         )

object CustomerComBox {
  implicit val customerComBoxFormat: OFormat[CustomerComBox] = Json.format[CustomerComBox]
}
