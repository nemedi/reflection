package demo.chatnio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Transport {

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T read(SocketChannel socketChannel) throws IOException, ClassNotFoundException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.SIZE / 8);
		socketChannel.read(byteBuffer);
		while (byteBuffer.remaining() > 0) {	
		}
		byteBuffer = ByteBuffer.allocate(byteBuffer.getInt(0));
		socketChannel.read(byteBuffer);
		while (byteBuffer.remaining() > 0) {
		}
		ObjectInputStream objectInputStream = new ObjectInputStream(
				new ByteArrayInputStream(byteBuffer.array()));
		return (T) objectInputStream.readObject();
	}
	
	public static <T extends Serializable> void write(T object, SocketChannel socketChannel) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    for (int i = 0; i < Integer.SIZE / 8; i++) {
	    	byteArrayOutputStream.write(0);
	    }
	    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
	    objectOutputStream.writeObject(object);
	    objectOutputStream.close();
	    final ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
	    byteBuffer.putInt(0, byteArrayOutputStream.size() - Integer.SIZE / 8);
	    socketChannel.write(byteBuffer);
	}

}
