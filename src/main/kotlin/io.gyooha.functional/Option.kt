package io.gyooha.functional

sealed class Option<out T>
data class Some<out T>(val get: T) : Option<T>()
object None : Option<Nothing>()

fun <T, R> Option<T>.map(transformer: (T) -> R): Option<R> {
    return when (this) {
        is Some<T> -> Some(transformer(get))
        is None -> None
    }
}

fun <T, R> Option<T>.flatMap(transformer: (T) -> Option<R>): Option<R> {
    return map(transformer).flatten()
}

fun <T> Option<Option<T>>.flatten(): Option<T> {
    return when (this) {
        is Some -> get
        is None -> None
    }
}

fun <T : R, R> Option<T>.getOrElse(default: () -> R): R {
    return when (this) {
        is Some -> get
        is None -> default()
    }
}

fun <KType : R, R> Some<KType>.orElse(default: () -> Option<R>): Option<R> {
    return when (get) {
        is String -> if (get.isEmpty()) default() else this
        is Int -> if (get == 0) default() else this
        else -> None
    }
}

fun <T> Option<T>.filter(predicate: (T) -> Boolean): Option<T> {
    return when (this) {
        is Some<T> -> if (predicate(get)) this else None
        is None -> None
    }
}

fun main() {
    val mapResult = Some(1)
        .map {
            1.toString()
        } //
        .flatMap {
            if (it.isEmpty()) {
                None
            } else {
                Some(it.toInt())
            }
        }
        .flatMap { Some(it + 5) }
        .getOrElse { 0 }

    println(mapResult.toString())
}