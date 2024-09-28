import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    JFrame frame;
    JTable table;
    ConsolePanel consolePanel;
    PlotPanel plotPanel;
    JTextField xValueField;
    JComboBox<String> functionComboBox;
    JTextField intervalStartField;
    JTextField intervalEndField;
    JTextField numPointsField;

    double start = 0;
    double end = 6;
    int selectedFunction = 0;

    Algorithm algorithm = new Algorithm();

    private class ItemListenerImplementation implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                try {
                    selectedFunction = functionComboBox.getSelectedIndex();
                    start = Double.parseDouble(intervalStartField.getText());
                    end = Double.parseDouble(intervalEndField.getText());
                    if (selectedFunction != 0) {
                        plotPanel.generateAndPlotFunctionSeries(selectedFunction, start, end);
                    }
                }catch (Exception ex) {
                    consolePanel.print("Неверно введены данные");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        frame = new JFrame("LAB 5");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel initialPanel = createInitialPanel();
        frame.add(initialPanel);
        frame.setVisible(true);
    }

    public JPanel createInitialPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Отступы

        // Панель ConsolePanel для вывода
        consolePanel = new ConsolePanel();

        // Левая панель с полями ввода и графиком
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Поле для ввода точки
        xValueField = new JTextField();
        leftPanel.add(new JLabel("Точка для вычисления:"));
        leftPanel.add(xValueField);

        // Комбобокс для выбора функции
        functionComboBox = new JComboBox<>(new String[]{"не выбрано", "sin(x)", "ln(x)", "x^2 - 2x + 2"});
        ItemListener itemListener = new ItemListenerImplementation();
        functionComboBox.addItemListener(itemListener);
        leftPanel.add(new JLabel("Выберите функцию:"));
        leftPanel.add(functionComboBox);

        // Поля для ввода интервала и количества точек
         intervalStartField = new JTextField();
         intervalEndField = new JTextField();
         numPointsField = new JTextField();
        leftPanel.add(new JLabel("Интервал (начало):"));
        leftPanel.add(intervalStartField);
        leftPanel.add(new JLabel("Интервал (конец):"));
        leftPanel.add(intervalEndField);
        leftPanel.add(new JLabel("Количество точек:"));
        leftPanel.add(numPointsField);

        // Панель для графика
        plotPanel = new PlotPanel();
        leftPanel.add(plotPanel);

        // Правая панель с таблицей и консолью
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Отступы

        // Таблица для точек
         table = new JTable(new DefaultTableModel(new Object[]{"x", "y"}, 0)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Ограничение на количество строк (не больше 12)
        JButton addRowButton = new JButton("Добавить строку");
        addRowButton.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            if (model.getRowCount() < 12) {
                model.addRow(new Object[]{"", ""});
            } else {
                consolePanel.print("Достигнуто максимальное количество точек (12).");
            }
        });

        JButton removeRowButton = new JButton("Удалить строку");
        removeRowButton.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                model.removeRow(selectedRow);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addRowButton);
        buttonPanel.add(removeRowButton);

        rightPanel.add(tableScrollPane, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.CENTER);
        rightPanel.add(consolePanel, BorderLayout.SOUTH);

        // Кнопки внизу
        JButton calculateButton = new JButton("Вычислить значение");
        JButton loadPointsFromFileButton = new JButton("Загрузить точки из файла");
        loadPointsFromFileButton.addActionListener(e -> loadPointsFromFile());
        JButton generatePointsButton = new JButton("Сгенерировать точки");
        generatePointsButton.addActionListener(e -> generatePoints(intervalStartField.getText(), intervalEndField.getText(), numPointsField.getText()));

        // Добавляем обработчики для кнопок
        calculateButton.addActionListener(e -> {
            try {
                // Собираем точки из таблицы
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int rowCount = model.getRowCount();
                Algorithm.Point[] points = new Algorithm.Point[rowCount];
                for (int i = 0; i < rowCount; i++) {
                    double x = Double.parseDouble(model.getValueAt(i, 0).toString());
                    double y = Double.parseDouble(model.getValueAt(i, 1).toString());
                    points[i] = new Algorithm.Point(x, y);
                }

                // Вводим x для вычисления значения
                double xValue = Double.parseDouble(xValueField.getText());

                // Вычисляем значения для каждого метода
                Algorithm algorithm = new Algorithm();
                double lagrangeResult = algorithm.interpolate(points, MethodType.LAGRANGE, xValue);
                double newtonResult = algorithm.interpolate(points, MethodType.NEWTON, xValue);
                double gaussResult = algorithm.interpolate(points, MethodType.GAUSS, xValue);

                // Вывод результатов в консоль
                consolePanel.print("Результат для многочлена Лагранжа: " + lagrangeResult);
                consolePanel.print("Результат для многочлена Ньютона: " + newtonResult);
                consolePanel.print("Результат для многочлена Гаусса: " + gaussResult);
                consolePanel.newLine();

                // Получаем таблицу конечных разностей
                double[][] finiteDifferences = algorithm.getFiniteDifferences(points);
                consolePanel.print("Таблица конечных разностей:");
                StringBuilder header = new StringBuilder("n\\m ");
                for (int j = 0; j < finiteDifferences.length; j++) {
                    header.append(String.format("%-10s", j));
                }
                consolePanel.print(header.toString());

                for (int i = 0; i < finiteDifferences.length; i++) {
                    StringBuilder row = new StringBuilder(String.format("%-10s", i));
                    for (int j = 0; j < finiteDifferences[i].length; j++) {
                        row.append(String.format("%-10.4f", finiteDifferences[i][j]));  // Форматирование до 4 знаков после запятой
                    }
                    consolePanel.print(row.toString());
                }
                consolePanel.newLine();

                // Отображаем графики функции и интерполяции
                List<Double> xValues = new ArrayList<>();
                List<Double> yValuesFunction = new ArrayList<>();
                List<Double> yValuesNewton = new ArrayList<>();

                for (Algorithm.Point point : points) {
                    xValues.add(point.x);
                    yValuesFunction.add(point.y);
                    yValuesNewton.add(algorithm.interpolate(points, MethodType.NEWTON, point.x));  // Вычисляем для Ньютона
                }

                plotPanel.plotInterpolation(xValues, yValuesNewton, "Интерполяция Ньютона");
                plotPanel.plotInterpolationNodes(Arrays.asList(points));

            } catch (NumberFormatException ex) {
                consolePanel.print("Ошибка: Введите корректные числовые значения.");
            }
        });

        // Панель для кнопок внизу
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(calculateButton);
        bottomPanel.add(loadPointsFromFileButton);
        bottomPanel.add(generatePointsButton);

        // Добавляем панели в основную панель
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }


    private void addRow() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{"", ""});
    }

    private void removeRow() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
        }
    }

    private void loadPointsFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                List<Algorithm.Point> points = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] split = line.split(" ");
                    double x = Double.parseDouble(split[0]);
                    double y = Double.parseDouble(split[1]);
                    points.add(new Algorithm.Point(x, y));
                }

                if (points.size() >= 5 && points.size() <= 12) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.setRowCount(0); // Очищаем таблицу
                    for (Algorithm.Point point : points) {
                        model.addRow(new Object[]{point.x, point.y});
                    }
                } else {
                    consolePanel.print("Неверное количество точек. Нужно от 5 до 12.");
                }
            } catch (IOException e) {
                consolePanel.print("Ошибка чтения файла.");
            }
        }
    }

    private void generatePoints(String startText, String endText, String numPointsText) {
        try {
            double start = Double.parseDouble(startText);
            double end = Double.parseDouble(endText);
            int numPoints = Integer.parseInt(numPointsText);

            if (numPoints < 5 || numPoints > 12) {
                consolePanel.print("Неверное количество точек. Нужно от 5 до 12.");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // Очищаем таблицу

            double step = (end - start) / (numPoints - 1);
            String selectedFunction = (String) functionComboBox.getSelectedItem();

            for (int i = 0; i < numPoints; i++) {
                double x = start + i * step;
                double y = 0;

                switch (selectedFunction) {
                    case "sin(x)":
                        y = Math.sin(x);
                        break;
                    case "ln(x)":
                        y = Math.log(x);
                        break;
                    case "x^2 - 2x + 2":
                        y = x * x - 2 * x + 2;
                        break;
                    case "не выбрано":
                        consolePanel.print("функция не выбрана");
                        return;
                }

                model.addRow(new Object[]{x, y});
            }

        } catch (NumberFormatException e) {
            consolePanel.print("Ошибка ввода интервала или количества точек.");
        }
    }

    private void calculateValues() {
        try {
            double xValue = Double.parseDouble(xValueField.getText());
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int rowCount = model.getRowCount();

            if (rowCount < 5 || rowCount > 12) {
                consolePanel.print("Неверное количество точек. Нужно от 5 до 12.");
                return;
            }

            // Собираем точки из таблицы
            Algorithm.Point[] points = new Algorithm.Point[rowCount];
            for (int i = 0; i < rowCount; i++) {
                double x = Double.parseDouble(model.getValueAt(i, 0).toString());
                double y = Double.parseDouble(model.getValueAt(i, 1).toString());
                points[i] = new Algorithm.Point(x, y);
            }

            // Вычисляем значение для каждого метода
            double resultLagrange = algorithm.interpolate(points, MethodType.LAGRANGE, xValue);
            double resultNewton = algorithm.interpolate(points, MethodType.NEWTON, xValue);
            double resultGauss = algorithm.interpolate(points, MethodType.GAUSS, xValue);

            consolePanel.print("Результаты:");
            consolePanel.print("Лагранж: " + resultLagrange);
            consolePanel.print("Ньютон: " + resultNewton);
            consolePanel.print("Гаусс: " + resultGauss);
        } catch (NumberFormatException e) {
            consolePanel.print("Ошибка ввода аргумента для вычисления.");
        }
    }
}
