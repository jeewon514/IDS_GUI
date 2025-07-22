import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ResultPage extends JPanel {
    private JLabel resultLabel;
    private JButton backButton;
    private JButton saveButton;
    private MainFrame mainFrame;  // MainFrame 참조
    private LogAnalyzer logAnalyzer;

    public ResultPage(MainFrame mainFrame, LogAnalyzer logAnalyzer) {
        this.mainFrame = mainFrame;
        this.logAnalyzer = logAnalyzer;
        setLayout(null);

        resultLabel = new JLabel();
        resultLabel.setBounds(50, 80, 600, 100);
        resultLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        add(resultLabel);

        backButton = new JButton("◀ 돌아가기");
        backButton.setBounds(230, 220, 120, 35);
        backButton.addActionListener(e -> mainFrame.showPage("Table"));
        add(backButton);

        saveButton = new JButton("결과 저장");
        saveButton.setBounds(360, 220, 140, 35);
        saveButton.addActionListener(e -> saveToFile());
        add(saveButton);
    }

    public void showResult() {
        HashMap<String, Integer> suspiciousMap = logAnalyzer.detectSuspiciousIPs();

        if (suspiciousMap.isEmpty()) {
            resultLabel.setText("이상 징후가 감지되지 않았습니다.");
        } else {
            StringBuilder sb = new StringBuilder("<html><body>🚨 의심 IP 감지:<br>");
            for (Map.Entry<String, Integer> entry : suspiciousMap.entrySet()) {
                sb.append("- ").append(entry.getKey())
                        .append(" (").append(entry.getValue()).append("회)<br>");
            }
            sb.append("</body></html>");
            resultLabel.setText(sb.toString());
        }
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("suspicious_result.txt"));
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                HashMap<String, Integer> suspiciousMap = logAnalyzer.detectSuspiciousIPs();

                if (suspiciousMap.isEmpty()) {
                    writer.write("이상 징후가 감지되지 않았습니다.");
                } else {
                    writer.write("🚨 의심 IP 감지 결과\n");
                    for (Map.Entry<String, Integer> entry : suspiciousMap.entrySet()) {
                        writer.write("- " + entry.getKey() + " (" + entry.getValue() + "회)\n");
                    }
                }

                JOptionPane.showMessageDialog(this, "저장 완료!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "저장 실패: " + e.getMessage());
            }
        }
    }
}



