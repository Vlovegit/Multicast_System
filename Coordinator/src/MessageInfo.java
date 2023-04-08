public class MessageInfo {
    
    String message;
	Long timestamp;
	
	public MessageInfo(String message, long currentTime) {
		// TODO Auto-generated constructor stub
		this.message = message;
		this.timestamp = currentTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

}
