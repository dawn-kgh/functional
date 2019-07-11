sealed class FunList<out T> {
    data class Cons<out T>(val head: T, val tail: FunList<T>) : FunList<T>()
    object Nil : FunList<Nothing>()
}

fun FunList<Double>.product(): Double {
    return when (this) {
        is FunList.Nil -> 1.0
        is FunList.Cons -> when (head == 0.0) {
            true -> 0.0
            false -> head * tail.product()
        }
    }
}

fun <T> FunList<T>.setHead(targetValue: T): FunList<T> {
    return when (this) {
        is FunList.Nil -> FunList.Nil
        is FunList.Cons -> FunList.Cons(targetValue, tail)
    }
}

fun <T> FunList<T>.drop(n: Int): FunList<T> {
    return when (this) {
        is FunList.Nil -> FunList.Nil
        is FunList.Cons -> when (n) {
            0 -> this
            else -> tail.drop(n - 1)
        }
    }
}

fun <T> FunList<T>.dropWhile(predicate: (T) -> Boolean): FunList<T> {
    return when (this) {
        is FunList.Nil -> FunList.Nil
        is FunList.Cons -> when (predicate(head)) {
            true -> tail.dropWhile(predicate)
            false -> this
        }
    }
}

fun <T> FunList<T>.init(): FunList<T> {
    return when (val parent = this) {
        is FunList.Nil -> FunList.Nil
        is FunList.Cons -> when (parent.tail) {
            is FunList.Nil -> parent
            is FunList.Cons -> parent.tail.init()
        }
    }
}

fun FunList<Int>.sum(): Int {
    return when (this) {
        is FunList.Nil -> 0
        is FunList.Cons -> head + tail.sum()
    }
}

fun <T> FunList<T>.getTail(): FunList<T> {
    return when (this) {
        is FunList.Nil -> FunList.Nil
        is FunList.Cons -> tail
    }
}

fun <T> FunList<T>.getHead(): T {
    return when (this) {
        is FunList.Nil -> throw IllegalArgumentException("Not Found head")
        is FunList.Cons -> head
    }
}

fun <T, R> FunList<T>.foldRight(acc: R, f: (T, R) -> R): R {
    return when (this) {
        is FunList.Nil -> acc
        is FunList.Cons -> f(head, tail.foldRight(acc, f))
    }
}

tailrec fun <T, R> FunList<T>.foldLeft(acc: R, f: (R, T) -> R): R {
    return when (this) {
        is FunList.Nil -> acc
        is FunList.Cons -> tail.foldLeft(f(acc, head), f)
    }
}

fun FunList<Int>.sum2(): Int {
    return foldRight(0) { i1, i2 ->
        kotlin.io.println("i1 : $i1")
        i1 + i2
    }
}

fun FunList<Int>.sum3(): Int {
    return foldLeft(0) { acc, i ->
        kotlin.io.println("i1 : $i")
        acc + i
    }
}

fun FunList<Double>.product3(): Double {
    return foldLeft(0.0) { acc, i ->
        acc * i
    }
}

fun FunList<Double>.product2(): Double {
    return foldRight(1.0) { d1, d2 ->
        d1 * d2
    }
}

fun <T> FunList<T>.reverse(): FunList<T> {
    return foldLeft(FunList.Nil) { acc: FunList<T>, a ->
        FunList.Cons(a, acc)
    }
}

fun <T, R> FunList<T>.foldRight2(acc: R, f: (R, T) -> R): R {
    return reverse().foldLeft(acc) { _acc, i ->
        f(_acc, i)
    }
}

fun <T> FunList<T>.length(): Int {
    return foldRight(0) { _, i: Int ->
        i + 1
    }
}

fun <T> FunList<T>.append(secondList: FunList<T> = FunList.Nil): FunList<T> {
    return foldRight(secondList) { i, acc ->
        FunList.Cons(i, acc)
    }
}

fun <T> append2(firstList: FunList<T>, secondList: FunList<T>): FunList<T> {
    return firstList.foldRight(secondList) { i, acc ->
        FunList.Cons(i, acc)
    }
}

fun <T> FunList<FunList<T>>.concat(): FunList<T> {
    return foldRight(FunList.Nil, ::append2)
}

fun FunList<Int>.addOne(): FunList<Int> {
    return foldLeft(FunList.Nil) { acc: FunList<Int>, i ->
        FunList.Cons(i + 1, acc)
    }.reverse()
}

fun <T, R> FunList<T>.map(transformer: (T) -> R): FunList<R> {
    return foldLeft(FunList.Nil) { acc: FunList<R>, i ->
        FunList.Cons(transformer(i), acc)
    }.reverse()
}

fun <T> FunList<T>.filter(predicate: (T) -> Boolean): FunList<T> {
    return foldLeft(FunList.Nil) { acc: FunList<T>, i ->
        if (predicate(i)) {
            FunList.Cons(i, acc)
        } else {
            acc
        }
    }
}

fun <T, R> FunList<T>.flatMap(transformer: (T) -> FunList<R>): FunList<R> {
    return map(transformer).concat()
}

tailrec fun FunList<Int>.plus(secondList: FunList<Int>, acc: FunList<Int> = FunList.Nil): FunList<Int> {
    return if (this !is FunList.Nil || secondList !is FunList.Nil) {
        getTail().plus(secondList.getTail(), FunList.Cons(this.getHead() + secondList.getHead(), acc))
    } else {
        acc.reverse()
    }
}

tailrec fun <T, R> FunList<T>.zipWith(secondList: FunList<T>, acc: FunList<R> = FunList.Nil, f: (T, T) -> R): FunList<R> {
    return when {
        this is FunList.Cons && secondList is FunList.Cons -> {
            this.tail.zipWith(secondList.tail, FunList.Cons(f(head, secondList.head), acc), f)
        }
        else -> {
            acc.reverse()
        }
    }
}