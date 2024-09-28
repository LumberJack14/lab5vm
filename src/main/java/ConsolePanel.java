import javax.swing.*;
import java.awt.*;

public class ConsolePanel extends JPanel {

    private JTextArea textArea;
    private JScrollPane scrollPane;

    public ConsolePanel() {
        setLayout(new BorderLayout());
        textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void print(String message) {
        textArea.append(message + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void newLine() {
        textArea.append("\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
