package demo.viewer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Transport {

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T receive(DatagramSocket datagramSocket) throws IOException, ClassNotFoundException {
		byte[] buffer = new byte[Integer.SIZE / 8];
		DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
		datagramSocket.receive(datagramPacket);
		ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
		buffer = new byte[objectInputStream.readInt()];
		datagramPacket = new DatagramPacket(buffer, buffer.length);
		datagramSocket.receive(datagramPacket);
		objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
		return (T) objectInputStream.readObject();
	}
	
	public static <T extends Serializable> void send(T object, DatagramSocket datagramSocket, InetSocketAddress address)
			throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    for (int i = 0; i < Integer.SIZE / 8; i++) {
	    	byteArrayOutputStream.write(0);
	    }
	    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
	    objectOutputStream.writeObject(object);
	    objectOutputStream.close();
	    byte[] buffer = byteArrayOutputStream.toByteArray();
	    DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length, address);
	    datagramSocket.send(datagramPacket);
	    datagramSocket.close();
	}

}
