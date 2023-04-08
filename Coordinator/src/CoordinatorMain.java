import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class CoordinatorMain {

    static int threshold,cport;
	static HashSet<ParticipantInfo> participantSet = new HashSet<ParticipantInfo>();
    public static void main(String[] args) throws IOException {

		String filename = args[0];

        List<String> config = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                config.add(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
		
        threshold = Integer.parseInt(config.get(1));
		cport = Integer.parseInt(config.get(0));
		System.out.println("Message Threshold : " + CoordinatorMain.threshold);
		
        try (ServerSocket csocket = new ServerSocket(cport)) {
            System.out.println("Coordinator started on port : " + cport);
            
            while(true){
            	new Thread(new ParticipantThread(csocket.accept(),threshold)).start();
            }
        }
		
	}
}
