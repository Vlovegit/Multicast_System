import java.io.*;
import java.util.*;

public class Participant {

	public static boolean pConnStatus = false;
	
	public static void main(String[] args) throws InterruptedException {
		
		
		String pConfig = args[0];
		ArrayList <String> configurationInfo = getConfigurationInfo(pConfig);
		int participantID = Integer.parseInt(configurationInfo.get(0)); //set participant id
        String fileLogger = configurationInfo.get(1); // file for logging multicast msgs
        String connectionInfo = configurationInfo.get(2);//fetch host and port of coordinator
		
		System.out.println("Participant successfully started");
		Dispatcher(participantID, connectionInfo, fileLogger); 
		
		
	}

	private static void Dispatcher(int participantID,String connectionInfo,String fileLogger) {
		
		try{
			new Thread(new Dispatcher(participantID,connectionInfo,fileLogger)).start();
			return;
		}catch(Exception e){
			System.out.println("Command Thread unsuccessful : "+e.getMessage());
		}
	}

	private static ArrayList <String> getConfigurationInfo(String configurationFileString) {
	
		ArrayList <String> configurationInfo = new ArrayList<String>();
		try{
			
			BufferedReader buffer = new BufferedReader(new FileReader(configurationFileString));
			String input = null;

			while((input = buffer.readLine()) != null){
				configurationInfo.add(input.trim());
			}
			
			if(buffer!= null){
				buffer.close();
			}
		}catch(Exception e){
			System.out.println("Some exception occurred at Coordinator Side " + e.getMessage());
		}
		return configurationInfo;
	}
}

