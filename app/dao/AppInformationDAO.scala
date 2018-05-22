package dao

import dao.CustomColumnTypes._
import com.google.inject.Inject
import models.AppInformation
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

/**
  * Created by TallSafe on 2018/5/22. 
  */
trait AppInformationComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  final class AppInformationTable(tag: Tag) extends Table[AppInformation](tag, "AppInformation") {

    def appid = column[Int]("APPID", O.PrimaryKey, O.AutoInc)

    def serveid = column[Int]("SERVEID")

    def name = column[Option[String]]("NAME")

    def apptype = column[Option[String]]("apptype")

    def path = column[Option[String]]("path")

    def remark = column[Option[String]]("REMARK")

    def creator = column[Option[Int]]("CREATOR")

    def createtime = column[Option[DateTime]]("CREATETIME")

    def updater = column[Option[Int]]("UPDATER")

    def updatetime = column[Option[DateTime]]("UPDATETIME")


    def * = (appid, serveid, name, apptype, path, remark, creator, createtime, updater, updatetime).shaped <>
      ( {
        case (appid, serveid, name, apptype, path, remark, creator, createtime, updater, updatetime) =>
          AppInformation(appid, serveid, name, apptype, path, remark, creator, createtime, updater, updatetime)
      }, { a: AppInformation =>
        Some((a.AppId, a.ServerId, a.Name, a.AppType, a.Path, a.Remark, a.Creator, a.CreateTime, a.Updater, a.UpdateTime))
      })
  }

}

class AppInformationDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                  implicit val ec: ExecutionContext) extends AppInformationComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private lazy val apps = TableQuery[AppInformationTable]

  def createTable(): Unit = {
    db.run(
      apps.schema.create
    )
  }
}
