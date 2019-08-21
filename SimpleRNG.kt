package io.funfun.redbook

interface RNG {
    fun nextInt(): Pair<Int, RNG>
}

class SimpleRNG(val seed: Long) : RNG {
    override fun nextInt(): Pair<Int, RNG> {
        val newSeed = (seed * 0x5DEECE66DL + 0xBL) and 0xFFFFFFFFFFFFL
        val nextRNG = SimpleRNG(newSeed)
        val n = (newSeed shr 16).toInt()
        return n to nextRNG
    }
}

fun randomPair(rng: RNG): Pair<Int, Int> {
    val (i1) = rng.nextInt()
    val (i2) = rng.nextInt()

    return i1 to i2
}

fun randomPair2(rng: RNG): Pair<Pair<Int, Int>, RNG> {
    val (i1, rng2) = rng.nextInt()
    val (i2, rng3) = rng2.nextInt()
    return ((i1 to i2) to rng3)
}

fun noneNegativeInt(rng: RNG): Pair<Int, RNG> {
    val (n1, rng2) = rng.nextInt()

    return when {
        n1 >= 0 -> n1
        else -> -(n1 + 1)
    } to rng2
}

fun double(rng: RNG): Pair<Double, RNG> {
    val (n1, rng2) = noneNegativeInt(rng)
    return (n1.toDouble() / (Int.MAX_VALUE.toDouble() + 1)) to rng2
}

fun intDouble(rng: RNG): Triple<Int, Double, RNG> {
    val (n1, r1) = rng.nextInt()
    val (n2, r2) = double(r1)

    return Triple(n1, n2, r2)
}

fun doubleInt(rng: RNG): Triple<Double, Int, RNG> {
    val (n1, r1) = double(rng)
    val (n2, r2) = r1.nextInt()

    return Triple(n1, n2, r2)
}

fun double3(rng: RNG): Pair<Triple<Double, Double, Double>, RNG> {
    val (n1, r1) = double(rng)
    val (n2, r2) = double(r1)
    val (n3, r3) = double(r2)

    return Triple(n1, n2, n3) to r3
}

tailrec fun ints(count: Int, rng: RNG, acc: List<Int> = emptyList()): Pair<List<Int>, RNG> =
    when (count == 0) {
        true -> acc to rng
        false -> {
            val (n, nextRNG) = rng.nextInt()
            ints(count - 1, nextRNG, acc + n)
        }
    }

val int: Rand<Int> = {
    it.nextInt()
}

//typealias Rand<T> = (RNG) -> Pair<T, RNG>
typealias Rand<T> = TypeState<RNG, T>

fun <A> unit(a: A): Rand<A> = ClassState<RNG, A> {
    a to it
}.run

fun <A, B> map(s: Rand<A>, f: (A) -> B): Rand<B> = ClassState<RNG, B> {
    val (a, rng2) = s(it)
    Pair(f(a), rng2)
}.run

fun noneNegativeEven(): Rand<Int> = map(::noneNegativeInt) { i ->
    i - i % 2
}

fun elegantDouble(): Rand<Double> = map(::noneNegativeInt) { i ->
    (i.toDouble() / (Int.MAX_VALUE.toDouble() + 1))
}

fun <A, B, C> map2(ra: Rand<A>, rb: Rand<B>, f: (A, B) -> C): Rand<C> = ClassState<RNG, C> { rng ->
    val (a, rng2) = ra(rng)
    val (b, rng3) = rb(rng2)
    f(a, b) to rng3
} .run

fun <A, B> both(ra: Rand<A>, rb: Rand<B>): Rand<Pair<A, B>> =
    map2(ra, rb) { a, b ->
        a to b
    }

val randIntDoule: Rand<Pair<Int, Double>> = both(int, ::double)
val randDoubleInt: Rand<Pair<Double, Int>> = both(::double, int)

fun <A> sequence(fs: List<Rand<A>>): Rand<List<A>> = fs.foldRight(unit(listOf())) { i: Rand<A>, acc: Rand<List<A>> ->
    map2(i, acc) { a, list ->
        list + a
    }
}

fun noneNegativeLessThan(n: Int): Rand<Int> = { rng ->
    val (i, rng2) = noneNegativeInt(rng)
    val mod = i % n
    if (i + (n - 1) - mod >= 0) {
        mod to rng2
    } else {
        noneNegativeLessThan(n)(rng2)
    }
}

fun <A, B> flatMap(f: Rand<A>, g: (A) -> Rand<B>): Rand<B> = { rng ->
    val (n, rng2) = f(rng)
    g(n)(rng2)
}

fun noneNegativeLessThan2(n: Int): Rand<Int> = flatMap(::noneNegativeInt) { i ->
    val mod = i % n
    if (i + (n - 1) - mod >= 0) unit(mod) else noneNegativeLessThan2(n)
}

fun <A, B> mapByFlatMap(s: Rand<A>, f: (A) -> B): Rand<B> = flatMap(s) {
    unit(f(it))
}

fun <A, B, C> map2ByFlatMap(ra: Rand<A>, rb: Rand<B>, f: (A, B) -> C): Rand<C> = flatMap(
    ra
) { a ->
    flatMap(rb) { b ->
        unit(f(a, b))
    }
}

fun <S, A, B> map3(a: (S) -> Pair<A, S>, f: (A) -> B): (S) -> Pair<B, S> = {
    val (a, s) = a(it)
    val b = f(a)
    b to s
}

data class ClassState<S, out A>(val run: (S) -> Pair<A, S>)

typealias TypeState<S, A> = (S) -> Pair<A, S>

fun main() {
    val rng = SimpleRNG(42)

    val (n1, rng2) = rng.nextInt()
    val (n2, rng3) = rng2.nextInt()

    println("n1 : $n1, n2: $n2")

    println("pair : ${randomPair(rng)}")
    println("pair2 : ${randomPair2(rng)}")
    println("pair3 : ${noneNegativeInt(rng)}")
    println("pair4 : ${double(rng)}")
}