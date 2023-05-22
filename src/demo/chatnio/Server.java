package demo.chatnio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Server {
	
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private Map<SocketChannel, String> logins;
	
	public void start(int port) throws UnknownHostException, IOException {
		stop();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
		serverSocketChannel.configureBlocking(false);
		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		logins = new HashMap<SocketChannel, String>();
		ServerWorker worker = new ServerWorker();
		new Thread(worker).start();
	}
	
	public void stop() throws IOException {
		if (selector != null && selector.isOpen()) {
			selector.close();
		}
		if (serverSocketChannel != null
				&& serverSocketChannel.isOpen()) {
			serverSocketChannel.close();
		}
		selector = null;
		serverSocketChannel = null;
	}

	private class ServerWorker implements Runnable {

		@Override
		public void run() {
			while (serverSocketChannel != null && serverSocketChannel.isOpen()) {
				try {
					if (selector.select() == 0) {
						continue;
					}
					for (Iterator<SelectionKey> i = selector.selectedKeys().iterator();
							i.hasNext();) {
						SelectionKey key = i.next();
						i.remove();
						if (!key.isValid()) {
							continue;
						}
						if (key.isAcceptable()) {
							handleAccept(key);
						} else if (key.isReadable()) {
							handleRead(key);
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					continue;
				}
			}
			
		}
		
		private void handleAccept(SelectionKey key) throws IOException {
			SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
			socketChannel.configureBlocking(false);
			String attribute = socketChannel.socket().getInetAddress().toString()
					+ ":" + socketChannel.socket().getPort();
			socketChannel.register(selector, SelectionKey.OP_READ, attribute);
		}
		
		private void handleRead(SelectionKey key) throws IOException, ClassNotFoundException {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			Message message = Transport.read(socketChannel);
			switch (message.getType()) {
			case Message.LOGIN:
				handleLogin(socketChannel, message.getArguments()[0]);
				break;
			case Message.SEND:
				handleSend(socketChannel, message.getArguments()[0], message.getArguments()[1]);
				break;
			case Message.LOGOUT:
				handleLogout(socketChannel);
				break;
			default:
				handleForbidden(socketChannel);
				break;
			}
		}
		
		private boolean isLogged(SocketChannel socketChannel) {
			return logins.containsKey(socketChannel)
					&& logins.get(socketChannel) != null;
		}

		private void handleLogin(SocketChannel socketChannel, String name) throws IOException {
			synchronized (logins) {
				if (isLogged(socketChannel)) {
					handleForbidden(socketChannel);
					return;
				}
				if (logins.keySet().contains(name)) {
					Transport.write(new Message(Message.DENY), socketChannel);
					return;
				}
				List<String> names = new ArrayList<String>();
				names.add(Message.ALL);
				Message message = new Message(Message.ADD, name);
				for (Entry<SocketChannel, String> login : logins.entrySet()) {
					if (login.getKey() != socketChannel) {
						names.add(login.getValue());
						Transport.write(message, login.getKey());
					}
				}
				logins.put(socketChannel, name);
				Transport.write(new Message(Message.ACCEPT,
						names.toArray(new String[names.size()])),
						socketChannel);
			}
		}

		private void handleSend(SocketChannel socketChannel, String to, String text) throws IOException {
			synchronized (logins) {
				if (!isLogged(socketChannel)) {
					handleForbidden(socketChannel);
					return;
				}
				Message message = new Message(Message.RECEIVE, logins.get(socketChannel), text);
				if (Message.ALL.equals(to)) {
					for (Entry<SocketChannel, String> login : logins.entrySet()) {
						Transport.write(message, login.getKey());
					}
				} else {
					for (Entry<SocketChannel, String> login : logins.entrySet()) {
						if (to.equals(login.getValue())) {
							Transport.write(message, login.getKey());
							Transport.write(message, socketChannel);
							return;
						}
					}
					handleForbidden(socketChannel);
				}
			}
		}

		private void handleLogout(SocketChannel socketChannel) throws IOException {
			if (!isLogged(socketChannel)) {
				handleForbidden(socketChannel);
				return;
			}
			Message message = new Message(Message.REMOVE, logins.get(socketChannel));
			for (Entry<SocketChannel, String> login : logins.entrySet()) {
				if (login.getKey() != socketChannel) {
					Transport.write(message, login.getKey());
				}
			}
			logins.remove(socketChannel);
			Transport.write(new Message(Message.EXIT), socketChannel);
		}
		
		private void handleForbidden(SocketChannel socketChannel) throws IOException {
			Transport.write(new Message(Message.FORBIDDEN), socketChannel);
		}

	}

}
