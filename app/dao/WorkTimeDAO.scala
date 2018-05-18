package dao

import dao.CustomColumnTypes._
import com.google.inject.Inject
import models.WorkTime
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait WorkTimeComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  final class WorkTimeTable(tag: Tag) extends Table[WorkTime](tag, "WorkTime") {

    def empno = column[Int]("EMPNO")

    def prono = column[String]("PRONO")

    def workdate = column[DateTime]("WORKDATE")

    def worktime = column[Double]("WORKTIME")

    def overtime = column[Option[Double]]("OVERTIME")

    def remark = column[Option[String]]("REMARK")

    def creator = column[Option[Int]]("CREATOR")

    def createtime = column[Option[DateTime]]("CREATETIME")

    def updater = column[Option[Int]]("UPDATER")

    def updatetime = column[Option[DateTime]]("UPDATETIME")


    def * = (empno, prono, workdate, worktime, overtime, remark, creator, createtime, updater, updatetime).shaped <>
      ( {
        case (empno, prono, workdate, worktime, overtime, remark, creator, createtime, updater, updatetime) =>
          WorkTime(empno, prono, workdate, worktime, overtime, remark, creator, createtime, updater, updatetime)
      }, { w: WorkTime =>
        Some((w.EmpNo, w.ProNo, w.WorkDate, w.WorkTime, w.OverTime, w.Remark, w.Creator, w.CreateTime, w.Updater, w.UpdateTime))
      })
  }

}

class WorkTimeDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                            implicit val ec: ExecutionContext) extends WorkTimeComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private lazy val worktimes = TableQuery[WorkTimeTable]

  def createTable(): Unit = {
    db.run(
      worktimes.schema.create
    )
  }

  def add(w: WorkTime): Future[Int] = {
    db.run(
      worktimes += w
    )
  }

  def get(proNo: String, empNo: Int, wDate: DateTime): Future[Option[WorkTime]] = {
    import dao.CustomColumnTypes._
    db.run(
      worktimes.filter(
        wt => wt.prono === proNo
          && wt.empno === empNo
          && wt.workdate === wDate
      ).result
    ).map(_.headOption)
  }

  def update(w: WorkTime): Future[Int] = {
    import dao.CustomColumnTypes._
    db.run(
      worktimes.filter(
        wt => wt.prono === w.ProNo
          && wt.empno === w.EmpNo
          && wt.workdate === w.WorkDate).update(w)
    )
  }

  def delete(cNo: Int): Future[Int] = {
    db.run(
      worktimes.delete
    )
  }
}
