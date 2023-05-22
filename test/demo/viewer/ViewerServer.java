package demo.viewer;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import demo.viewer.Server.ServerListener;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class ViewerServer extends Shell implements ServerListener {
	
	private List list;
	private Server server;

	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			ViewerServer shell = new ViewerServer(display);
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

	public ViewerServer(Display display) throws IOException {
		super(display, SWT.SHELL_TRIM);
		addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					server.stop();
				} catch (IOException exception) {
					MessageDialog.openError(ViewerServer.this, "Error", exception.getMessage());
				}
			}
		});
		setLayout(new FillLayout(SWT.HORIZONTAL));
		list = new List(this, SWT.BORDER);
		createContents();
		server = new Server(display, this);
		server.start(Settings.REMOTE_PORT);
	}

	protected void createContents() {
		setText("Viewer Server");
		setSize(640, 480);
	}

	@Override
	protected void checkSubclass() {
	}

	@Override
	public void onAdd(InetSocketAddress address) {
		list.add(address.toString());
	}

	@Override
	public void onRemove(InetSocketAddress address) {
		list.remove(address.toString());
	}
}
