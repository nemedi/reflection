package demo.chat;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	public interface ClientListener {
		
		void onAccept(String[] names);
		
		void onDeny();
		
		void onAdd(String name);
		
		void onRemove(String name);
		
		void onReceive(String from, String text);
		
		void onExit();
		
		void onForbidden();
	}
	
	private Socket socket;
	private ClientListener listener;
	
	public Client(ClientListener listener) { 
		this.listener = listener;
	}

	public void connect(String host, int port) throws UnknownHostException, IOException {
		disconnect();
		socket = new Socket(host, port);
		ClientWorker worker = new ClientWorker();
		new Thread(worker).start();
	}
	
	public void disconnect() throws IOException {
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
		socket = null;
	}
	
	public void login(String name) throws IOException {
		Transport.write(new Message(Message.LOGIN, name), socket);
	}
	
	public void send(String to, String text) throws IOException {
		Transport.write(new Message(Message.SEND, to, text), socket);
	}
	
	public void logout() throws IOException {
		Transport.write(new Message(Message.LOGOUT), socket);
	}

	private class ClientWorker implements Runnable {
	
		@Override
		public void run() {
			while (socket != null && !socket.isClosed()) {
				try {
					if (socket.getInputStream().available() > 0) {
						Message message = Transport.read(socket);
						switch (message.getType()) {
						case Message.ACCEPT:
							listener.onAccept(message.getArguments());
							break;
						case Message.DENY:
							listener.onDeny();
							break;
						case Message.ADD:
							listener.onAdd(message.getArguments()[0]);
							break;
						case Message.REMOVE:
							listener.onRemove(message.getArguments()[0]);
							break;
						case Message.RECEIVE:
							listener.onReceive(message.getArguments()[0], message.getArguments()[1]);
							break;
						case Message.EXIT:
							listener.onExit();
							break;
						default:
							listener.onForbidden();
							break;
						}
					}
				} catch (IOException e) {
					continue;
				}
			}
		}
		
	}
	
}
