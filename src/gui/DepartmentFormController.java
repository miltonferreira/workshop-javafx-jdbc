package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	// controla janela do DepartmentForm.fxml <<<<<<<<<<<<< 

	private Department entity; // entidades do departamento
	
	private DepartmentService service; // fonte com BD
	
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
	
	// Recebe um Department para fazer alteraçoes nas suas info's
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	// faz alteraçoes de infos no banco de dados
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	// Métodos para tratar eventos dos TextField's, Label e Button's ----------------
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if(service == null) {
			// caso tenha esquecido de injetar dependencia
			throw new IllegalStateException("Service was null");
		}
		
		try {
			entity = getFormData();
			service.saveOrUpdate(entity); // salva infos do obj no BD
			
			Utils.currentStage(event).close(); // fecha janela atual
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	
	// pega dados nas caixas do formulario e cria departamento
	private Department getFormData() {
		
		// cria um novo obj das caixas do formulario
		Department obj = new Department();
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtName.getText());
		
		return obj;
		
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close(); // fecha janela atual sem salvar
	}
	
	// Métodos para tratar eventos dos TextField's, Label e Button's ----------------
	
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
