package demo.viewer;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import demo.viewer.Client.ClientListener;

public class ViewerClient extends Shell implements ClientListener {
	
	private Image image;
	private Canvas canvas;
	private Client client;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			ViewerClient shell = new ViewerClient(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 * @throws IOException 
	 */
	public ViewerClient(Display display) throws IOException {
		super(display, SWT.SHELL_TRIM);
		addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					client.disconnect();
				} catch (IOException exception) {
					MessageDialog.openError(ViewerClient.this, "Error", exception.getMessage());
				}
			}
		});
		setLayout(new FillLayout(SWT.HORIZONTAL));
		canvas = new Canvas(this, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (image != null) {
					e.gc.drawImage(image, 0, 0);
				}
			}
		});
		createContents();
		client = new Client(this);
		client.connect(Settings.REMOTE_HOST, Settings.REMOTE_PORT, Settings.LOCAL_PORT);
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Viewer Client");
		setSize(640, 480);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void onRefresh(Image image) {
		this.image = image;
		this.canvas.redraw();
	}

}
