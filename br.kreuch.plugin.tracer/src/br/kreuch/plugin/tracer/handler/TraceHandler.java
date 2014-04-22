package br.kreuch.plugin.tracer.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.handlers.HandlerUtil;

import br.kreuch.plugin.tracer.factory.I18nConstantsFactory;
import br.kreuch.plugin.tracer.factory.JdbcConnectionFactory;
import br.kreuch.plugin.tracer.util.Util;

public class TraceHandler extends AbstractHandler {
	private Map<Long, String> useCases;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;

		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof ICompilationUnit) {
			try {				
				@SuppressWarnings("deprecation")
				ICompilationUnit workingCopy = (ICompilationUnit)((ICompilationUnit)firstElement).getWorkingCopy();
				
				String genFile = Util.getPackageNameForFilePath(((ICompilationUnit)firstElement).getPath().toString());
				long classId = JdbcConnectionFactory.getJdbcConnection().getObjectIdForGenFile(genFile);
				
				if (classId == 0L){
					JOptionPane.showMessageDialog(null, I18nConstantsFactory.getI18nConstantFor(I18nConstantsFactory.CLASS_NOT_FOUND_IN_EA));
				} else {
					List<Long> useCaseIds = this.selectUseCases(workingCopy);

					if (useCaseIds.size() > 0){
						if (this.createConnector(useCaseIds, classId)){
							this.writeToActiveEditorFile(workingCopy, useCaseIds);
						}
					}
				}
				
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		} else {
			MessageDialog.openInformation(shell, I18nConstantsFactory.getI18nConstantFor(I18nConstantsFactory.INFORMATION), I18nConstantsFactory.getI18nConstantFor(I18nConstantsFactory.SELECT_A_FILE));
		}
		
		return null;
	}

	protected String getPersistentProperty(IResource res, QualifiedName qn) {
		try {
			return res.getPersistentProperty(qn);
		} catch (CoreException e) {
			return "";
		}
	}

	protected void setPersistentProperty(IResource res, QualifiedName qn,
			String value) {
		try {
			res.setPersistentProperty(qn, value);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private final void populateUseCases(){
		this.useCases = JdbcConnectionFactory.getJdbcConnection().getUseCases();
	}
	
	@SuppressWarnings("deprecation")
	private final void writeToActiveEditorFile(ICompilationUnit activeFile, List<Long> useCaseIds){
		String strUseCases = Util.getListAsString(useCaseIds, "UC-");
		strUseCases = "/** <UseCases>" + strUseCases + " </UseCases> */\n\n";

		try {
			String source = activeFile.getSource();
			Document document = new Document(source);

			int offset;
			try {
				offset = document.search(0, "package", true, true, true);
			} catch (BadLocationException e1) {
				offset = 0;
			}

			ReplaceEdit edit = new ReplaceEdit(0, offset, strUseCases);

			activeFile.applyTextEdit(edit, null);
			activeFile.reconcile(ICompilationUnit.NO_AST, false, null, null);
			activeFile.commitWorkingCopy(false, null);
			activeFile.discardWorkingCopy();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	private final boolean createConnector(List<Long> useCaseIds, long classId){
		boolean result = JdbcConnectionFactory.getJdbcConnection().deleteExistingConnectors(useCaseIds, classId);		
		
		if (result){			
			for (Long id : useCaseIds){
				result = JdbcConnectionFactory.getJdbcConnection().createConnector(id, classId);
				
				if (!result){
					return false;
				}
			}			
		}
		
		return result;
	}
	
	@SuppressWarnings("deprecation")
	private final List<Long> selectUseCases(ICompilationUnit activeFile){
		this.populateUseCases();
		
		String alreadySelectedUseCases = "";
		List<Long> selectedUC = null;
		try {
			String source = activeFile.getSource();
			Document document = new Document(source);
			
			int offset;
			try {
				offset = document.search(0, "package", true, true, true);
				if (offset > 0){
					alreadySelectedUseCases = document.get(0, offset);
					selectedUC = Util.getStringAsList(alreadySelectedUseCases);
				} else {					
					selectedUC = new ArrayList<Long>();
				}
			} catch (BadLocationException e1) {
				
			}

			
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		
		String[] opcoes = new String[useCases.size()];
		Iterator<Long> it = useCases.keySet().iterator();
		int i = 0;
		while (it.hasNext()){
			Long item = it.next();
			opcoes[i++] = useCases.get(item);
		}

		int[] selectedIndices = new int[selectedUC.size()];
		int pos = 0;
		JList list = new JList(opcoes);
		for (int j = 0; j <	list.getModel().getSize(); j++){
			Long item = Util.getKeyByValue(useCases, (String)list.getModel().getElementAt(j));
			if (selectedUC.contains(item)){
				selectedIndices[pos++] = j;
			}
		}
		list.setSelectedIndices(selectedIndices);
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setSize(500, 200);
		scrollPane.getViewport().add(list);
		
		List<Long> result = new ArrayList<Long>();
		
		if (JOptionPane.showConfirmDialog(null, scrollPane, I18nConstantsFactory.getI18nConstantFor(I18nConstantsFactory.USE_CASES), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
			for (int x = 0; x < list.getSelectedIndices().length; x++) {
				Object useCase = list.getSelectedValues()[x];

				if ((useCase != null) && (!useCase.equals(""))) {
					useCase = Util.getKeyByValue(useCases, (String) useCase);
					result.add((Long) useCase);
				}
			}
		}

		return result;
	}
}
