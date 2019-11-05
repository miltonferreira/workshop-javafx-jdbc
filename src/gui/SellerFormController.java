package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
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
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable{
	// controla janela do SellertForm.fxml <<<<<<<<<<<<<
	// classe subject(emite o evento)

	private Seller entity; // entidades do departamento
	
	private SellerService service; // fonte com BD
	
	// permite que objs se inscreverem e receber o evento
	private List<DataChangeListener> dataChangeListeners = new ArrayList<DataChangeListener>();
	
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
	
	// Recebe um Seller para fazer alteraçoes nas suas info's
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	// faz alteraçoes de infos no banco de dados
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	// add na lista dataChangeListeners
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
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
			
			notifyDataChangeListeners(); // manda as mudanças para lista de eventos
			
			Utils.currentStage(event).close(); // fecha janela atual
		}
		catch (ValidationException e) {
			setErrorMessage(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	
	// manda as mudanças para lista de eventos
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}

	// pega dados nas caixas do formulario e cria departamento
	private Seller getFormData() {
		
		// cria um novo obj das caixas do formulario
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		// trim() elimita espaço em branco no começo e final
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can`t be empty");
		}
		
		obj.setName(txtName.getText());
		
		// checa na coleçao de errors se existe algum dentro
		if(exception.getErrors().size() > 0) {
			throw exception; // lança excecao se tiver algum dentro do MAP
		}
		
		return obj;
		
	}
	
	// Botao que cancela e fecha a janela

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
	
	// insere as infos na janela do SellerForm.fxml
	public void updateFormData() {
		
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId())); // mostra o ID
		txtName.setText(entity.getName()); // mostra o nome
	}

	// controla labelErrorName do formulario
	private void setErrorMessage(Map<String, String> errors) {
		
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name")); // mostra mensagem no label
		}
		
	}
	
}
