package com.rmsftc.utils.math

interface MathOperand<T> {
    fun add(other: T): T
    fun subtract(other: T): T
    fun multiply(other: T): T
    fun divide(other: T): T
    fun remainder(other: T): T
}

@JvmName("plus")
operator fun <T : MathOperand<T>> T.plus(other: T): T {
    return this.add(other)
}

@JvmName("minus")
operator fun <T : MathOperand<T>> T.minus(other: T): T {
    return this.subtract(other)
}

@JvmName("times")
operator fun <T : MathOperand<T>> T.times(other: T): T {
    return this.multiply(other)
}

@JvmName("div")
operator fun <T : MathOperand<T>> T.div(other: T): T {
    return this.divide(other)
}

@JvmName("rem")
operator fun <T : MathOperand<T>> T.rem(other: T): T {
    return this.remainder(other)
}