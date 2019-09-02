package server
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import org.eclipse.jetty.server.Slf4jRequestLog

object ServerMain {
  def main(args: Array[String]) {
    val port =
      if (System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server  = new Server(port)
    val context = new WebAppContext()
    context setContextPath "/"
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")
    context.setResourceBase("src/main/webapp")

    server.setHandler(context)
    val logger = new Slf4jRequestLog()
    logger.setLoggerName("craftingsim.request")
    logger.setLogTimeZone("UTC")
    server.setRequestLog(logger)

    server.start
    server.join
  }
}
