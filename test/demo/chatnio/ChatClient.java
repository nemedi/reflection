package demo.chatnio;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import demo.chatnio.Client;
import demo.chatnio.Client.ClientListener;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class ChatClient extends Shell implements ClientListener {
	
	private static final String LOGIN = "Login";
	private static final String LOGOUT = "Logout";
	
	private Text txtName;
	private Text txtMessage;
	private Client client;
	private List lstNames;
	private List lstMessages;
	private Button btnSend;
	private Button btnLogin;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			ChatClient shell = new ChatClient(display, Settings.HOST, Settings.PORT);
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
	 */
	public ChatClient(Display display, String host, int port) {
		super(display, SWT.SHELL_TRIM);
		addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					if (LOGOUT.equals(btnLogin.getText())) {
						client.logout();
					}
					client.disconnect();
				} catch (IOException exception) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", exception.getMessage());
				}
			}
		});
		client = new Client(this);
		setLayout(new GridLayout(3, false));
		
		Label lblName = new Label(this, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name");
		
		txtName = new Text(this, SWT.BORDER);
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					login();
				}
			}
		});
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnLogin = new Button(this, SWT.NONE);
		btnLogin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				login();
			}
		});
		btnLogin.setText(LOGIN);
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		lstNames = new List(sashForm, SWT.BORDER);
		lstNames.setEnabled(false);
		
		lstMessages = new List(sashForm, SWT.BORDER);
		lstMessages.setEnabled(false);
		sashForm.setWeights(new int[] {153, 475});
		
		Label lblMessage = new Label(this, SWT.NONE);
		lblMessage.setEnabled(false);
		lblMessage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMessage.setText("Message");
		
		txtMessage = new Text(this, SWT.BORDER);
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					send();
				}
			}
		});
		txtMessage.setEnabled(false);
		txtMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnSend = new Button(this, SWT.NONE);
		btnSend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				send();
			}
		});
		btnSend.setEnabled(false);
		btnSend.setText("Send");
		createContents();
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try {
					client.connect(host, port);
				} catch (IOException e) {
					MessageDialog.openError(ChatClient.this, "Error", e.getMessage());
				}
				
			}
		});
	}

	private void login() {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (LOGIN.equals(btnLogin.getText())) {
						if (txtName.getText().trim().length() > 0) {
							client.login(txtName.getText().trim());
						}
					} else {
						client.logout();
					}
				} catch (IOException e) {
					MessageDialog.openError(ChatClient.this, "Error", e.getMessage());
				}
				
			}
		});
	}

	private void send() {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (lstNames.getSelectionIndex() >= 0 && txtMessage.getText().trim().length() > 0) {
						client.send(lstNames.getItems()[lstNames.getSelectionIndex()],
								txtMessage.getText().trim());
						txtMessage.setText("");
					}
				} catch (IOException e) {
					MessageDialog.openError(ChatClient.this, "Error", e.getMessage());
				}
				
			}
		});
		
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Chat");
		setSize(640, 480);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void onAccept(String[] names) {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				txtName.setEnabled(false);
				btnLogin.setText(LOGOUT);
				lstNames.setItems(names);
				lstNames.setEnabled(true);
				lstNames.setSelection(0);
				lstMessages.setEnabled(true);
				txtMessage.setEnabled(true);
				btnSend.setEnabled(true);
				txtMessage.setFocus();
			}
		});
		
	}

	@Override
	public void onDeny() {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				MessageDialog.openWarning(ChatClient.this, "Warning", "User already exists.");
			}
		});
	}

	@Override
	public void onAdd(String name) {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				lstNames.add(name);
			}
		});
	}

	@Override
	public void onRemove(String name) {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				lstNames.remove(name);
			}
		});
	}

	@Override
	public void onReceive(String from, String text) {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				lstMessages.add(MessageFormat.format("{0}: {1}", from, text));
			}
		});
	}

	@Override
	public void onExit() {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				txtName.setEnabled(true);
				btnLogin.setText(LOGIN);
				lstNames.setItems(new String[] {});
				lstNames.setEnabled(false);
				lstMessages.setItems(new String[] {});
				lstMessages.setEnabled(false);
				txtMessage.setText("");
				txtMessage.setEnabled(false);
				btnSend.setEnabled(false);
				txtName.setFocus();
			}
		});
	}

	@Override
	public void onForbidden() {
		getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				MessageDialog.openWarning(ChatClient.this, "Warning", "Your action was forbidden by the server.");
			}
		});
		
	}

}
