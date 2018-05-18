package dao

import dao.CustomColumnTypes._
import com.google.inject.Inject
import models.Menu
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by TallSafe on 2018/5/17. 
  */
trait MenuComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  final class MenuTable(tag: Tag) extends Table[Menu](tag, "Menu") {

    def menuno = column[Int]("MENUNO", O.PrimaryKey, O.AutoInc)

    def code = column[String]("CODE")

    def pcode = column[String]("PCODE")

    def path = column[String]("PATH")

    def name = column[Option[String]]("NAME")

    def icon = column[Option[String]]("ICON")

    def order = column[Option[Int]]("ORDER")

    def isiconvisible = column[Option[Boolean]]("ISICONVISIBLE")

    def creator = column[Option[Int]]("CREATOR")

    def createtime = column[Option[DateTime]]("CREATETIME")

    def updater = column[Option[Int]]("UPDATER")

    def updatetime = column[Option[DateTime]]("UPDATETIME")


    def * = (menuno, code, pcode, path, name, icon, order, isiconvisible, creator, createtime, updater, updatetime).shaped <>
      ( {
        case (menuno, code, pcode, path, name, icon, order, isiconvisible, creator, createtime, updater, updatetime) =>
          Menu(menuno, code, pcode, path, name, icon, order, isiconvisible, creator, createtime, updater, updatetime)
      }, { m: Menu =>
        Some(m.MenuNo, m.Code, m.PCode, m.Path, m.Name, m.Icon, m.Order, m.IsIconVisible, m.Creator, m.CreateTime, m.Updater, m.UpdateTime)
      })
  }

}

class MenuDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                        implicit val ec: ExecutionContext) extends MenuComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private lazy val menus = TableQuery[MenuTable]

  def createTable(): Unit = {
    db.run(
      menus.schema.create
    )
  }

  def add(m: Menu): Future[Int] = {
    db.run(
      menus += m
    )
  }

  def update(m: Menu): Future[Int] = {
    db.run(
      menus.filter(_.menuno === m.MenuNo).update(m)
    )
  }
  def get(mNo: Int): Future[Option[Menu]] = {
    db.run(
      menus.filter(_.menuno === mNo).result
    ).map(_.headOption)
  }

  def delete(mNo: Int): Future[Int] = {
    db.run(
      menus.filter(_.menuno === mNo).delete
    )
  }

  def findAll(): Future[Seq[Menu]] = {
    db.run(menus.result)
  }
}

