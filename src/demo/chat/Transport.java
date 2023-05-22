package demo.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Transport {

	public static Message read(Socket socket) throws IOException {
		InputStream stream = socket.getInputStream();
		int type = stream.read();
		String[] arguments = new String[stream.read()];
		for (int i = 0; i < arguments.length; i++) {
			int length = stream.read();
			byte[] buffer = new byte[length];
			int count = 0;
			do {
				count += stream.read(buffer, count, length);
			} while (count < length);
			arguments[i] = new String(buffer);
		}
		return new Message(type, arguments);
	}
	
	public static void write(Message message, Socket socket) throws IOException {
		OutputStream stream = socket.getOutputStream();
		stream.write(message.getType());
		String[] arguments = message.getArguments();
		stream.write(arguments.length);
		for (int i = 0; i < arguments.length; i++) {
			stream.write(arguments[i].length());
			stream.write(arguments[i].getBytes());
		}
		stream.flush();
	}
	

}
