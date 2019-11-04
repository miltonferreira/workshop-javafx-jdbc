package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener{
	// controla os componentes do DepartmentList.fxml <<<<<<<<<<<<
	// classe observer(recebe lista de eventos para atualizar)
	
	private DepartmentService service; // DepartmentService para salvar infos no BD
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId; // pega coluna de ID's
	
	@FXML
	private TableColumn<Department, String> tableColumnName; // pega coluna de nomes
	
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT; // pega coluna de botões de edição das linhas
	
	@FXML
	TableColumn<Department, Department> tableColumnREMOVE; // <<<<<<<<<<<<<<<<<<<<<<<
	
	@FXML
	private Button btNew; // pega o botao de novo departamento
	
	private ObservableList<Department> obsList; // carrega os departamentos nessa observableList
	
	@FXML
	public void onBtNewAction(ActionEvent event) { 
		Stage parentStage = Utils.currentStage(event); // com event é capaz de acessar o stage onde está o botao
		Department obj = new Department(); // cria um novo obj ao clicar no botao NEW
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}
	
	//
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	// inicia o comportamento das colunas
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow(); // acessa a cena do Main
		// tableView fica na mesma altura da janela(preenche para baixo até o limite inferior da janela)
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	// metodo resposanvel por acessar o serviço, carregar os departamentos e jogar departamentos no observableList
	public void updateTableView() {
		
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		List<Department> list = service.findAll(); // pega lista de departamentos
		obsList = FXCollections.observableArrayList(list); // pega a lista
		tableViewDepartment.setItems(obsList); // recebe a lista de departamentos
		initEditButtons(); // botao de edição para cada linha
		initRemoveButtons(); // botao para deletar departamento
		
	}
	
	// cria a janela de dialogo
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load(); // carrega um painel do arquivo DepartmentForm.fxml
			
			// pega uma referencia ao DepartmentFormController
			DepartmentFormController controller = loader.getController();
			
			controller.setDepartment(obj); // indica o obj para editar as infos
			controller.setDepartmentService(new DepartmentService()); // cria um novo DepartmentService para salvar infos no BD
			controller.subscribeDataChangeListener(this); // recebe mudança dos eventos da classe DepartmentFormController
			controller.updateFormData(); // carrega as infos do obj no formulario
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane)); // nova cena
			dialogStage.setResizable(false); // nao deixa ser redimensionada
			dialogStage.initOwner(parentStage); // stage pai da cena
			dialogStage.initModality(Modality.WINDOW_MODAL); // ela fica trava e nao deixa acessar janela atrás
			dialogStage.showAndWait(); // ???
			
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
		
	}
	
	// faz atualizaçao dos eventos
	@Override
	public void onDataChanged() {
		
		updateTableView(); 
		
	}
	
	// acrescenta um novo botão com o texto edit, em cada linha da tabela, abrindo um formulario
	private void initEditButtons() {
		
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
		private final Button button = new Button("edit");
		
			@Override
			protected void updateItem(Department obj, boolean empty) {
				
				super.updateItem(obj, empty);
				
				if (obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				
				button.setOnAction(
					event -> createDialogForm(
					obj, "/gui/DepartmentForm.fxml",Utils.currentStage(event)));
			}
		});
		}
	
	// acrescenta um novo botão para remover o departamento da lista
	private void initRemoveButtons() {
		
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			 private final Button button = new Button("remove");
			 
			 @Override
			 protected void updateItem(Department obj, boolean empty) {
				 super.updateItem(obj, empty);
				 
				 if (obj == null) {
					 setGraphic(null);
					 return;
				 }
				 
				 setGraphic(button);
				 button.setOnAction(event -> removeEntity(obj));
			 }
		 });
		}
	
	// remove um departamento da tabela
	private void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		// quer deletar o departamento
		if(result.get() == ButtonType.OK) {
			
			if(service == null) {
				throw new IllegalStateException("Service was null"); // se acontecer exceção, é porque o programador esquece de ejetar dependencia da classe
			}
			
			try {
				service.remove(obj);
				updateTableView(); // atualiza dados da tabela
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
		
	}
	
}
