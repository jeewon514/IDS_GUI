import java.io.*;
import java.util.*;

// 로그를 분석하는 핵심 클래스
public class LogAnalyzer {
    // 로그 항목을 저장할 리스트
    private ArrayList<LogEntry> logs = new ArrayList<>();

    // 추가
    public HashMap<String, Integer> detectSuspiciousIPs() {
        HashMap<String, Integer> map = new HashMap<>();

        for (LogEntry log : logs) {
            String ip = log.getIp();
            String message = log.getMessage();

            if (message.contains("LOGIN_FAIL") || message.contains("PORT_SCAN")) {
                map.put(ip, map.getOrDefault(ip, 0) + 1);
            }
        }

        map.entrySet().removeIf(entry -> entry.getValue() < 3);
        return map;
    }
    // 여기까지

    // 로그 파일을 한 줄씩 읽어 로그 객체로 저장
    public void loadLogFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                // 예: "2025-07-18 14:23:51 IP:192.168.0.5 LOGIN_FAIL"
                LogEntry entry = parseLogLine(line);
                if (entry != null) {
                    logs.add(entry);
                }
            }

            System.out.println("로그 로딩 완료. 총 " + logs.size() + "개 항목.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 로그 한 줄을 파싱하는 메서드 (간단한 형식 가정)
    private LogEntry parseLogLine(String line) {
        try {
            // 로그 형식: 날짜 시간 IP:주소 메시지
            // 예: 2025-07-18 14:23:51 IP:192.168.0.5 LOGIN_FAIL
            String[] parts = line.trim().split(" ");

            // 조건: "날짜 시간 IP:주소 메시지" 형식임
            if (parts.length >= 4 && parts[2].startsWith("IP:")) {
                String date = parts[0];
                String time = parts[1];
                String ip = parts[2].replace("IP:", "");
                String message = parts[3];

                return new LogEntry(ip, date + " " + time, message);
            }
        } catch (Exception e) {           // 잘못된 형식 무시
        }
        return null;
    }

    // 로그 항목 리스트 반환
    public ArrayList<LogEntry> getLogs() {
        return logs;
    }
}

