import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.landray.kmss.plugin.sign.SignParamStorer;
import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LicenseDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField accountField;
    private JTextField passwordField;
    private AnActionEvent event;
    private JCheckBox checkBox;
    private JTextField versionField;
    private JPanel initPanel;

    private static final String[] SAVEKEYS = { "username",
            "savePassword", "password" };

    public LicenseDialog(final AnActionEvent event) {
        this.event = event;

        setTitle("请输入帐号信息");
        setContentPane(contentPane);

        Map result = SignParamStorer.getInstance().load(
                SAVEKEYS,event.getProject().getBasePath()+File.separator+".idea");

        if(result.size() != 0){
            checkBox.setSelected(Boolean.valueOf((String)result.get("savePassword")));
            accountField.setText((String)result.get("username"));
            passwordField.setText((String)result.get("password"));
        }

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
            // 模块对象
            Module module = event.getData(DataKeys.MODULE);

            // 条件校验
            if (null == module) {
                Messages.showErrorDialog(this, "请选择项目再下载License", "Error");
                return;
            }

            if (null == versionField.getText() || "".equals(versionField.getText())) {
                Messages.showErrorDialog(this, "请输入版本号", "Error");
                return;
            }

            if (null == accountField.getText() || "".equals(accountField.getText())) {
                Messages.showErrorDialog(this, "请输入SVN帐号", "Error");
                return;
            }

            if (null == passwordField.getText() || "".equals(passwordField.getText())) {
                Messages.showErrorDialog(this, "请输入SVN密码", "Error");
                return;
            }

            //如果勾选保存，则保存信息
            if(checkBox.isSelected()){
                Map map = new HashMap();
                map.put("licenseFile",versionField.getText());
                map.put("licenseFiles",versionField.getText());
                map.put("username",accountField.getText());
                map.put("password",passwordField.getText());
                map.put("savePassword",String.valueOf(checkBox.isSelected()));
                SignParamStorer.getInstance().save(map,
                        SAVEKEYS,event.getProject().getBasePath()+File.separator+".idea");
            }
            // 项目目录
            String projectUrl = module.getProject().getBasePath();
            // License目录
            String webPath = projectUrl + File.separator + "WebContent" + File.separator + "WEB-INF" + File.separator + "KmssConfig";

            String rtnStr = LicenseGenerator.generator(versionField.getText(), accountField.getText(), passwordField.getText(), webPath);

            JSONObject result = JSONObject.fromObject(rtnStr);
            Messages.showMessageDialog(module.getProject(), (String) result.get("errorMessage"), "下载完成", Messages.getInformationIcon());
            if (!"false".equals(result.get("success"))) {
                dispose();
            }

        } catch (Throwable e) {
            Messages.showErrorDialog(this, "License下载失败", "Error");
            e.printStackTrace();
        }

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
        VirtualFile[] data = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        checkBox = new JCheckBox("保存", false);


    }
}
