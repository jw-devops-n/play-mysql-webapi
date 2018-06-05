package dao

import dao.CustomColumnTypes._
import com.google.inject.Inject
import models.{Employee, WorkTime, WorkTimeEntity}
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
  with ProjectComponent with EmployeeComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private lazy val worktimes = TableQuery[WorkTimeTable]

  private lazy val projects = TableQuery[ProjectTable]

  private lazy val employees = TableQuery[EmployeeTable]

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

  def listByUid(uid: Int): Future[Seq[WorkTimeEntity]] = {
    db.run(
      (
        worktimes.filter(_.empno === uid)
          join projects on (_.prono === _.prono)
        ).result
    ).map {
      ss =>
        ss.map {
          s =>
            WorkTimeEntity(s._2.ProName, None, s._1)
        }
    }
  }

  def list: Future[Seq[WorkTimeEntity]] = {
    db.run(
      (
        worktimes
          join projects on (_.prono === _.prono)
          joinLeft employees on (_._1.empno === _.empno)
        ).result
    ).map {
      ss =>
        ss.map {
          s =>
            WorkTimeEntity(s._1._2.ProName, s._2.getOrElse(Employee()).EmpName, s._1._1)
        }
    }
  }
}
