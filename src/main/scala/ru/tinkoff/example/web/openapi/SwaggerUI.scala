package ru.tinkoff.example.web.openapi

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}
import scalatags.Text.all._

case class SwaggerUISettings(title: String, pathPrefix: String, swaggerSchemePostfix: String)

trait SwaggerUI extends Directives {

  private def swaggerIndex(html: String) = complete(
    HttpEntity(
      contentType = ContentTypes.`text/html(UTF-8)`,
      string = html
    )
  )
  private val webJars = get(getFromResourceDirectory("META-INF/resources/webjars"))

  def swaggerUIRoute(settings: SwaggerUISettings): Route = concat(
    path("swagger")(swaggerIndex(SwaggerIndex.index(settings))),
    pathPrefix("webjars")(webJars)
  )
}

private object SwaggerIndex {

  // TODO: Get from build info
  private val swaggerVersion = "3.20.8"

  def index(settings: SwaggerUISettings): String = {

    def webjar(s: String) = s"${settings.pathPrefix}/webjars/swagger-ui-dist/$swaggerVersion/$s"

    html(
      meta(charset := "UTF-8"),
      tag("title")(settings.title),
      link(
        rel := "stylesheet",
        href := "https://fonts.googleapis.com/css?family=Open+Sans:400,700|Source+Code+Pro:300,600|Titillium+Web:400,600,700"
      ),
      link(
        rel := "stylesheet",
        href := webjar("swagger-ui.css")
      ),
      tag("style")(style),
      body(
        div(id := "swagger-ui"),
        script(src := webjar("swagger-ui-bundle.js")),
        script(src := webjar("swagger-ui-standalone-preset.js")),
        script(onLoad(settings.pathPrefix + settings.swaggerSchemePostfix))
      )
    ).render
  }

  private def style = raw("""
                            |html {
                            |  box-sizing: border-box;
                            |  overflow: -moz-scrollbars-vertical;
                            |  overflow-y: scroll;
                            |}
                            |
                            |*,
                            |*:before,
                            |*:after {
                            |  box-sizing: inherit;
                            |}
                            |
                            |body {
                            |  margin:0;
                            |  background: #fafafa;
                            |}
                            |
      """.stripMargin)

  private def onLoad(swaggerScheme: String) = raw(s"""
                                               |window.onload = function () {
                                               |  SwaggerUIBundle({
                                               |    url: "$swaggerScheme",
                                               |    dom_id: '#swagger-ui',
                                               |    deepLinking: true,
                                               |    presets: [
                                               |      SwaggerUIBundle.presets.apis,
                                               |      SwaggerUIStandalonePreset
                                               |    ],
                                               |    plugins: [
                                               |      SwaggerUIBundle.plugins.DownloadUrl
                                               |    ],
                                               |      layout: "StandaloneLayout"
                                               |  });
                                               |};
      """.stripMargin)
}
