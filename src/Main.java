import java.util.Locale;

public class Main {
    private static void printVector(double[] vect) {
        for (double element : vect) {
            System.out.println(element);
        }
    }

    private static void printVectorMath(double[] vect) {
        for (double element : vect) {
            System.out.printf(Locale.ENGLISH, "%E\n", element);
        }
    }

    private static double[] calcDiscrepancy(double[] left, double[] right) {
        double[] result = new double[left.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = left[i] - right[i];
        }

        return result;
    }

    private static double[] calcAlpha(double[] lowerDiagonal, double[] mainDiagonal, double[] upperDiagonal) {
        double[] alpha = new double[mainDiagonal.length - 1];

        alpha[0] = -upperDiagonal[0] / mainDiagonal[0];
        for (int i = 1; i < alpha.length; i++) {
            alpha[i] = -upperDiagonal[i] / (mainDiagonal[i] + lowerDiagonal[i - 1] * alpha[i - 1]);
        }

        return alpha;
    }

    private static double[] calcBeta(double[] lowerDiagonal, double[] mainDiagonal, double[] rightPart, double[] alpha) {
        double[] beta = new double[mainDiagonal.length];

        beta[0] = rightPart[0] / mainDiagonal[0];
        for (int i = 1; i < beta.length; i++) {
            beta[i] = (rightPart[i] - lowerDiagonal[i - 1] * beta[i - 1]) / (mainDiagonal[i] + lowerDiagonal[i - 1] * alpha[i - 1]);
        }

        return beta;
    }

    private static double[] calcRightSweep(double[] lowerDiagonal, double[] mainDiagonal, double[] upperDiagonal, double[] rightPart) {
        double[] result = new double[mainDiagonal.length];
        double[] alpha = calcAlpha(lowerDiagonal, mainDiagonal, upperDiagonal);
        double[] beta = calcBeta(lowerDiagonal, mainDiagonal, rightPart, alpha);

        result[result.length - 1] = beta[beta.length - 1];
        for (int i = result.length - 2; i >= 0; i--) {
            result[i] = alpha[i] * result[i + 1] + beta[i];
        }

        return result;
    }

    /**/

    private static double calcK(double x) {
        return Math.pow(Math.sin(x), 2.0) + 1.0;
    }

    private static double calcQ(double x) {
        return Math.cos(x);
    }

    private static double calcF(double x) {
        return Math.exp(x);
    }

    private static double[] calcBalanceMethod(int n, double[] nodes, double step, double[] kappa, double[] g) {
        double[] coefsMainDiagonal = new double[nodes.length];
        double[] coefsUpperDiagonal = new double[nodes.length - 1];
        double[] coefsLowerDiagonal = new double[nodes.length - 1];
        double[] rightPart = new double[nodes.length];
        double halfBack;
        double halfForward;

        halfForward = nodes[0] + step / 2.0;
        coefsMainDiagonal[0] = -calcK(halfForward) / step - kappa[0] - step * calcQ(nodes[0]) / 2.0;
        coefsUpperDiagonal[0] = calcK(halfForward) / step;
        rightPart[0] = -g[0] - step * calcF(nodes[0]) / 2.0;

        halfBack = nodes[n] - step / 2.0;
        coefsLowerDiagonal[n - 1] = calcK(halfBack) / step;
        coefsMainDiagonal[n] = -calcK(halfBack) / step - kappa[1] - step * calcQ(nodes[n]) / 2.0;
        rightPart[n] = -g[1] - step * calcF(nodes[n]) / 2.0;

        for (int i = 1; i < n; i++) {
            halfBack = nodes[i] - step / 2.0;
            halfForward = nodes[i] + step / 2.0;

            coefsLowerDiagonal[i - 1] = calcK(halfBack) / Math.pow(step, 2.0);
            coefsMainDiagonal[i] = -(calcK(halfBack) + calcK(halfForward)) / Math.pow(step, 2.0) - calcQ(nodes[i]);
            coefsUpperDiagonal[i] = calcK(halfForward) / Math.pow(step, 2.0);
            rightPart[i] = -calcF(nodes[i]);
        }

        return calcRightSweep(coefsLowerDiagonal, coefsMainDiagonal, coefsUpperDiagonal, rightPart);
    }

    private static double[] calcRitzMethod(int n, double[] nodes, double step, double[] kappa, double[] g) {
        double[] coefsMainDiagonal = new double[nodes.length];
        double[] coefsUpperDiagonal = new double[nodes.length - 1];
        double[] coefsLowerDiagonal = new double[nodes.length - 1];
        double[] rightPart = new double[nodes.length];
        double halfBack;
        double halfForward;

        halfForward = nodes[0] + step / 2.0;
        coefsMainDiagonal[0] = -calcK(halfForward) / step - kappa[0] - step * calcQ(halfForward) / 2.0;
        coefsUpperDiagonal[0] = calcK(halfForward) / step;
        rightPart[0] = -g[0] - step * calcF(halfForward) / 2.0;

        halfBack = nodes[n] - step / 2.0;
        coefsLowerDiagonal[n - 1] = calcK(halfBack) / step;
        coefsMainDiagonal[n] = -calcK(halfBack) / step - kappa[1] - step * calcQ(halfBack) / 2.0;
        rightPart[n] = -g[1] - step * calcF(halfBack) / 2.0;

        for (int i = 1; i < n; i++) {
            halfBack = nodes[i] - step / 2.0;
            halfForward = nodes[i] + step / 2.0;

            coefsLowerDiagonal[i - 1] = calcK(halfBack) / Math.pow(step, 2.0);
            coefsMainDiagonal[i] = -(calcK(halfBack) + calcK(halfForward)) / Math.pow(step, 2.0) - (calcQ(halfBack) + calcQ(halfForward)) / 2.0;
            coefsUpperDiagonal[i] = calcK(halfForward) / Math.pow(step, 2.0);
            rightPart[i] = -(calcF(halfBack) + calcF(halfForward)) / 2.0;
        }

        return calcRightSweep(coefsLowerDiagonal, coefsMainDiagonal, coefsUpperDiagonal, rightPart);
    }

    public static void main(String... args) {
        int n = 10;
        double step = 1.0 / n;
        double[] nodes = new double[n + 1];
        double[] kappa = new double[2];
        double[] g = new double[2];
        double[] resultBalance;
        double[] resultRitz;

        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = i * step;
        }

        kappa[0] = 1.0;
        kappa[1] = 1.0;
        g[0] = 1.0;
        g[1] = 1.0;

        System.out.println("Метод баланса:");
        System.out.println("Узлы:");
        printVector(nodes);
        System.out.println("Значения искомой функции в узлах:");
        resultBalance = calcBalanceMethod(n, nodes, step, kappa, g);
        printVector(resultBalance);

        System.out.println("Метод Ритца:");
        System.out.println("Узлы:");
        printVector(nodes);
        System.out.println("Значения искомой функции в узлах:");
        resultRitz = calcRitzMethod(n, nodes, step, kappa, g);
        printVector(resultRitz);

        System.out.println("\nНевязка:");
        printVectorMath(calcDiscrepancy(resultBalance, resultRitz));
    }
}
