package io.gyooha.functional

sealed class Either<out E, out T>
data class Right<T>(val get: T) : Either<Nothing, T>()
data class Left<E>(val error: E) : Either<E, Nothing>()


fun <E, T, R> Either<E, T>.map(transformer: (T) -> R): Either<E, R> {
    return when (this) {
        is Left -> this
        is Right -> Right(transformer(get))
    }
}

fun <E, T, R> Either<E, T>.flatMap(transformer: (T) -> Either<E, R>): Either<E, R> {
    return when (this) {
        is Left -> this
        is Right -> Right(transformer(get)).flatten()
    }
}

private fun <E, T> Either<E, Either<E, T>>.flatten(): Either<E, T> = when (this) {
    is Left -> this
    is Right -> get
}

fun <E, B, T : B> Either<E, T>.orElse(f: () -> Either<E, B>): Either<E, B> {
    return when (this) {
        is Left -> f()
        is Right -> this
    }
}

fun <E, A, B, C> Either<E, A>.map2(b: Either<E, B>, f: (A, B) -> C): Either<E, C> {
    return when (this) {
        is Left -> this
        is Right -> when (b) {
            is Left -> b
            is Right -> Right(f(get, b.get))
        }
    }
}

fun <T> Either<Exception, T>.Try(f: () -> T): Either<Exception, T> =
        try {
            Right(f())
        } catch (e: Exception) {
            Left(e)
        }

