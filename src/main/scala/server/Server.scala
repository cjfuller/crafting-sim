package server

import com.typesafe.scalalogging.Logger
import craftingsim.OptimizeRequest
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

  get("/*") {
    contentType = "text/html"
    Ok(loadedHtml)
  }
  get("/api/*") {
    NotFound("Not found")
  }
  get("/api/v0/items/:cls") {
    contentType = "application/json"
    val cls   = params("cls")
    val items = data.ItemReader.itemsByClass(cls).keys.toList.sorted
    Ok(items)
  }
  post("/api/v0/optimize") {
    contentType = "text/plain"
    val body = parse(request.body)
    val data = body.extract[OptimizeRequest]
    Ok(Main.run(data))
  }
  get("/elm.js") {
    contentType = "text/javascript"
    Source
      .fromResource("elm.js")
      .mkString
    // Uncomment for development to avoid having to restart the server.
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
      InternalServerError("Server error")
  }
}
