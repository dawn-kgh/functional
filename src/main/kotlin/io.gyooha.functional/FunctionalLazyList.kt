package io.gyooha.functional

sealed class Stream<out T> {
    data class Cons<out T>(val head: () -> T, val tail: () -> Stream<T>) : Stream<T>()
    object Empty : Stream<Nothing>()

    companion object {
        fun <T> cons(head: () -> T, tail: () -> Stream<T>): Stream<T> {
            val hd by lazy {
                head
            }

            val tl by lazy {
                tail
            }

            return Cons(hd, tail)
        }
    }
}

fun <T> empty(): Stream<T> = Stream.Empty

fun <T> List<T>.apply2(): Stream<T> {
    return if (isEmpty()) empty() else Stream.cons({ this[0] }, { drop(1).apply2() })
}

fun <T, R> Stream<T>.foldRight(acc: () -> R, f: (T, () -> R) -> R): R = when (this) {
    is Stream.Cons -> {
        f(head()) { tail().foldRight(acc, f) }
    }
    is Stream.Empty -> acc()
}

fun <T> Stream<T>.reverse(): Stream<T> {
    return foldRight({ Stream.Empty }) { i: T, acc: () -> Stream<T> ->
        Stream.Cons({ i }, acc)
    }
}

fun <T, R> Stream<T>.map(transformer: (T) -> R): Stream<R> =
        foldRight({ Stream.Empty }) { i: T, acc: () -> Stream<R> ->
            Stream.Cons({ transformer(i) }, acc)
        }

fun <T> Stream<T>.toList() =
        foldRight({ listOf() }) { i: T, acc: () -> List<T> ->
            acc().plus(i)
        }.reversed()

fun <T> Stream<T>.filter(predicate: (T) -> Boolean): Stream<T> {
    return foldRight({ Stream.Empty }) { i: T, acc: () -> Stream<T> ->
        if (predicate(i)) {
            Stream.Cons({ i }, acc)
        } else {
            acc()
        }
    }
}

fun <T> append(firstList: Stream<T>, targetList: () -> Stream<T>) =
        firstList.foldRight(targetList) { i: T, acc: () -> Stream<T> ->
            Stream.Cons({ i }, acc)
        }

fun <T> Stream<Stream<T>>.flatten() =
        foldRight({ Stream.Empty }, ::append)

fun <T, R> Stream<T>.flatMap(transformer: (T) -> Stream<R>): Stream<R> =
        map(transformer).flatten()

fun <T> T.println() {
    println(this)
}