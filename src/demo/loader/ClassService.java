package demo.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

import demo.remoting.RpcException;

public class ClassService implements IClassService {
	
	private static String directory;
	
	public static void setDirectory(String directory) {
		if (!directory.endsWith(File.separator)) {
			directory += File.separator;
		}
		ClassService.directory = directory;
	}

	@Override
	public String getBytecode(String name) throws RpcException {
		try {
			String path = directory + name.substring(name.lastIndexOf(".") + 1) + ".class";
			FileInputStream inputStream = new FileInputStream(path);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = inputStream.read(buffer, 0, buffer.length)) > 0) {
				outputStream.write(buffer, 0, count);
			}
			inputStream.close();
			outputStream.close();
			return Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}

}
