package adeln.json.benchmarks

import adeln.json.storage.inTransaction
import adeln.json.storage.mkDb
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmallStorageBenchmark {

    val ctx: Context = InstrumentationRegistry.getTargetContext()

    @Test
    fun sqlite() {
        mkDb(ctx).writableDatabase.inTransaction {
            repeat(100) {

            }
        }
    }
}
