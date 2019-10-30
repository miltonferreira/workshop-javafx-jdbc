package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable{
	// controla os componentes do DepartmentList.fxml <<<<<<<<<<<<
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId; // pega coluna de ID's
	
	@FXML
	private TableColumn<Department, String> tableColumnName; // pega coluna de nomes
	
	@FXML
	private Button btNew; // pega o botao de novo departamento
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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

}
