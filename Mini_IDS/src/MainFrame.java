import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;       // 카드 레이아웃으로 페이지 전환
    private JPanel mainPanel;            // 전체 페이지 담는 패널

    // 첫 페이지 컴포넌트들
    private JPanel homePanel;
    private JButton openButton, analyzeButton, saveButton;
    private JLabel titleLabel;

    // 클래스 내부에서 사용할 LogAnalyzer 선언
    private LogAnalyzer logAnalyzer;  // 로그 분석기 객체

    public MainFrame(){
        setTitle("Mini IDS - 침입 탐지 로그 분석기");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // 화면 중앙 정렬
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createHomePage();                              // 첫 화면 생성

        add(mainPanel);
        setSize(700,500);
        setVisible(true);
    }

    private void createHomePage(){
        homePanel = new JPanel();                 //homePanel: 첫번째 화면(로그 열기/분석/저장 버튼 포함)
        homePanel.setLayout(null);                //null 레이아웃 사용: 픽셀 단위로 정확히 버튼 배치 가능.

        titleLabel = new JLabel("Mini IDS - 침입 탐지 로그 분석기");
        titleLabel.setFont(new Font("Dialog",Font.BOLD,20));
        titleLabel.setBounds(180,50,400,30);
        homePanel.add(titleLabel);

        openButton = new JButton("로그 열기");
        openButton.setBounds(250, 150, 200, 40);
        homePanel.add(openButton);

        // openButton 리스너 등록
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Log files", "log", "txt");
                fileChooser.setFileFilter(filter);

                int result = fileChooser.showOpenDialog(MainFrame.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();          // 분석기 객체 생성
                    logAnalyzer = new LogAnalyzer();
                    logAnalyzer.loadLogFile(selectedFile);

                    JOptionPane.showMessageDialog(MainFrame.this,"파일이 성공적으로 로드되었습니다!",
                            "파일 열기",JOptionPane.INFORMATION_MESSAGE);
                }

            }
        });

        analyzeButton = new JButton("분석 시작");
        analyzeButton.setBounds(250, 210, 200, 40);
        homePanel.add(analyzeButton);

        saveButton = new JButton("결과 저장");
        saveButton.setBounds(250, 270, 200, 40);
        homePanel.add(saveButton);

        // 최종 패널을 카드 레이아웃에 추가
        mainPanel.add(homePanel, "Home");


    }
    public static void main(String[] args) {
        new MainFrame();
    }
}
