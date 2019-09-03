package io.funfun.redbook

sealed class Input
object Coin : Input()
object Turn : Input()

data class Machine(val locked: Boolean, val candies: Int, val coins: Int)

fun simulateMachine(
    inputs: List<Input>
): StateClass<Machine, Pair<Int, Int>> {
    return StateClass { m ->
        val calculateMachine = inputs.fold(m) { acc, input ->
            when (input) {
                is Coin -> {
                    when {
                        acc.candies <= 0 || !acc.locked -> acc
                        else -> Machine(locked = false, candies = acc.candies, coins = acc.coins + 1)
                    }
                }
                is Turn -> {
                    when {
                        acc.candies <= 0 || acc.locked -> acc
                        else -> Machine(locked = true, candies = acc.candies - 1, coins = acc.coins)
                    }
                }
            }.also { println("result : $it") }
        }

        Pair(calculateMachine.candies, calculateMachine.coins) to calculateMachine
    }
}


fun main() {
    simulateMachine(
        listOf(Coin, Coin, Coin, Turn, Coin, Turn, Turn, Turn, Coin, Coin, Coin, Turn, Coin, Coin, Coin, Turn, Coin, Turn, Coin)
    ).run(Machine(true, 5, 10)).also { println(it) }
}