import java.net.*;

class Listener extends Thread {
	private ServerWindow sw;
	private Socket client;
	
	Listener(ServerWindow sw) {
		this.sw = sw;
		setDaemon(true);
	}

	public void run() {
		try {
			while (true) {
				setClient(sw.getServer().accept());// akzeptiert Clients
				ServerThread c = new ServerThread(sw, getClient()); // erstellt einen ServerThread für neuen Client
				c.start();						
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Socket getClient() {
		return client;
	}

	public void setClient(Socket client) {
		this.client = client;
	}
}