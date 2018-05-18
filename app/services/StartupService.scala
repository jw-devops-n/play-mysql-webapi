package services

import java.time.Clock
import com.google.inject.AbstractModule

/**
  * Created by zjw on 2017/12/22.
  */
class StartupService extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    bind(classOf[InitDB]).asEagerSingleton()
  }
}
