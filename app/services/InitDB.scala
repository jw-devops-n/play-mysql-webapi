package services

import java.time.{Clock, Instant}

import dao._
import javax.inject._
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by zjw on 2017/12/22.
  */
class InitDB @Inject()(
                        customerDAO: CustomerDAO,
                        employeeDAO: EmployeeDAO,
                        historyDAO: HistoryDAO,
                        linkDAO: LinkDAO,
                        projectDAO: ProjectDAO,
                        workTimeDAO: WorkTimeDAO,
                        menuDAO: MenuDAO,
                        serverDAO: ServerDAO,
                        appInformationDAO: AppInformationDAO,
                        clock: Clock,
                        appLifecycle: ApplicationLifecycle,
                        implicit val ec: ExecutionContext
                      ) {
  private val start: Instant = clock.instant

  Logger.info(s"ApplicationTimer demo: Starting application at $start.")

  customerDAO.createTable()
  employeeDAO.createTable()
  employeeDAO.addAdmin()
  historyDAO.createTable()
  linkDAO.createTable()
  projectDAO.createTable()
  workTimeDAO.createTable()
  menuDAO.createTable()
  serverDAO.createTable()
  appInformationDAO.createTable()

  appLifecycle.addStopHook { () =>
    val stop: Instant = clock.instant
    val runningTime: Long = stop.getEpochSecond - start.getEpochSecond
    Logger.info(s"ApplicationTimer demo: Stopping application at ${clock.instant} after ${runningTime}s.")
    Future.successful(())
  }

}

