package adeln.json

import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmallJsonBenchmark {

  @Test
  fun jackson() {
    smallJson(mkJackson(), "jackson")
  }

  @Test
  fun moshi() {
    smallJson(mkMoshi(), "moshi")
  }

  @Test
  fun gson() {
    smallJson(mkGson(), "gson")
  }
}
