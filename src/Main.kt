fun calcK(x: Double): Double {
    return Math.pow(Math.sin(x), 2.0) + 1.0
}

fun calcQ(x: Double): Double {
    return Math.cos(x)
}

fun calcF(x: Double): Double {
    return Math.exp(x)
}

fun calcBalanceMethod(nodes: Vector, step: Double, kappa: Vector, g: Vector): Vector {
    val coefs = Matrix(nodes.size, nodes.size)
    val rightPart = Vector(nodes.size)
    var halfBack: Double
    var halfForward: Double

    halfForward = nodes[0] + step / 2.0
    coefs[0][0] = -calcK(halfForward) / step - kappa[0] - step * calcQ(nodes[0]) / 2.0
    coefs[0][1] = calcK(halfForward) / step
    rightPart[0] = -g[0] - step * calcF(nodes[0]) / 2.0

    halfBack = nodes[nodes.size - 1] - step / 2.0
    coefs[nodes.size - 1][nodes.size - 2] = calcK(halfBack) / step
    coefs[nodes.size - 1][nodes.size - 1] = -calcK(halfBack) / step - kappa[1] - step * calcQ(nodes[nodes.size - 1]) / 2.0
    rightPart[nodes.size - 1] = -g[1] - step * calcF(nodes[nodes.size - 1]) / 2.0

    for (i in 1 until nodes.size - 1) {
        halfBack = nodes[i] - step / 2.0
        halfForward = nodes[i] + step / 2.0

        coefs[i][i - 1] = calcK(halfBack) / Math.pow(step, 2.0)
        coefs[i][i] = -(calcK(halfBack) + calcK(halfForward)) / Math.pow(step, 2.0) - calcQ(nodes[i])
        coefs[i][i + 1] = calcK(halfForward) / Math.pow(step, 2.0)
        rightPart[i] = -calcF(nodes[i])
    }

    return Matrix.calpRightSweep(coefs, rightPart)
}

fun calcRitzMethod(nodes: Vector, step: Double, kappa: Vector, g: Vector): Vector {
    val coefs = Matrix(nodes.size, nodes.size)
    val rightPart = Vector(nodes.size)
    var halfBack: Double
    var halfForward: Double

    halfForward = nodes[0] + step / 2.0
    coefs[0][0] = -calcK(halfForward) / step - kappa[0] - step * calcQ(halfForward) / 2.0
    coefs[0][1] = calcK(halfForward) / step
    rightPart[0] = -g[0] - step * calcF(halfForward) / 2.0

    halfBack = nodes[nodes.size - 1] - step / 2.0
    coefs[nodes.size - 1][nodes.size - 2] = calcK(halfBack) / step
    coefs[nodes.size - 1][nodes.size - 1] = -calcK(halfBack) / step - kappa[1] - step * calcQ(halfBack) / 2.0
    rightPart[nodes.size - 1] = -g[1] - step * calcF(halfBack) / 2.0

    for(i in 1 until nodes.size - 1) {
        halfBack = nodes[i] - step / 2.0
        halfForward = nodes[i] + step / 2.0

        coefs[i][i - 1] = calcK(halfBack) / Math.pow(step, 2.0)
        coefs[i][i] = -(calcK(halfBack) + calcK(halfForward)) / Math.pow(step, 2.0) - (calcQ(halfBack) + calcQ(halfForward)) / 2.0
        coefs[i][i + 1] = calcK(halfForward) / Math.pow(step, 2.0)
        rightPart[i] = -(calcF(halfBack) + calcF(halfForward)) / 2.0
    }

    return Matrix.calpRightSweep(coefs, rightPart)
}

fun main(args: Array<String>) {
    val n = 10
    val step = 1.0 / n
    val nodes = Vector(n + 1)
    val kappa = Vector(2)
    val g = Vector(2)
    var resultBalance: Vector
    var resultRitz: Vector

    for (i in 0 until nodes.size) {
        nodes[i] = i * step
    }

    kappa[0] = 1.0
    kappa[1] = 1.0
    g[0] = 1.0
    g[1] = 1.0

    println("Метод баланса:")
    println("Узлы:")
    nodes.print()
    println("Значения искомой функции в узлах:")
    resultBalance = calcBalanceMethod(nodes, step, kappa, g)
    resultBalance.print()

    println("Метод Ритца:")
    println("Узлы:")
    nodes.print()
    println("Значения искомой функции в узлах:")
    resultRitz = calcRitzMethod(nodes, step, kappa, g)
    resultRitz.print()

    println()
    println("Невязка:")
    (resultBalance - resultRitz).abs().printMath()
}