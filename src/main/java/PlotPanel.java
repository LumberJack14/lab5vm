import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlotPanel extends JPanel {
    private XYSeries functionSeries;
    private XYSeries interpolationSeries;
    private XYSeries interpolationNodes;

    public PlotPanel() {
        setLayout(new BorderLayout());
        functionSeries = new XYSeries("Исходная функция");
        interpolationSeries = new XYSeries("Интерполяция");
        interpolationNodes = new XYSeries("Узлы интерполяции");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(functionSeries);
        dataset.addSeries(interpolationSeries);
        dataset.addSeries(interpolationNodes);

        JFreeChart chart = ChartFactory.createXYLineChart("График функции и интерполяции",
                "X", "Y", dataset);

        // Настройка рендеринга
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);  // Исходная функция
        renderer.setSeriesShapesVisible(0, false); // Отключаем точки для функции

        renderer.setSeriesLinesVisible(1, true);  // Интерполяция
        renderer.setSeriesShapesVisible(1, false); // Отключаем точки для интерполяции

        renderer.setSeriesLinesVisible(2, false);  // Узлы интерполяции (линии не нужны)
        renderer.setSeriesShapesVisible(2, true);  // Включаем точки для узлов
        renderer.setSeriesPaint(2, Color.BLUE);      // Задаем цвет узлов интерполяции

        plot.setRenderer(renderer);
        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    public void generateAndPlotFunctionSeries(int selectedFunction, double start, double end) {
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        for (double x = start; x <= end; x += 0.1) {
            double y = switch(selectedFunction){
                case 1 -> Math.sin(x);
                case 2 -> Math.log(x);
                case 3 -> (Math.pow(x, 2) - 2*x - x);
                default -> 0;
            };
            xValues.add(x);
            yValues.add(y);
        }

        plotFunction(xValues, yValues, "Функция");
    }

    public void plotFunction(List<Double> xValues, List<Double> yValues, String functionName) {
        functionSeries.clear();
        for (int i = 0; i < xValues.size(); i++) {
            functionSeries.add(xValues.get(i), yValues.get(i));
        }
    }

    public void plotInterpolation(List<Double> xValues, List<Double> yValues, String interpolationName) {
        interpolationSeries.clear();
        for (int i = 0; i < xValues.size(); i++) {
            interpolationSeries.add(xValues.get(i), yValues.get(i));
        }
    }

    public void plotInterpolationNodes(List<Algorithm.Point> points) {
        interpolationNodes.clear();  // Очищаем предыдущие узлы
        for (Algorithm.Point point : points) {
            interpolationNodes.add(point.x, point.y);  // Добавляем узлы интерполяции
        }
    }
}
