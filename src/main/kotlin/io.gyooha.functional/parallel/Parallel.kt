package io.gyooha.functional.parallel

import java.util.concurrent.TimeUnit

data class Par<T>(private val data: T) {
    companion object {
        fun <T> unit(f: () -> T): Par<T> = Par(f())
        fun <T> run(a: Par<T>): T = a.data
        fun <A, B, C> map2(a: Par<A>, b: Par<B>, f: (A, B) -> C): Par<C> = unit {
            f(
                    run(a),
                    run(b)
            )
        }

        fun <T> fork(f: () -> Par<T>): Par<T> = f()
        fun <T> lazyUnit(f: () -> T): Par<T> = unit(f)
        fun <T> run(s: ExecutorService, a: Par<T>): T = TODO()
        fun <T> run(s: ExecutorService, a: TypePar<T>): Future<T> = a(s)

        fun <T> unit(t: T): TypePar<T> = {
            UnitFuture(t)
        }

        private class UnitFuture<T>(val get: T) : Future<T> {
            override fun get(): T = get

            override fun get(timeOut: Long, unit: TimeUnit): T = get

            override fun cancel(evenIfRunning: Boolean): Boolean = false

            override fun isDone(): Boolean = false

            override fun isCancelled(): Boolean = false
        }

        fun <A, B, C> map2(a: TypePar<A>, b: TypePar<B>, f: (A, B) -> C): TypePar<C> = {
            val af = a(it)
            val bf = b(it)
            UnitFuture(f(af.get(), bf.get()))
        }

        fun <T> fork(f: () -> TypePar<T>): TypePar<T> = {
            it.submit(
                    object : Callable<T> {
                        override fun cell(): T = f()(it).get()
                    }
            )
        }
    }
}

fun sum(ints: Sequence<Int>): Int =
        ints.fold(0) { acc, i ->
            acc + i
        }

fun sum(ints: List<Int>): Par<Int> =
        if (ints.size <= 1) {
            Par.unit {
                ints.getOrElse(0) {
                    0
                }
            }
        } else {
            val halfSize = ints.size / 2
            val (l, r) = ints.partition { it > halfSize }
            /*Par.map2(Par.fork { sum(l) }, Par.fork { sum(r) }) { par1, par2 ->
                par1 + par2
            }*/

            TODO()
        }

typealias TypePar<T> = (ExecutorService) -> Future<T>