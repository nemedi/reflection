package demo.chat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Server {
	
	private ServerSocket serverSocket;
	private Map<Socket, String> logins;
	
	public void start(int port) throws UnknownHostException, IOException {
		stop();
		serverSocket = new ServerSocket(port, 10, InetAddress.getLocalHost());
		ServerWorker worker = new ServerWorker();
		logins = new HashMap<Socket, String>();
		new Thread(worker).start();
	}
	
	public void stop() throws IOException {
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
		serverSocket = null;
	}

	private class ServerWorker implements Runnable {

		@Override
		public void run() {
			while (serverSocket != null && !serverSocket.isClosed()) {
				try {
					ClientWorker worker = new ClientWorker(serverSocket.accept());
					new Thread(worker).start();
				} catch (IOException e) {
					continue;
				}
			}
			
		}
		
	}
	
	private class ClientWorker implements Runnable {

		private Socket socket;

		public ClientWorker(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				while (socket != null && !socket.isClosed()) {
					try {
						if (socket.getInputStream().available() > 0) {
							Message message = Transport.read(socket);
							switch (message.getType()) {
							case Message.LOGIN:
								handleLogin(socket, message.getArguments()[0]);
								break;
							case Message.SEND:
								handleSend(socket, message.getArguments()[0], message.getArguments()[1]);
								break;
							case Message.LOGOUT:
								handleLogout(socket);
								break;
							default:
								handleForbidden(socket);
								break;
							}
						}
					} catch (Exception e) {
						continue;
					}
				}
			} finally {
				if (isLogged(socket)) {
					try {
						handleLogout(socket);
					} catch (IOException e) {
					}
				}
			}
		}
		
		private boolean isLogged(Socket socket) {
			return logins.containsKey(socket)
					&& logins.get(socket) != null;
		}

		private void handleLogin(Socket socket, String name) throws IOException {
			synchronized (logins) {
				if (isLogged(socket)) {
					handleForbidden(socket);
					return;
				}
				if (logins.keySet().contains(name)) {
					Transport.write(new Message(Message.DENY), socket);
					return;
				}
				List<String> names = new ArrayList<String>();
				names.add(Message.ALL);
				Message message = new Message(Message.ADD, name);
				for (Entry<Socket, String> login : logins.entrySet()) {
					if (login.getKey() != socket) {
						names.add(login.getValue());
						Transport.write(message, login.getKey());
					}
				}
				logins.put(socket, name);
				Transport.write(new Message(Message.ACCEPT,
						names.toArray(new String[names.size()])), socket);
			}
		}

		private void handleSend(Socket socket, String to, String text) throws IOException {
			synchronized (logins) {
				if (!isLogged(socket)) {
					handleForbidden(socket);
					return;
				}
				Message message = new Message(Message.RECEIVE, logins.get(socket), text);
				if (Message.ALL.equals(to)) {
					for (Entry<Socket, String> login : logins.entrySet()) {
						Transport.write(message, login.getKey());
					}
				} else {
					for (Entry<Socket, String> login : logins.entrySet()) {
						if (to.equals(login.getValue())) {
							Transport.write(message, login.getKey());
							Transport.write(message, socket);
							return;
						}
					}
					handleForbidden(socket);
				}
			}
		}

		private void handleLogout(Socket socket) throws IOException {
			if (!isLogged(socket)) {
				handleForbidden(socket);
				return;
			}
			Message message = new Message(Message.REMOVE, logins.get(socket));
			for (Entry<Socket, String> login : logins.entrySet()) {
				if (login.getKey() != socket) {
					Transport.write(message, login.getKey());
				}
			}
			logins.remove(socket);
			Transport.write(new Message(Message.EXIT), socket);
		}
		
		private void handleForbidden(Socket socket) throws IOException {
			Transport.write(new Message(Message.FORBIDDEN), socket);
		}
	}
}
