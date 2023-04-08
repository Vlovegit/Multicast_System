import java.io.*;


public class ParticipantOps {
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;
	
	public ParticipantOps(DataInputStream dataInputStream,DataOutputStream dataOutputStream) {
		
		this.dataInputStream = dataInputStream;
		this.dataOutputStream = dataOutputStream;
	}

	private void writeToOutputStream(String str) {
		
		try{
			dataOutputStream.writeUTF(str);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private String readInputStream() {
		
		String msg=null;
		try{
			msg = dataInputStream.readUTF();
		}catch(IOException e){
			e.printStackTrace();
		}
		return msg;
	}

	public void registerParticipant(String port) {
		
		writeToOutputStream("register");
		writeToOutputStream(port);
		Participant.pConnStatus = true;
	}

	public void deregisterParticipant() {
		
		System.out.println("Participant successfully deregistered");
		writeToOutputStream("deregister");
		Participant.pConnStatus = false;
	}

	public void disconnectParticipant() {
		
		
		writeToOutputStream("disconnect");
		Participant.pConnStatus = false;
	}

	public void reconnectParticipant(String newPort) {
		
		writeToOutputStream("reconnect");
		writeToOutputStream(newPort);
		Participant.pConnStatus = true;
	}

	public void multicastSend(String messageStringMulticast) {
		
		try{
			//send message on output channel
			writeToOutputStream("msend");
			writeToOutputStream(messageStringMulticast);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}