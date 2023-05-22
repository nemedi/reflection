package demo.viewer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Server {
	
	public interface ServerListener {
		
		void onAdd(InetSocketAddress address);
		
		void onRemove(InetSocketAddress address);
	}
	
	private Display display;
	private ServerListener listener;
	private Set<InetSocketAddress> addresses;
	private DatagramSocket datagramSocket;
	private Timer timer;
	
	public Server(Display dislay, ServerListener listener) {
		this.display = dislay;
		this.listener = listener;
	}

	public void start(int port) throws IOException {
		stop();
		InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), port);
		datagramSocket = new DatagramSocket(address);
		ServerWorker worker = new ServerWorker();
		addresses = new HashSet<InetSocketAddress>();
		new Thread(worker).start();
		display = Display.getDefault();
		timer = new Timer();
		timer.scheduleAtFixedRate(getTimerTask(), 0, 1000);
	}
	
	private TimerTask getTimerTask() {
		return new TimerTask() {
			
			@Override
			public void run() {
				display.asyncExec(() -> {
					GC gc = new GC(display);
			        final Image image = new Image(display, display.getBounds());
			        gc.copyArea(image, 0, 0);
			        gc.dispose();
			        final Response response = new Response(image);
					image.dispose();
					synchronized (addresses) {
						for (Iterator<InetSocketAddress> iterator = addresses.iterator(); iterator.hasNext();) {
							try {
								InetSocketAddress address = iterator.next();
								Transport.send(response, new DatagramSocket(), address);
							} catch (IOException e) {
								iterator.remove();
							}
						}
					}
				});
				
			}
		};
	}

	public void stop() throws IOException {
		if (timer != null) {
			timer.cancel();
		}
		if (datagramSocket != null && !datagramSocket.isClosed()) {
			datagramSocket.close();
		}
		timer = null;
		datagramSocket = null;
	}

	public class ServerWorker implements Runnable {
	
		@Override
		public void run() {
			while (datagramSocket != null && !datagramSocket.isClosed()) {
				try {
					Request request = Transport.receive(datagramSocket);
					InetSocketAddress address = request.getAddress();
					synchronized (addresses) {
						switch (request.getType()) {
						case Request.ADD:
							if (!addresses.contains(address)) {
								addresses.add(address);
								display.asyncExec(() -> listener.onAdd(address));
							}
							break;
						case Request.REMOVE:
							if (addresses.contains(address)) {
								addresses.remove(address);
								display.asyncExec(() -> listener.onRemove(address));
							}
							break;
						}
					}	
				} catch (IOException | ClassNotFoundException e) {
					continue;
				}
			}
		}
	}
}
