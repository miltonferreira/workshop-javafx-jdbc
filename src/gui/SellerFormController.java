package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable{
	// controla janela do SellertForm.fxml
	// classe subject(emite o evento)

	private Seller entity; // entidades do departamento
	
	private SellerService service; // ponte com BD para acessar vendedores
	
	private DepartmentService departmentService; // ponte com BD para acessar departamentos
	
	// permite que objs se inscreverem e receber o evento
	private List<DataChangeListener> dataChangeListeners = new ArrayList<DataChangeListener>();
	
	// campos de texto ---------------------------------------------------------------
	@FXML
	private TextField txtId; // texto de ID do vendedor
	
	@FXML
	private TextField txtName; // texto de nome do vendedor
	
	@FXML
	private TextField txtEmail; // texto de email do vendedor
	
	@FXML
	private DatePicker dpBirthDate; // texto de niver do vendedor
	
	@FXML
	private TextField txtBaseSalary; // texto de salario do vendedor
	// campos de texto ---------------------------------------------------------------
	
	@FXML
	private ComboBox<Department> comboBoxDepartment; // mostra lista de departamentos do vendedor
	
	// campos de errors --------------------------------------------------------------
	@FXML
	private Label labelErrorName; // texto de erro
	
	@FXML
	private Label labelErrorEmail; // texto de erro
	
	@FXML
	private Label labelErrorBirthDate; // texto de erro
	
	@FXML
	private Label labelErrorBaseSalary; // texto de erro
	// campos de errors --------------------------------------------------------------
	
	// botoes da View ----------------------------------------------------------------
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	// botoes da View ----------------------------------------------------------------
	
	private ObservableList<Department> obsList; // carrega lista dos departamentos no BD
	
	// Recebe um Seller para fazer alteraçoes nas suas info's
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	// faz alteraçoes de infos no banco de dados
	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
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
		
		// NOME --------------------------------------------------------------------------------
		// trim() elimita espaço em branco no começo e final
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can`t be empty");
		}
		
		obj.setName(txtName.getText());
		// NOME --------------------------------------------------------------------------------
		
		// EMAIL -------------------------------------------------------------------------------
		// trim() elimita espaço em branco no começo e final
		if(txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can`t be empty");
		}
		
		obj.setEmail(txtEmail.getText());
		// EMAIL -------------------------------------------------------------------------------
		
		// NIVER -------------------------------------------------------------------------------
		
		if(dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can`t be empty");
		}else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		// NIVER -------------------------------------------------------------------------------
		
		// Salario -----------------------------------------------------------------------------
		// trim() elimita espaço em branco no começo e final
		if(txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field can`t be empty");
		}
		
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		// Salario -----------------------------------------------------------------------------
		
		// Departamento ------------------------------------------------------------------------
		
		obj.setDepartment(comboBoxDepartment.getValue()); // pega o departamento do vendedor
		
		// Departamento ------------------------------------------------------------------------
		
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
		Constraints.setTextFieldMaxLength(txtName, 70); // aceita somente 70 caracteres no texto
		Constraints.setTextFieldDouble(txtBaseSalary); // aceita somente double
		Constraints.setTextFieldMaxLength(txtEmail, 60); // aceita somente 60 caracteres no email
		
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy"); // formata a data de niver do vendedor
		
		initializeComboBoxDepartment(); // inicia o comboBox com os departamentos
		
	}
	
	// insere as infos na janela do SellerForm.fxml
	public void updateFormData() {
		
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId())); // mostra o ID
		txtName.setText(entity.getName()); // mostra o nome
		txtEmail.setText(entity.getEmail()); // mostra o email
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary())); // mostra o salario
		
		// caso exista uma data de niver, faz a conversão
		if(entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault())); //zone pega o fuso-horario do computador do usuario
		}
		
		if(entity.getDepartment() == null) {
			//se for um novo vendedor coloca o primeiro departamento da lista
			comboBoxDepartment.getSelectionModel().selectFirst();
		}else {
			// preenche o comboBox com o departamento do vendedor existente
			comboBoxDepartment.setValue(entity.getDepartment());	
		}
		
	}

	// carrega comboBox com os departamentos
	public void loadAssociatedObjects() {
		
		if(departmentService == null) {
			throw new IllegalStateException("departmentService was null");
		}
		List<Department> list = departmentService.findAll(); // carrega todos departamentos
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList); // adiciona departamentos no comboBox
	}
	
	// controla labelErrorName do formulario
	private void setErrorMessage(Map<String, String> errors) {
		
		Set<String> fields = errors.keySet();
		
		// condição ternaria melhor que if/else
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : "")); // true = mensagem, false = mostra mensagem no label
		
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : "")); // true = mensagem, false = mostra mensagem no label
		
		labelErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : "")); // true = mensagem, false = mostra mensagem no label
		
		// sem condição ternaria
		if(fields.contains("birthDate")) {
			labelErrorBirthDate.setText(errors.get("birthDate")); // mostra mensagem no label
		} else {
			labelErrorBirthDate.setText(""); // mostra mensagem vazia no label
		}
		
	}
	
	// inicia o comboBox com os departamentos
	private void initializeComboBoxDepartment() {
		
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			 @Override
			 protected void updateItem(Department item, boolean empty) {
				 super.updateItem(item, empty);
				 setText(empty ? "" : item.getName());
			 }
		};
		
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
	
}
