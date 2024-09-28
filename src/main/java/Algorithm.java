import java.util.Arrays;
import java.util.Comparator;

public class Algorithm {

    public static class Point {
        public double x;
        public double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public double interpolate(Point[] points, MethodType methodType, double xValue) {
        switch (methodType) {
            case LAGRANGE:
                return lagrangeInterpolation(points, xValue);
            case NEWTON:
                return newtonInterpolation(points, xValue);
            case GAUSS:
                return gaussInterpolation(points, xValue);
            default:
                throw new IllegalArgumentException("Unknown interpolation method");
        }
    }

    private double lagrangeInterpolation(Point[] points, double xValue) {
        double result = 0.0;
        int n = points.length;

        for (int i = 0; i < n; i++) {
            double term = points[i].y;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    term *= (xValue - points[j].x) / (points[i].x - points[j].x);
                }
            }
            result += term;
        }
        return result;
    }

    private double newtonInterpolation(Point[] points, double xValue) {
        int n = points.length;
        double[][] dividedDifferences = new double[n][n];

        for (int i = 0; i < n; i++) {
            dividedDifferences[i][0] = points[i].y;
        }

        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                dividedDifferences[i][j] = (dividedDifferences[i + 1][j - 1] - dividedDifferences[i][j - 1]) /
                        (points[i + j].x - points[i].x);
            }
        }

        double result = dividedDifferences[0][0];
        double product = 1.0;

        for (int i = 1; i < n; i++) {
            product *= (xValue - points[i - 1].x);
            result += product * dividedDifferences[0][i];
        }

        return result;
    }

    private double gaussInterpolation(Point[] points, double xValue) {
        int n = points.length;

        Arrays.sort(points, Comparator.comparingDouble(p -> p.x));

        int mid = n / 2;
        double h = points[1].x - points[0].x;
        double q = (xValue - points[mid].x) / h;

        double[][] delta = new double[n][n];
        for (int i = 0; i < n; i++) {
            delta[i][0] = points[i].y;
        }

        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                delta[i][j] = delta[i + 1][j - 1] - delta[i][j - 1];
            }
        }

        double result = delta[mid][0];
        double qProduct = 1;
        int factorial = 1;

        for (int i = 1; i < n; i++) {
            if (i % 2 == 1) {
                qProduct *= q - (i / 2);
                result += (qProduct * delta[mid - (i / 2)][i]) / factorial;
            } else {
                qProduct *= q + (i / 2);
                result += (qProduct * delta[mid - (i / 2)][i]) / factorial;
            }
            factorial *= i + 1;
        }

        return result;
    }

    public double[][] getFiniteDifferences(Point[] points) {
        int n = points.length;
        double[][] differences = new double[n][n];

        for (int i = 0; i < n; i++) {
            differences[i][0] = points[i].y;
        }

        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                differences[i][j] = differences[i + 1][j - 1] - differences[i][j - 1];
            }
        }

        return differences;
    }
}

