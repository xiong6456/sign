import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.landray.kmss.plugin.sign.SignParamStorer;
import com.landray.kmss.plugin.util.DESUtil;
import com.landray.kmss.plugin.util.FileUtil;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatcherDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField savePath;
    private JButton fileChooseBtn;
    private JPanel filePanel;
    private JTextField webName;
    private JTextField svnAddr;
    private JTextField svnAccont;
    private JTextField svnPassword;
    private JCheckBox checkBox;
    private AnActionEvent event;
    private JBList fieldList;
    private static final String[] SAVEKEYS = { "username",
            "savePassword", "password" };

    PatcherDialog(final AnActionEvent event) {
        this.event = event;
        setTitle("请选择签名文件");

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

        // 保存路径按钮事件
        fileChooseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userDir = System.getProperty("user.home");
                JFileChooser fileChooser = new JFileChooser(userDir + "/Desktop");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int flag = fileChooser.showOpenDialog(null);
                if (flag == JFileChooser.APPROVE_OPTION) {
                    savePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

    }

    private void onOK() {
        try {
            // 模块对象
            Module module = event.getData(DataKeys.MODULE);
            System.out.println(System.getProperty("user.dir"));
            String projectUrl = module.getProject().getBasePath();
            Map result = SignParamStorer.getInstance().load(
                    SAVEKEYS,event.getProject().getBasePath()+File.separator+".idea");
            String content = FileUtil.readFile(new File(projectUrl+File.pathSeparator+"sign.svn"));
            System.out.println(content);
            if(content != null && !"".equals(content)){

            }

            // 条件校验
            if (null == savePath.getText() || "".equals(savePath.getText())) {
                Messages.showErrorDialog(this, "请选择保存路径!", "Error");
                return;
            }

            if (null == webName.getText() || "".equals(webName.getText())) {
                Messages.showErrorDialog(this, "请选择输入保存后名称", "Error");
                return;
            }

            if (null == svnAddr.getText() || "".equals(svnAddr.getText())) {
                Messages.showErrorDialog(this, "请输入SVN地址", "Error");
                return;
            }

            if (null == svnAccont.getText() || "".equals(svnAccont.getText())) {
                Messages.showErrorDialog(this, "请输入SVN帐号", "Error");
                return;
            }

            if (null == svnPassword.getText() || "".equals(svnPassword.getText())) {
                Messages.showErrorDialog(this, "请输入SVN密码", "Error");
                return;
            }

            ListModel<VirtualFile> model = fieldList.getModel();
            if (model.getSize() == 0) {
                Messages.showErrorDialog(this, "请选择输出文件!", "Error");
                return;
            }

            if (checkBox.isSelected()) {
                /*File file= new File(projectUrl+File.pathSeparator+"sign.svn");
                if(!file.exists()){
                    file.createNewFile();
                }else{
                    file.delete();
                    file.createNewFile();
                }*/
                Map tmap = new HashMap();
                tmap.put("svnAddr",svnAddr.getText());
                tmap.put("svnAccont",svnAccont.getText());
                tmap.put("svnPassword",svnPassword.getText());
                FileUtil.writeFile(new File(projectUrl+File.pathSeparator+"sign.svn"),tmap.toString());
            }

            CompilerModuleExtension instance = CompilerModuleExtension.getInstance(module);
            // 编译目录
            String compilerOutputUrl = instance.getCompilerOutputPath().getPath();
            // JavaWeb项目的WebRoot目录
            String webPath = "/" + webName.getText() + "/";
            // 导出目录
            String exportPath = savePath.getText() + webPath;
            List<String> files = new ArrayList<String>();
            for (int i = 0; i < model.getSize(); i++) {
                VirtualFile element = model.getElementAt(i);
                String elementName = element.getName();
                String elementPath = element.getPath();
                files.add(elementName);
                /*if (elementName.endsWith(".java")) {
                    String className = File.separator + elementPath.split("/src/")[1].replace(".java", ".class");
                    File from = new File(compilerOutputUrl + className);
                    File to = new File(exportPath + "WEB-INF" + File.separator + "classes" + className);
                    FileUtil.copy(from, to);
                } else {
                    File from = new File(elementPath);
                    File to = new File(exportPath + elementPath.split(webPath)[1]);
                    FileUtil.copy(from, to);
                }*/
            }
            ClassSigner.generator(files, svnAddr.getText(), svnAccont.getText(), svnPassword.getText(), webPath);
            dispose();
        } catch (Throwable e) {
            Messages.showErrorDialog(this, "Create Patcher Error!", "Error");
            e.printStackTrace();
        }

        // add your code here

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        VirtualFile[] data = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        fieldList = new JBList(data);
        fieldList.setEmptyText("No File Selected!");
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fieldList);
        decorator.createPanel();
        filePanel = decorator.createPanel();

        //读取文件并写入弹框
//        DESUtil.decode()
    }
}