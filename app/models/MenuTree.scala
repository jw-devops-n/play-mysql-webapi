package models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/18. 
  */
case class MenuTree(
                     var Menu: Option[Menu] = None,
                     var Children: Seq[MenuTree] = Nil
                   )

object MenuTree {
  implicit val metroMenuTreeFormat: OFormat[MenuTree] = Json.format[MenuTree]
}