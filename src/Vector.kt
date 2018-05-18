import java.util.*

class Vector(var size: Int) {
    var vector: Array<Double> = Array(size, { 0.0 })

    constructor(initVector: Array<Double>) : this(initVector.size) {
        for (i in vector.indices) {
            vector[i] = initVector[i]
        }
    }

    constructor(initVector: Vector) : this(initVector.vector)

    fun print() {
        vector.forEach { element -> println(element) }
    }

    fun printMath() {
        vector.forEach { element -> System.out.printf(Locale.ENGLISH, "%E\n", element) }
    }

    fun norm(): Double {
        var max = Double.NEGATIVE_INFINITY
        for (i in vector.indices) {
            if (Math.abs(vector[i]) > max) {
                max = Math.abs(vector[i])
            }
        }
        return max
    }

    fun abs(): Vector {
        vector.map { element -> Math.abs(element) }
        return this
    }

    operator fun plus(b: Vector): Vector {
        var result: Vector
        if (size != b.size) {
            throw Exception("Incorrect size")
        }
        result = Vector(this)
        for (i in vector.indices) {
            result[i] += b[i]
        }
        return result
    }

    operator fun minus(b: Vector): Vector {
        var result: Vector
        if (size != b.size) {
            throw Exception("Incorrect size")
        }
        result = Vector(this)
        for (i in vector.indices) {
            result[i] -= b[i]
        }
        return result
    }

    operator fun times(b: Double): Vector {
        var result = Vector(this)
        for (i in vector.indices) {
            result[i] *= b
        }
        return result
    }

    operator fun times(b: Vector): Double {
        if (size != b.size) {
            throw Exception("Incorrect size")
        }
        return (0 until size).sumByDouble { vector[it] * b[it] }
    }

    operator fun div(b: Double): Vector {
        var result = Vector(this)
        for (i in vector.indices) {
            result[i] /= b
        }
        return result
    }

    operator fun get(i: Int): Double {
        return vector[i]
    }

    operator fun set(i: Int, b: Double) {
        vector[i] = b
    }
}