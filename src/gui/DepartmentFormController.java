package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable{
	// controla janela do DepartmentForm.fxml <<<<<<<<<<<<< 

	private Department entity;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	// Recebe um Department para fazer altera�oes nas suas info's
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	// M�todos para tratar eventos dos TextField's, Label e Button's ----------------
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("onBtSaveAction");
	}
	
	@FXML
	public void onBtCancelAction() {
		System.out.println("onBtCancelAction");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	// restricoes dos TextField's, Label
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId); // aceita somente inteiros
		Constraints.setTextFieldMaxLength(txtName, 30); // aceita somente 30 caracteres no texto
	}
	
	// insere as infos na janela do DepartmentForm.fxml
	public void updateFormData() {
		
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId())); // mostra o ID
		txtName.setText(entity.getName()); // mostra o nome
	}

}
