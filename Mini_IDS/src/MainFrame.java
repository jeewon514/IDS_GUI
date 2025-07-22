import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.filechooser.*;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;       // 카드 레이아웃으로 페이지 전환
    private JPanel mainPanel;            // 전체 페이지 담는 패널

    // 첫 페이지 컴포넌트들
    private JPanel homePanel;
    private JButton openButton, analyzeButton, saveButton;
    private JLabel titleLabel;

    // 클래스 내부에서 사용할 LogAnalyzer 선언
    private LogAnalyzer logAnalyzer;  // 로그 분석기 객체

    // 분석 결과 페이지
    private JPanel tablePanel;                // 로그 테이블 패널
    private JTable logTable;                  // 로그 테이블
    private DefaultTableModel tableModel;     // 테이블 데이터 모델

    // 의심 IP 분석 결과 페이지
    private ResultPage resultPage;

    public MainFrame(){
        setTitle("Mini IDS - 침입 탐지 로그 분석기");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // 화면 중앙 정렬
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createHomePage();                               // 첫 화면 생성
        createTablePage();                              // 분석 결과 페이지 추가
        //ResultPage();

        // resultPage 카드에 등록
        logAnalyzer = new LogAnalyzer(); // 로그 열기 후 초기화한 상태여야 함
        resultPage = new ResultPage(this, logAnalyzer);
        mainPanel.add(resultPage, "Result");

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

        // openButton 리스너 등록(파일 열기 버튼의 "동작" 추가)
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

                    // ResultPage를 다시 생성해서 logAnalyzer 갱신(두 페이지를 연결)
                    mainPanel.remove(resultPage); // 이전 페이지 제거 (중복 방지)
                    resultPage = new ResultPage(MainFrame.this, logAnalyzer);
                    mainPanel.add(resultPage, "Result");

                    // 로그 항목이 0개면 경고 후 종료
                    if (logAnalyzer.getLogs().isEmpty()) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "로그 형식이 잘못되었거나 분석 가능한 항목이 없습니다.",
                                "오류",
                                JOptionPane.ERROR_MESSAGE);
                        return; // 테이블로 넘어가지 않음
                    }else{
                        JOptionPane.showMessageDialog(MainFrame.this,"파일이 성공적으로 로드되었습니다!",
                                "파일 열기",JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                // 나중에 추가함(분석 결과 페이지 추가한 후에)
                ArrayList<LogEntry> logs = logAnalyzer.getLogs();
                tableModel.setRowCount(0);               // 테이블 초기화

                for (LogEntry log : logs){
                    Object[] row = {log.getIp(), log.getTimestamp(), log.getMessage()};
                    tableModel.addRow(row);
                }

                cardLayout.show(mainPanel, "Table");    // 테이블 화면으로 전환
            }
        });

        analyzeButton = new JButton("분석 시작");
        analyzeButton.setBounds(250, 210, 200, 40);
        homePanel.add(analyzeButton);

        // analyzeButton 리스너 등록 (ResultPage 때)
        analyzeButton.addActionListener(e -> {
            if (logAnalyzer == null) return;
            resultPage.showResult();
            cardLayout.show(mainPanel, "Result");
        });


        saveButton = new JButton("결과 저장");
        saveButton.setBounds(250, 270, 200, 40);
        homePanel.add(saveButton);

        // 최종 패널을 카드 레이아웃에 추가
        mainPanel.add(homePanel, "Home");
    }

    private void createTablePage(){
        tablePanel = new JPanel(null);  // ← null layout으로 버튼 위치 지정 가능

        // 테이블 열 제목
        String[] columnNames = {"IP 주소", "시간", "메시지"};
        tableModel = new DefaultTableModel(columnNames, 0);
        logTable = new JTable(tableModel);


        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setBounds(30, 30, 620, 300);
        tablePanel.add(scrollPane);

        // 분석 시작 버튼을 테이블 페이지에 배치
        analyzeButton = new JButton("분석 시작");
        analyzeButton.setBounds(250, 350, 200, 40);
        analyzeButton.addActionListener(e -> {
            if (logAnalyzer == null) return;
            resultPage.showResult();
            showPage("Result");
        });
        tablePanel.add(analyzeButton);


        mainPanel.add(tablePanel, "Table"); // 카드 레이아웃에 등록
    }
    // MainFrame.java 안 어디든 메서드 위치 OK
    public void showPage(String name) {
        cardLayout.show(mainPanel, name);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
