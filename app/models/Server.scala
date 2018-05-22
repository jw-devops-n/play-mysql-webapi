package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by TallSafe on 2018/5/22. 
  */
case class Server(
                   var ServeId: Int = 0,
                   var Name: Option[String] = None,
                   var Ip: Option[String] = None,
                   var LoginName: Option[String] = None,
                   var PassWord: Option[String] = None,
                   var OperationSystem: Option[String] = None,
                   var Image: Option[String] = None,
                   var Unit: Option[String] = None,
                   var Reserve: Option[String] = None,
                   var Reserve1: Option[String] = None,
                   var Remark: Option[String] = None,
                   var Creator: Option[Int] = None,
                   var CreateTime: Option[DateTime] = None,
                   var Updater: Option[Int] = None,
                   var UpdateTime: Option[DateTime] = None
                 )

object Server {
  implicit val serverFormat: OFormat[Server] = Json.format[Server]

}
