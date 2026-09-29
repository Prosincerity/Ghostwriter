package com.prosincerity.ghostwriter.media

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackNotificationUpdaterTest {
    @Test
    fun burstPublishesLatestButtonsOnce() {
        val queue = TestQueue()
        var buttons = "Play, Restart, Disable loop"
        val published = mutableListOf<String>()
        val updater = PlaybackNotificationUpdater(queue::schedule, queue::remove) { published += buttons }

        updater.request()
        buttons = "Pause, Restart, Disable loop"
        updater.request()
        buttons = "Pause, Restart, Enable loop"
        updater.request()

        queue.advanceTo(499)
        assertTrue(published.isEmpty())
        queue.advanceTo(500)
        assertEquals(listOf("Pause, Restart, Enable loop"), published)
        assertTrue(queue.tasks.isEmpty())
    }

    @Test
    fun continuousChangesDoNotStarveUpdatesOrExceedTwoPostsPerSecond() {
        val queue = TestQueue()
        var state = 0
        val published = mutableListOf<Pair<Long, Int>>()
        val updater = PlaybackNotificationUpdater(queue::schedule, queue::remove) { published += queue.now to state }

        repeat(100) {
            queue.advanceTo(it * 20L)
            state = it
            updater.request()
        }
        queue.advanceTo(2_000)

        assertEquals(listOf(500L, 1_000L, 1_500L, 2_000L), published.map { it.first })
        assertEquals(99, published.last().second)
    }

    @Test
    fun cancellationPreventsNotificationFromReturningAfterPlaybackIsReleased() {
        val queue = TestQueue()
        var posts = 0
        val updater = PlaybackNotificationUpdater(queue::schedule, queue::remove) { posts++ }

        updater.request()
        updater.cancel()
        queue.advanceTo(1_000)

        assertEquals(0, posts)
        assertTrue(queue.tasks.isEmpty())
    }

    @Test
    fun newPlaybackCanScheduleAfterPendingUpdateIsCancelled() {
        val queue = TestQueue()
        var posts = 0
        val updater = PlaybackNotificationUpdater(queue::schedule, queue::remove) { posts++ }

        updater.request()
        queue.advanceTo(100)
        updater.cancel()
        updater.request()
        queue.advanceTo(500)
        assertEquals(0, posts)
        queue.advanceTo(600)
        assertEquals(1, posts)
    }

    private class TestQueue {
        var now = 0L
        val tasks = mutableMapOf<Runnable, Long>()

        fun schedule(task: Runnable, delay: Long) { tasks[task] = now + delay }
        fun remove(task: Runnable) { tasks.remove(task) }

        fun advanceTo(time: Long) {
            while (true) {
                val next = tasks.minByOrNull { it.value }?.takeIf { it.value <= time } ?: break
                now = next.value
                tasks.remove(next.key)
                next.key.run()
            }
            now = time
        }
    }
}
