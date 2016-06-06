package autofindviews.popup.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.File;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.e4.compatibility.SelectionService;

import autofindviews.ConfigFrame;

public class OperHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		 IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		SelectionService service= (SelectionService) window.getSelectionService();
		ISelection selection =service.getSelection();
		File selectedFile=null;
		if(!selection.isEmpty()&&selection instanceof StructuredSelection)
		{
			StructuredSelection mySelection=(StructuredSelection)selection;
			Object selectObj=mySelection.getFirstElement();
			if(selectObj instanceof File)
			{
				selectedFile = (File)selectObj;
				
			}
		}
		if(selectedFile==null)
		{
			MessageDialog.openInformation(window.getShell(), "信息提示", "请选择文件(*.xml)");
		}else
		{
			ConfigFrame.createFrame(window.getShell(),selectedFile);
		}
		return null;
	}
}