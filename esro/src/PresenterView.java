package esro.src;

import java.awt.Canvas;
import java.io.File;

import javax.swing.JFrame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
public class PresenterView {

    static OleClientSite clientSite;
    static OleFrame frame;

    public static void main(String[] args) {
        final Display display = new Display();
        final Shell shell = new Shell(display);

        JFrame jframe=new JFrame("Mi jframe");
        final Canvas canvas=new Canvas();
        jframe.getContentPane().add(canvas);
        jframe.setSize(800, 600);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setVisible(true);

        display.asyncExec(new Runnable() {
            public void run() {
                Shell shell = SWT_AWT.new_Shell(display, canvas);
                shell.setSize(800, 600);

                shell.setText("Word Example");
                shell.setLayout(new FillLayout());
                try {
                    frame = new OleFrame(shell, SWT.NONE);
                    clientSite = new OleClientSite(frame, SWT.NULL, new File("test/control.doc"));
                    addFileMenu(frame);
                } catch (SWTError e) {
                    e.printStackTrace();
                    display.dispose();
                    return;
                }
                shell.open();

            }
        });

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    static void addFileMenu(OleFrame frame) {
        final Shell shell = frame.getShell();
        Menu menuBar = shell.getMenuBar();
        if (menuBar == null) {
            menuBar = new Menu(shell, SWT.BAR);
            shell.setMenuBar(menuBar);
        }
        MenuItem fileMenu = new MenuItem(menuBar, SWT.CASCADE);
        fileMenu.setText("&File");
        Menu menuFile = new Menu(fileMenu);
        fileMenu.setMenu(menuFile);
        frame.setFileMenus(new MenuItem[] { fileMenu });

        MenuItem menuFileOpen = new MenuItem(menuFile, SWT.CASCADE);
        menuFileOpen.setText("Open...");
        menuFileOpen.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                fileOpen();
            }
        });
        MenuItem menuFileExit = new MenuItem(menuFile, SWT.CASCADE);
        menuFileExit.setText("Exit");
        menuFileExit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
            }
        });
    }

    static void fileOpen() {
        FileDialog dialog = new FileDialog(clientSite.getShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] { "*.doc" });
        String fileName = dialog.open();
        if (fileName != null) {
            clientSite.dispose();
            clientSite = new OleClientSite(frame, SWT.NONE, "Word.Document", new File(fileName));
            clientSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
        }
    }
}
