package demo.viewer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class Response implements Serializable {

	private static final long serialVersionUID = 1L;
	private byte[] data;
	private transient Image image;
	
	public Response(Image image) {
		data = image.getImageData().data;
	}
	
	public Image getImage() {
		if (image == null) {
			InputStream stream = new ByteArrayInputStream(data);
			ImageData imageData = new ImageData(stream);
			image = new Image(Display.getDefault(), imageData);
		}
		return image;
	}
	
	public byte[] getData() {
		return data;
	}
}
