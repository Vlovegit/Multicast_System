import java.io.*;

public class CoordinatorOps {
    
    DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;
	DataOutputStream mcastOutputStrem;
	ParticipantInfo pinfo;

	public CoordinatorOps(ParticipantInfo pinfo,
			DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
		// TODO Auto-generated constructor stub
		this.pinfo = pinfo;
		this.dataInputStream = dataInputStream;
		this.dataOutputStream = dataOutputStream;
		
	}

    public boolean register() {
		// TODO Auto-generated method stub
		try{
			//add to registered participant pool
			pinfo.connectionStatus = "Registered";
			if(CoordinatorMain.participantSet.contains(pinfo))//if participant with same id is already registered
				return false;
			
			CoordinatorMain.participantSet.add(pinfo);
			System.out.println("Registered Participants :  \n" + CoordinatorMain.participantSet);
			
		}catch(Exception e){
			System.out.println("Participant registration failed due to "+e.getMessage());
			//e.printStackTrace();
		}
		return true;	
	}

	public void deregister() {
		// TODO Auto-generated method stub
		try{
			//remove participant from participants pool
			System.out.println("Participant " + pinfo + " has deregistered");
			CoordinatorMain.participantSet.remove(pinfo);
			System.out.println("Registered Participants : \n" + CoordinatorMain.participantSet);
			
			
		}catch(Exception e){
			System.out.println("Unable to deregister due to "+e.getMessage());
			//e.printStackTrace();
		}
	}

	public void reconnect() {
		// TODO Auto-generated method stub
		try{
			int reconnectListenerPortNo = Integer.parseInt(dataInputStream.readUTF());// read new port number from user
			
			try{
				if(pinfo.createListener(reconnectListenerPortNo));
			}catch (Exception e){
				System.out.println("Port Disconnected due to "+e.getMessage());
				//e.printStackTrace();
			}
			pinfo.connectionStatus ="Registered";
			Thread.sleep(300);
			pinfo.sendPendingMsgs();//flush all the buffer messages having td < 120 secs
			
		}catch(Exception e){
			System.out.println("Unable to reconnect due to " +e.getMessage());
			//e.printStackTrace();
		}
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		try{
			pinfo.connectionStatus = "Disconnected";
			//disconnect the exisiting port connection
			//participant.terminateListenerPortConnection();
			System.out.println("Participant "+ pinfo +" is disconnected\n");
		}catch(Exception e){
			System.out.println("Unable to disconnect due to  "+e.getMessage());
			//e.printStackTrace();
		}
	}

	public synchronized void multicastSend() {
		// TODO Auto-generated method stub
		try{
			//read the message
			String message = dataInputStream.readUTF();
			
			for(ParticipantInfo p : CoordinatorMain.participantSet){
				if(p.connectionStatus.equals("Registered") && p.msgBuffer.isEmpty()){
					p.getMulticastStream().writeUTF(message);
				}
				else if(p.connectionStatus.equals("Registered") && !p.msgBuffer.isEmpty()){
					p.sendPendingMsgs();// empty all the messages in buffer having timestamp < td secs
					p.getMulticastStream().writeUTF(message);
				}
				else{
					//message added in LinkedList with a timestamp
					MessageInfo msg = new MessageInfo(message,System.currentTimeMillis());
					p.msgBuffer.add(msg);
				}
			}
			
			
		}catch(Exception e){
			System.out.println("Multicast send message failed due to "+e.getMessage());
			//e.printStackTrace();
		}
	}
}
