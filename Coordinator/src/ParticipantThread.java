import java.io.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ParticipantThread implements Runnable {

	DataInputStream pInputStream;
	DataOutputStream pOutputStream;
	InetAddress pAddress;
	CoordinatorOps cthread;
	int threshold;

	public ParticipantThread(Socket csocket, int threshold) throws IOException {
		// TODO Auto-generated constructor stub
		pInputStream = new DataInputStream(csocket.getInputStream()); 
		pOutputStream = new DataOutputStream(csocket.getOutputStream()); 
		pAddress = csocket.getInetAddress();
		this.threshold = threshold;

	}

    @Override
    public void run() {
		// TODO Auto-generated method stub

		try {
			
            pOutputStream.writeUTF("Connection pass");

			String participantDetails = pInputStream.readUTF();

			ParticipantInfo participant = new ParticipantInfo(
					Integer.parseInt(participantDetails), pAddress, null, threshold);
			cthread = new CoordinatorOps(participant, pInputStream, pOutputStream);

			System.out.println("Participant " + participant + " created");

			while (true) {

				String command = pInputStream.readUTF();// /read user input

				if (!command.equals("deregister")) {

					switch (command) {

					case "register":
										if (participant.connectionStatus == null) {
											participant.setPortNo(Integer.parseInt(pInputStream.readUTF()));
											if (cthread.register()) {
												pOutputStream.writeUTF("pass");
												String status = pInputStream.readUTF();
												if (status.equals("listener is configured")) {
													participant.createListener(participant.getPortNo());
												}
											} else {
												pOutputStream.writeUTF("fail");// "Registration unsuccessful"
												System.out.println("Participant Id already exists");
											}
										} else {
											pOutputStream.writeUTF("fail");// "Registration unsuccessful"
											System.out.println("Participant already registered");
										}
										break;

					
					case "msend":
										if (participant.connectionStatus != null && 
										participant.connectionStatus.equals("Registered")) {//"Check if participant is registered"
											cthread.multicastSend();
											pOutputStream.writeUTF("pass");//"msend pass"
										} else {
											pOutputStream.writeUTF("fail");//"msend unsuccessful"
										}
										break;
					
					case "reconnect":
										if (participant.connectionStatus != null && 
										participant.connectionStatus.equals("Disconnected")) {
											cthread.reconnect();
											System.out.println("Status after reconnection "	+ participant.connectionStatus);
											pOutputStream.writeUTF("pass");//"Reconnected successfully"
										} else {
											pOutputStream.writeUTF("fail");//"Reconnect unsuccessful"
										}
										break;

					case "disconnect":
										if (participant.connectionStatus != null && 
										participant.connectionStatus.equals("Registered")) {//"Check if participant is registered"
											cthread.disconnect();
											pOutputStream.writeUTF("pass");//"Disconnected successfully"
										} else {
											pOutputStream.writeUTF("fail");//"Disconnection unsuccessful"
										}
										break;

					default:			System.out.println("Invalid command sent by the participant ");
					}

				} else {

					if (participant.connectionStatus != null) {
						cthread.deregister();
						pOutputStream.writeUTF("pass");//"Deregistration pass"
						break;
					} else {
						pOutputStream.writeUTF("fail");//"Deregistration unsuccessful"
					}

				}

			}

		} catch (IOException ef) {
			System.out.println("Participant terminated forcefully"
					+ ef.getMessage());
		} catch (Exception e) {
			System.out.println("Command execution sent by participant failed due to " +e.getMessage());
			//e.printStackTrace();
		}
	}

}