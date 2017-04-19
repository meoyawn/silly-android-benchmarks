package adeln.json.json

import adeln.json.startGcCounter
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

fun smallJson(parser: Parser, name: String) {
    val smallResponse = MockResponse().setBody(SMALL_JSON)
    val client = OkHttpClient()

    mockWebServer {
        it.setDispatcher(object : Dispatcher() {
            override fun dispatch(p0: RecordedRequest): MockResponse =
                smallResponse
        })

        val req = mkRequest(it.url(""))

        startGcCounter().use { gc ->

            repeat(250) {
                val response = client.newCall(req).execute()

                val parsed = parser(response.body().source())

                check(parsed.results.albummatches.album.isNotEmpty())
            }

            testLog("$name garbage ${gc.count}")
        }
    }
}
