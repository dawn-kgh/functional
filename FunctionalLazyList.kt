sealed class Stream<out T> {
    data class Cons<T>(val head: () -> T, val tail: () -> Stream<T>) : Stream<T>()
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

fun <T> apply(list: List<T>): Stream<T> {
    return if (list.isEmpty()) empty() else Stream.cons({ list[0] }, { apply(list.drop(1)) })
}