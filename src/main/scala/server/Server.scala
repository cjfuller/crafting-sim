package server

import com.typesafe.scalalogging.Logger
import craftingsim.OptimizeRequest
import javax.servlet.http.HttpServletResponse
import scala.io.Source
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods.parse
import org.scalatra.ScalatraServlet
import org.scalatra.{Ok, NotFound, InternalServerError}
import org.scalatra.json.NativeJsonSupport
import craftingsim.Main

object Server extends ScalatraServlet with NativeJsonSupport {
  val logger                             = Logger("server")
  implicit lazy val jsonFormats: Formats = DefaultFormats
  lazy val loadedHtml                    = Source.fromResource("main.html").mkString

  def noCache(implicit response: HttpServletResponse) = {
    response.setHeader(
      "Cache-Control",
      "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0"
    )
  }

  def cache(lifetimeSec: Int)(implicit response: HttpServletResponse) = {
    response.setHeader(
      "Cache-Control",
      s"public, max-age=${lifetimeSec}, must-revalidate, proxy-revalidate"
    )
  }

  get("/*") {
    contentType = "text/html"
    cache(300)
    Ok(loadedHtml)
  }
  get("/api/*") {
    noCache
    NotFound("Not found")
  }
  get("/api/v0/items/:cls") {
    contentType = "application/json"
    cache(86400)
    val cls   = params("cls")
    val items = data.ItemReader.itemsByClass(cls).keys.toList.sorted
    Ok(items)
  }
  post("/api/v0/optimize") {
    contentType = "text/plain"
    noCache
    val body = parse(request.body)
    val data = body.extract[OptimizeRequest]
    Ok(Main.run(data))
  }
  get("/elm.js") {
    contentType = "text/javascript"
    cache(60)
    Source
      .fromResource("elm.js")
      .mkString
    // Uncomment for development to avoid having to restart the server and/or
    // wait for cache.
    // noCache
    // Source
    //   .fromFile("src/main/resources/elm.js")
    //   .mkString
  }

  errorHandler = {
    case (exc: Throwable) =>
      contentType = "text/plain"
      val writer = new java.io.StringWriter()
      exc.printStackTrace(new java.io.PrintWriter(writer))
      logger.error(
        exc.getMessage() + "\n" + writer.toString()
      )
      noCache
      InternalServerError("Server error")
  }
}
