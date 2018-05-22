package dao

import com.google.inject.Inject
import dao.CustomColumnTypes._
import models.Server
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

/**
  * Created by TallSafe on 2018/5/22. 
  */
trait ServerComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  final class ServerTable(tag: Tag) extends Table[Server](tag, "Server") {

    def serveid = column[Int]("SERVEID", O.PrimaryKey, O.AutoInc)

    def name = column[Option[String]]("NAME")

    def ip = column[Option[String]]("IP")

    def loginname = column[Option[String]]("LOGINNAME")

    def password = column[Option[String]]("PASSWORD")

    def operationsystem = column[Option[String]]("OPERATIONSYSTEM")

    def image = column[Option[String]]("IMAGE")

    def unit = column[Option[String]]("UNIT")

    def reserve = column[Option[String]]("RESERVE")

    def reserve1 = column[Option[String]]("RESERVE1")

    def remark = column[Option[String]]("REMARK")

    def creator = column[Option[Int]]("CREATOR")

    def createtime = column[Option[DateTime]]("CREATETIME")

    def updater = column[Option[Int]]("UPDATER")

    def updatetime = column[Option[DateTime]]("UPDATETIME")


    def * = (serveid, name, ip, loginname, password, operationsystem, image, unit, reserve, reserve1, remark, creator, createtime, updater, updatetime).shaped <>
      ( {
        case (serveid, name, ip, loginname, password, operationsystem, image, unit, reserve, reserve1, remark, creator, createtime, updater, updatetime) =>
          Server(serveid, name, ip, loginname, password, operationsystem, image, unit, reserve, reserve1, remark, creator, createtime, updater, updatetime)
      }, { s: Server =>
        Some((s.ServeId, s.Name, s.Ip, s.LoginName, s.PassWord, s.OperationSystem, s.Image, s.Unit, s.Reserve, s.Reserve1, s.Remark, s.Creator, s.CreateTime, s.Updater, s.UpdateTime))
      })
  }

}

class ServerDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                          implicit val ec: ExecutionContext) extends ServerComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private lazy val servers = TableQuery[ServerTable]

  def createTable(): Unit = {
    db.run(
      servers.schema.create
    )
  }
}