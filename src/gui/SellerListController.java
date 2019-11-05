package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener{
	// controla os componentes do SellerList.fxml
	// classe observer(recebe lista de eventos para atualizar)
	
	private SellerService service; // SellerService para salvar infos no BD
	
	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId; // pega coluna de ID's
	
	@FXML
	private TableColumn<Seller, String> tableColumnName; // pega coluna de nomes
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT; // pega coluna de botões de edição das linhas
	
	@FXML
	TableColumn<Seller, Seller> tableColumnREMOVE; // pega coluna para inserir botões de remover vendedor
	
	@FXML
	TableColumn<Seller, Seller> tableColumnEmail; //
	
	@FXML
	TableColumn<Seller, Date> tableColumnBirthDate; //
	
	@FXML
	TableColumn<Seller, Double> tableColumnBaseSalary; //
	
	@FXML
	private Button btNew; // pega o botao de novo departamento
	
	private ObservableList<Seller> obsList; // carrega os departamentos nessa observableList
	
	@FXML
	public void onBtNewAction(ActionEvent event) { 
		Stage parentStage = Utils.currentStage(event); // com event é capaz de acessar o stage onde está o botao
		Seller obj = new Seller(); // cria um novo obj ao clicar no botao NEW
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}
	
	//
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	// inicia o comportamento das colunas
	private void initializeNodes() {

		// informações do vendedor
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		
		// informações do vendedor
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy"); // formata data do aniversário do vendedor
		
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow(); // acessa a cena do Main
		
		// tableView fica na mesma altura da janela(preenche para baixo até o limite inferior da janela)
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
		
		
		
	}
	
	// metodo resposanvel por acessar o serviço, carregar os departamentos e jogar departamentos no observableList
	public void updateTableView() {
		
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		List<Seller> list = service.findAll(); // pega lista de departamentos
		obsList = FXCollections.observableArrayList(list); // pega a lista
		tableViewSeller.setItems(obsList); // recebe a lista de departamentos
		initEditButtons(); // botao de edição para cada linha
		initRemoveButtons(); // botao para deletar departamento
		
	}
	
	// cria a janela de dialogo
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load(); // carrega um painel do arquivo SellerForm.fxml
			
			// pega uma referencia ao SellerFormController
			SellerFormController controller = loader.getController();
			
			controller.setSeller(obj); // indica o obj para editar as infos
			controller.setServices(new SellerService(), new DepartmentService()); // cria um novo SellerService e DepartmentService para salvar infos no BD
			controller.loadAssociatedObjects(); // carrega comboBox com os departamentos
			controller.subscribeDataChangeListener(this); // recebe mudança dos eventos da classe SellerFormController
			controller.updateFormData(); // carrega as infos do obj no formulario
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane)); // nova cena
			dialogStage.setResizable(false); // nao deixa ser redimensionada
			dialogStage.initOwner(parentStage); // stage pai da cena
			dialogStage.initModality(Modality.WINDOW_MODAL); // ela fica trava e nao deixa acessar janela atrás
			dialogStage.showAndWait(); // ???
			
		}
		catch (IOException e) {
			e.printStackTrace();
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
		
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
		private final Button button = new Button("edit");
		
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				
				super.updateItem(obj, empty);
				
				if (obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				
				button.setOnAction(
					event -> createDialogForm(
					obj, "/gui/SellerForm.fxml",Utils.currentStage(event)));
			}
		});
		}
	
	// acrescenta um novo botão para remover o departamento da lista
	private void initRemoveButtons() {
		
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			 private final Button button = new Button("remove");
			 
			 @Override
			 protected void updateItem(Seller obj, boolean empty) {
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
	private void removeEntity(Seller obj) {
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
