package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{
	// controla os componentes do DepartmentList.fxml <<<<<<<<<<<<
	
	private DepartmentService service; //
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId; // pega coluna de ID's
	
	@FXML
	private TableColumn<Department, String> tableColumnName; // pega coluna de nomes
	
	@FXML
	private Button btNew; // pega o botao de novo departamento
	
	private ObservableList<Department> obsList; // carrega os departamentos nessa observableList
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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
		
	}

}
