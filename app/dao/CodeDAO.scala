package dao

import com.google.inject.Inject
import models.Code
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by TallSafe on 2018/5/31. 
  */
trait CodeComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  final class CodeTable(tag: Tag) extends Table[Code](tag, "Code") {

    import dao.CustomColumnTypes._

    def syscode = column[String]("SYSCODE")

    def modcode = column[String]("MODCODE")

    def key = column[String]("KEY")

    def description = column[String]("DESCRIPTION")

    def point01 = column[Option[Double]]("POINT01")

    def point02 = column[Option[Double]]("POINT02")

    def string01 = column[Option[String]]("STRING01")

    def string02 = column[Option[String]]("STRING02")

    def date01 = column[Option[DateTime]]("DATE01")

    def date02 = column[Option[DateTime]]("DATE02")

    def creator = column[Option[Int]]("CREATOR")

    def createtime = column[Option[DateTime]]("CREATETIME")

    def updater = column[Option[Int]]("UPDATER")

    def updatetime = column[Option[DateTime]]("UPDATETIME")

    def pk = primaryKey("code_pk", (syscode, modcode, key))

    def * = (syscode, modcode, key, description, point01, point02, string01, string02, date01, date02, creator, createtime, updater, updatetime).shaped <>
      ( {
        case (syscode, modcode, key, description, point01, point02, string01, string02, date01, date02, creator, createtime, updater, updatetime) =>
          Code(syscode, modcode, key, description, point01, point02, string01, string02, date01, date02, creator, createtime, updater, updatetime)
      }, { c: Code =>
        Some((c.SysCode, c.ModCode, c.Key, c.Description, c.Point01, c.Point02, c.String01, c.String02, c.Date01, c.Date02, c.Creator, c.CreateTime, c.Updater, c.UpdateTime))
      })
  }

}

class CodeDAO @Inject()(
                         protected val dbConfigProvider: DatabaseConfigProvider,
                         implicit val ec: ExecutionContext) extends CodeComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private lazy val codes = TableQuery[CodeTable]

  def createTable(): Future[Unit] = {
    db.run(codes.schema.create)
  }

  def checkExistence(syscode: String, modcode: String, key: String): Future[Boolean] = {
    db.run(codes.filter(c => c.syscode === syscode && c.modcode === modcode && c.key === key).exists.result)
  }

  def findAll(): Future[Seq[Code]] = {
    db.run(
      codes.result
    )
  }
}
