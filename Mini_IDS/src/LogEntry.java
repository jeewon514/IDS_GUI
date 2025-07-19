// 로그 1개를 표현하는 데이터 클래스
public class LogEntry {
    private String ip;
    private String timestamp;
    private String message;

    public LogEntry(String ip, String timestamp, String message) {
        this.ip = ip;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getIp() {
        return ip;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}

