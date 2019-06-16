import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class RegistMachineAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        RegistMachineDialog dialog = new RegistMachineDialog(e);
        dialog.pack();
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

    }


}