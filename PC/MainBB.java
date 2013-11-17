
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class MainBB {

	public static void main(String[] argv) {
		StreamConnectionNotifier notifier;
        StreamConnection connection = null;
        LocalDevice local = null;        
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);

            UUID uuid = new UUID(80087355); // "04c6093b-0000-1000-8000-00805f9b34fb"
            String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
            
            notifier = (StreamConnectionNotifier) Connector.open(url);
            connection = notifier.acceptAndOpen();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
		BeamAndBall bb = new BeamAndBall();
		
		ReadCommServer readServer = new ReadCommServer(connection, bb);
		WriteCommServer writeServer = new WriteCommServer(connection, bb);
		
		readServer.start();
		writeServer.start();
	}
}

