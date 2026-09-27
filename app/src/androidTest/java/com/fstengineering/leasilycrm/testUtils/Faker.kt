package com.fstengineering.leasilycrm.testUtils

import kotlin.random.Random

val faker = FakerInstance

object FakerInstance {
    val random = FakerRandom
}

object FakerRandom {
    fun nextInt(bound: Int = Int.MAX_VALUE): Int = Random.nextInt(bound)
    fun nextLong(bound: Long = Long.MAX_VALUE): Long = Random.nextLong(bound)
    fun nextUUID(): String = java.util.UUID.randomUUID().toString()
}
