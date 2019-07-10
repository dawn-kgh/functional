package io.seroo

import java.lang.IllegalArgumentException

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

fun <T> T.println() {
    kotlin.io.println(this)
}

fun main() {
    val list: FunList<Int> = FunList.Cons(1, FunList.Cons(2, FunList.Cons(3, FunList.Cons(4, FunList.Cons(5, FunList.Nil)))))
    val list2: FunList<Double> = FunList.Cons(1.0, FunList.Cons(2.0, FunList.Cons(0.0, FunList.Cons(4.0, FunList.Cons(5.0, FunList.Nil)))))

    list.sum().println()
    list.drop(3).println()
    list.dropWhile { 3 > it }.println()
    list.init().println()
    list.sum2().println()
    list.sum3().println()

    list2.product().println()
    list2.product2().println()
    list2.product3().println()

    list.length().println()

    list.reverse().println()
}
