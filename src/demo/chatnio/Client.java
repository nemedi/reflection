package demo.chatnio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

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
	
	private ClientListener listener;
	private SocketChannel socketChannel;
	private Selector selector;
	
	public Client(ClientListener listener) { 
		this.listener = listener;
	}

	public void connect(String host, int port) throws UnknownHostException, IOException {
		disconnect();
		socketChannel = SocketChannel.open();
		socketChannel.socket().connect(new InetSocketAddress(host, port));
		socketChannel.configureBlocking(false);
		selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_READ, host + ":" + port);
		ClientWorker worker = new ClientWorker();
		new Thread(worker).start();
	}
	
	public void disconnect() throws IOException {
		if (selector != null && selector.isOpen()) {
			selector.close();
		}
		if (socketChannel != null && socketChannel.isOpen()) {
			socketChannel.close();
		}
		selector = null;
		socketChannel = null;
	}
	
	public void login(String name) throws IOException {
		Transport.write(new Message(Message.LOGIN, name), socketChannel);
	}
	
	public void send(String to, String text) throws IOException {
		Transport.write(new Message(Message.SEND, to, text), socketChannel);
	}
	
	public void logout() throws IOException {
		Transport.write(new Message(Message.LOGOUT), socketChannel);
	}

	private class ClientWorker implements Runnable {
	
		@Override
		public void run() {
			while (socketChannel != null && socketChannel.isOpen()) {
				try {
					if (selector.select() == 0) {
						continue;
					}
					for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
							iterator.hasNext();) {
						SelectionKey key = iterator.next();
						iterator.remove();
						if (!key.isValid()) {
							continue;
						}
						if (key.isReadable()) {
							handleRead(key);
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					continue;
				}
			}
		}
	
		private void handleRead(SelectionKey key) throws ClassNotFoundException, IOException {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			Message message = Transport.read(socketChannel);
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
		
	}
	
}
