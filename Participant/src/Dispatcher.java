import java.io.*;
import java.net.Socket;
import java.util.*;

public class Dispatcher implements Runnable {


	int participantID;
    int listenerPortID;
    String fileLogger;
	String connectionInfo;
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;
	ParticipantOps ops ;
	Receiver rcvrThread;
	
	public Dispatcher(int participantID, String connectionInfo,String fileLogger) {
		
		this.participantID = participantID;
		this.connectionInfo = connectionInfo;
		this.fileLogger = fileLogger;
	}

	public void run() {
		

		try{
			String connParameters [] = connectionInfo.split(":");
			
			Socket participantSocket = new Socket(connParameters[0],Integer.parseInt(connParameters[1]));
			
			dataInputStream = new DataInputStream(participantSocket.getInputStream());
			dataOutputStream = new DataOutputStream(participantSocket.getOutputStream());

			System.out.println(readFromInpStream());
			
			writeToOutStream(String.valueOf(participantID));
			
			Scanner sc = new Scanner(System.in);
			String userCommandString;
			
			boolean execParticipantStatus = true;
			ops = new ParticipantOps(dataInputStream,dataOutputStream);		
			while(execParticipantStatus){
				Thread.sleep(1200);
				System.out.print("Participant " + participantID +" >> ");
				userCommandString = sc.nextLine();
				execParticipantStatus = dispatchCommand(userCommandString);
			
			}
			
			return;
			
		}catch (Exception e){
			System.out.println("Exception in dispatcher thread : "+e.getMessage());
			
		}
		
	}
	
	private boolean dispatchCommand(String userCommandString) {
		
		
		ArrayList<String> cmd = parseString(userCommandString);
		String status;
		try{

			switch (cmd.get(0)){
				
				case "register":
									if(!Participant.pConnStatus){//not registered
										System.out.println("Registering participant now");
									}else{
										System.out.println("Participant is registered already");
									}
									ops.registerParticipant(cmd.get(1));
									status = readFromInpStream();
									if(status.equals("pass")){//Registration is successful
										//create receiver thread for multicast
										makeReceiver(Integer.parseInt(cmd.get(1)), fileLogger);
										Thread.sleep(1000);
										writeToOutStream("listener is configured");
									}else{
										System.out.println("Participant is registered already .");
										return false;
									}
									break;
			
				case "deregister":
									ops.deregisterParticipant();
									status = readFromInpStream();
									if(status.equals("pass")){//Deregistration is successful
										rcvrThread.disconnectPresentReceiver();
										return false; // closing participant node after deregistration
									}else{
										System.out.println("Participant is not deregistered. Participant needs to register first to deregister.");
									}
									break;
				
				case "disconnect":
									if(Participant.pConnStatus){

										ops.disconnectParticipant();//requesting server to disconnect the connection on listener port
										rcvrThread.disconnectPresentReceiver();//closing the present listener  created during registration
										Thread.sleep(500);
										status = readFromInpStream();
										if(status.equals("fail")){//"failed to disconnect"
											System.out.println("Unable to disconnect.");
										}				
										
									}else{
										System.out.println("Participant node is disconnected already.");
									}
									break;
				
				case "reconnect":
									//create Receiver Thread on new port 
									if(!Participant.pConnStatus){
										makeReceiver(Integer.parseInt(cmd.get(1)),fileLogger);
										Thread.sleep(500);
										ops.reconnectParticipant(cmd.get(1));// requesting server to reconnect on new port ID
										status = readFromInpStream();
										if(status.equals("fail")){//"Reconnection not successful"
											System.out.println("Unable to reconnect to server");
										}
									}else{
										System.out.println("Already connected to server.");
									}
									break;
				
				case "msend":
									if(Participant.pConnStatus){
										String msg1 = participantID + " : " + cmd.get(1);
										ops.multicastSend(msg1);
										status = readFromInpStream();
										if(status.equals("fail")){//"msend not successful"
											System.out.println("msend is unsuccessful");
										}
									}else{
										System.out.println("msend failed as not connected to server");
									}
									break;

				default:			System.out.println("Invalid command entered");

				}
			
		}catch(Exception e){
			System.out.println("Exception in dispatching command>> " + e.getMessage());
			
		}
		
		return true;
		
	}

	private void makeReceiver(int listenerPortID,String fileLogger) {
		
		try{
			rcvrThread = new Receiver(listenerPortID,fileLogger);
			new Thread(rcvrThread).start();
		}catch(Exception e){
			System.out.println("Exception in creation of multicast read thread"+e.getMessage());
			
		}
	}

	private ArrayList<String> parseString(String userCommand) {
		
		ArrayList<String> command = new ArrayList<>();
		try{
			
			command.add((userCommand.split(" ", 2)[0]).trim());
			if(userCommand.split(" ", 2).length > 1)
				command.add((userCommand.split(" ",2)[1]).trim());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return command;
	}

	private void writeToOutStream(String message) {
		
		try{
			dataOutputStream.writeUTF(message);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private String readFromInpStream() {
		
		String msg=null;
		try{
			msg = dataInputStream.readUTF();
		}catch(IOException e){
			e.printStackTrace();
		}
		return msg;
	}

}

