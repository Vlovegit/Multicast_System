import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

public class ParticipantInfo {


    int id,portNo;
	InetAddress ipAddress;
	String connectionStatus;
	LinkedList<MessageInfo> msgBuffer;
	DataOutputStream dataOutputStream;
	Socket p_socket;
	int threshold;

	public ParticipantInfo(int id, InetAddress ipAddress, String connectionStatus,int threshold) {
		this.id = id;
		this.ipAddress = ipAddress;
		this.connectionStatus = connectionStatus;
		this.msgBuffer = new LinkedList<MessageInfo>();
		this.threshold = threshold;
	}

	public DataOutputStream getMulticastStream() {
		return dataOutputStream;
	}

	public void setMulticastStream(DataOutputStream dataOutputStream) {
		this.dataOutputStream = dataOutputStream;
	}

    public int getPortNo() {
		return portNo;
	}
	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

    public void sendPendingMsgs(){
        try{
            System.out.println("Msg Buffer : "+ msgBuffer);
            while(!msgBuffer.isEmpty()){
                MessageInfo m = msgBuffer.poll();
				System.out.println("Message Info : "+ m);
				System.out.println("Time gap : "+ (System.currentTimeMillis() - m.timestamp)/1000);
				System.out.println("Threshold : "+ threshold);
                if((System.currentTimeMillis() - m.timestamp)/1000 <= threshold){
                    System.out.println("Sending messages now");
					dataOutputStream.writeUTF(m.message);
                }
            }
    
        }catch(Exception e){
            System.out.println("Failed to send pending messages due to "+e.getMessage());
            //e.printStackTrace();
        }
            
    }

    public boolean createListener(int new_port_no) throws Exception{
		// TODO Auto-generated method stub
			
		this.portNo = new_port_no;
		p_socket = new Socket(ipAddress,portNo);
		dataOutputStream = new DataOutputStream(p_socket.getOutputStream());
		return true;
	}


	@Override
	public String toString() {
		
		return "pid = " + id + ", status = " + connectionStatus+"]";
	}

    @Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ParticipantInfo other = (ParticipantInfo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
	
}
