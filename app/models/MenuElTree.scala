package models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/23. 
  */
case class MenuElTree(
                       var id: Int = 0,
                       var Name: String = "",
                       var Order:Option[Int] = None,
                       var Children: Seq[MenuElTree] = Nil
                     )

object MenuElTree {
  implicit val menuElTreeFormat: OFormat[MenuElTree] = Json.format[MenuElTree]
}
