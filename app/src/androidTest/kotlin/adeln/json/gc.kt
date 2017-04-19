package adeln.json

import java.io.Closeable
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class GcCounter(val count: AtomicInteger, val thread: Thread) : Closeable {

    override fun close(): Unit =
        thread.interrupt()
}

class Garbage

fun loop(cnt: AtomicInteger) {

    val frameMs = 16L

    val queue = ReferenceQueue<Garbage>()

    var phantom = PhantomReference(Garbage(), queue)

    try {
        while (!Thread.interrupted()) {
            if (queue.remove(frameMs) != null) {
                cnt.incrementAndGet()
                phantom = PhantomReference(Garbage(), queue)
            }
        }
    } catch (_: InterruptedException) {
    }
}

fun startGcCounter(): GcCounter {
    val cnt = AtomicInteger(0)

    val thread = thread { loop(cnt) }

    return GcCounter(cnt, thread)
}
