package service

import java.util.concurrent.{Executors, TimeUnit}
import javax.inject.{Inject, Singleton}

import config.ImportScheduleConfig
import org.slf4j.LoggerFactory
import play.api.inject.ApplicationLifecycle
import service.provider.AdvertImporter

import scala.concurrent.Future

@Singleton
class ImportSchedule @Inject()(importScheduleConfig: ImportScheduleConfig,
                               advertImporter: AdvertImporter,
                               lifecycle: ApplicationLifecycle) {

  val logger = LoggerFactory.getLogger(classOf[ImportSchedule])
  val executor = Executors.newSingleThreadScheduledExecutor()
  lifecycle.addStopHook(() => {
    Future.successful(executor.shutdown())
  })

  val runnable = new Runnable {
    override def run() = {
      try {
        advertImporter.runImport()
      } catch {
        case e: Exception => logger.error("Import failure", e)
      }
    }
  }

  logger.info("Scheduled import with {} {}", importScheduleConfig.initialDelay, importScheduleConfig.interval)
  executor.scheduleWithFixedDelay(runnable, importScheduleConfig.initialDelay, importScheduleConfig.interval, TimeUnit.SECONDS)

}
