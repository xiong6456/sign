import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;

public class LicenseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile[] data = e.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        // TODO: insert action logic here
        LicenseDialog dialog = new LicenseDialog(e);
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.requestFocus();
    }
}