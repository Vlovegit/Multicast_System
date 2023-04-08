import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable{

	int listenerPortID;
    String fileLogger;
    Socket mcSocket;
	ServerSocket serverSocket;
    DataInputStream dataInputStream;

	public Receiver(int listenerPortID, String fileLogger) {
		
		this.listenerPortID = listenerPortID;
		this.fileLogger = fileLogger;
	}

	public void run() {
		try{
			// accept coordinator connection
			serverSocket = new ServerSocket(listenerPortID);
			mcSocket = serverSocket.accept();
			dataInputStream = new DataInputStream(mcSocket.getInputStream());
			System.out.println("Listener creation complete on port ID >> "+listenerPortID);
			while(true){
				if(dataInputStream != null){
					
					String message = dataInputStream.readUTF();
					System.out.println("Multicast message recieved >> " + message);
					logger(message);

				}
			}
			
		}catch(Exception e){
			System.out.println("Port got Disconnected " + e.getMessage());
		}finally{
			disconnectPresentReceiver();
		}
	}
	
	public void disconnectPresentReceiver(){
		try{
			if(dataInputStream != null)
				dataInputStream.close();
			if(mcSocket != null)
				mcSocket.close();
			if(serverSocket !=null)
				serverSocket.close();
			
		}catch(IOException ioe){
			System.out.println("Exception in disconnecting the receiver" +ioe.getMessage());
		}
	}

	private void logger(String msg) {
		
		try {
			PrintWriter printWriter = null;
			File file = new File(fileLogger);
			FileWriter fileWriter = null;
			if (!file.exists()) {
				file.createNewFile();
			}

			fileWriter = new FileWriter(file.getAbsoluteFile(), true);
			printWriter = new PrintWriter(fileWriter);
			printWriter.println(msg);
	
			printWriter.close();
			fileWriter.close();
		}
		catch(Exception e) {
			System.out.println("Exception occurred while writing to log file"+e.getMessage());
		}
	}

}
