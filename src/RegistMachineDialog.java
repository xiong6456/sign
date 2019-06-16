import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RegistMachineDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField localName;
    private JTextField svnPassword;
    private JTextField svnAccont;
    private JCheckBox checkBox;
    private AnActionEvent event;

    public RegistMachineDialog(final AnActionEvent event) {
        this.event = event;
        String address = null; //获得机器名称
        try {
            InetAddress addr = InetAddress.getLocalHost();
            address = addr.getHostName().toString();
            localName.setText(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        setTitle("本机信息注册");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here


        try {

            Project project = event.getData(PlatformDataKeys.PROJECT);

            if (null == svnAccont.getText() || "".equals(svnAccont.getText())) {
                Messages.showErrorDialog(this, "请输入SVN帐号", "Error");
                return;
            }

            if (null == svnPassword.getText() || "".equals(svnPassword.getText())) {
                Messages.showErrorDialog(this, "请输入SVN密码", "Error");
                return;
            }

            String rtnReg = ClassSigner.regist(svnAccont.getText(),svnPassword.getText(),localName.getText(),String.valueOf(checkBox.isSelected()));
            Messages.showMessageDialog(project, rtnReg,"注册结果", Messages.getInformationIcon());

            if(rtnReg != null && !"".equals(rtnReg)){
                return;
            }

            dispose();

            String title = "本机注册";
            String msg = "注册成功，您现在可以进入SCM系统，激活当前账号使用的机器点击确认，进入SCM系统（仅限于Windows操作系统）";
            Messages.showMessageDialog(project, msg, title, Messages.getInformationIcon());


            browse("http://product.landray.com.cn/moduleindex.jsp?nav=/prod/scm/tree.jsp");
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void browse(String url) throws Exception {
        //获取操作系统的名字
        String osName = System.getProperty("os.name", "");
        if (osName.startsWith("Mac OS")) {
            //苹果的打开方式
            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
            openURL.invoke(null, new Object[]{url});
        } else if (osName.startsWith("Windows")) {
            //windows的打开方式。
            /*String browspath = System.getProperty("brows.path");
            try {
                if(browspath != null){
                    Runtime.getRuntime().exec("browspath " + url);
                }
                //Runtime.getRuntime().exec("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe " + url);
            } catch (Exception e) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
            */
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);

        } else {
            // Unix or Linux的打开方式
            String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++)
                //执行代码，在brower有值后跳出，
                //这里是如果进程创建成功了，==0是表示正常结束。
                if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0)
                    browser = browsers[count];
            if (browser == null)
                throw new Exception("Could not find web browser");
            else
                //这个值在上面已经成功的得到了一个进程。
                Runtime.getRuntime().exec(new String[]{browser, url});
        }
    }

}
