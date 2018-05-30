package dao

import dao.CustomColumnTypes._
import com.google.inject.Inject
import models.Link
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait LinkComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  final class LinkTable(tag: Tag) extends Table[Link](tag, "Link") {

    def linkno = column[Int]("LINKNO", O.PrimaryKey, O.AutoInc)

    def customerno = column[Int]("CUSTOMERNO")

    def linkname = column[String]("LINKNAME")

    def linktel = column[Option[String]]("LINKTEL")

    def creator = column[Option[Int]]("CREATOR")

    def createtime = column[Option[DateTime]]("CREATETIME")

    def updater = column[Option[Int]]("UPDATER")

    def updatetime = column[Option[DateTime]]("UPDATETIME")


    def * = (linkno, customerno, linkname, linktel, creator, createtime, updater, updatetime).shaped <>
      ( {
        case (linkno, customerno, linkname, linktel, creator, createtime, updater, updatetime) =>
          Link(linkno, customerno, linkname, linktel, creator, createtime, updater, updatetime)
      }, { l: Link =>
        Some((l.LinkNo, l.CustomerNo, l.LinkName, l.LinkTel, l.Creator, l.CreateTime, l.Updater, l.UpdateTime))
      })
  }

}

class LinkDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                        implicit val ec: ExecutionContext) extends LinkComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private lazy val links = TableQuery[LinkTable]

  def createTable(): Unit = {
    db.run(
      links.schema.create
    )
  }

  def add(l: Link): Future[Int] = {
    db.run(
      links += l
    )
  }

  def addInBatch(ls: Seq[Link]): Future[Int] = {
    db.run(
      links ++= ls
    ).map(_ => ls.length)
  }

  def updateInBatch(ls: Seq[Link]): Future[Unit] = {
    db.run(
      DBIO.seq(
        ls.map {
          l =>
            links.filter(_.linkno === l.LinkNo).update(l)
        }: _*
      ).transactionally
    )
  }

  def update(l: Link): Future[Int] = {
    db.run(
      links.filter(_.linkno === l.LinkNo).update(l)
    )
  }

  def delete(lNo: Int): Future[Int] = {
    db.run(
      links.filter(_.linkno === lNo).delete
    )
  }

  def deleteByCNo(cNo:Int):Future[Int]={
    db.run(
      links.filter(_.customerno === cNo).delete
    )
  }

  def get(lNo: Int): Future[Option[Link]] = {
    db.run(
      links.filter(_.linkno === lNo).result
    ) map (_.headOption)
  }
}
