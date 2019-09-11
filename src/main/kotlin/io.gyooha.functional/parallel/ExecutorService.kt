package io.gyooha.functional.parallel

import java.util.concurrent.TimeUnit

class ExecutorService {
    fun <T> submit(a: Callable<T>): Future<T> {
        TODO()
    }
}

interface Callable<T> {
    fun cell(): T
}

interface Future<T> {
    fun get(): T
    fun get(timeOut: Long, unit: TimeUnit): T
    fun cancel(evenIfRunning: Boolean): Boolean
    fun isDone(): Boolean
    fun isCancelled(): Boolean
}