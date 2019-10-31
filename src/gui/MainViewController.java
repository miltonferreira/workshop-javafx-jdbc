package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable{
	// Classe utilizada para controlar os botoes do Menu <<<<<<<<<<<<
	
	@FXML
	private MenuItem menuItemSeller; // controla o botao do vendedor
	
	@FXML
	private MenuItem menuItemDepartment; // controla o botao do departamento
	
	@FXML
	private MenuItem menuItemAbout; // controla o botao do about
	
	// event handler
	@FXML
	public void OnMenuItemSellerAction() {
		System.out.println("OnMenuItemSellerAction");
	}
	
	@FXML
	public void OnMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		}); // carrega a tela com lista de departamentos com função de inicialização do updateTableView
	}
	
	@FXML
	public void OnMenuItemAboutAction() {
		loadView("/gui/About.fxml", x ->{}); // carrega a tela de About com função de inicialização vazia
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		
		
	}
	
	// funçao para abrir outra janela
	// com synchronized garante que o codigo todo seja executado, evitando que seja parado na thread
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load(); // carrega a tela de About
			
			// mostra view dentro da janela principal
			Scene mainScene = Main.getMainScene();  // pega uma referencia da scene
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); // pega uma referencia para o VBox da cena principal
			
			Node mainMenu = mainVBox.getChildren().get(0); // pega uma referencia ao menu da cena principal
			mainVBox.getChildren().clear(); // limpa o VBox da cena principal
			
			mainVBox.getChildren().add(mainMenu); // adiciona o menu novamente
			mainVBox.getChildren().addAll(newVBox.getChildren()); // adiciona a janela About
			
			// segundo paramentro do metodo "initializingAction"
			T controller = loader.getController(); // retorna o controlador do tipo que for chamado
			initializingAction.accept(controller); // executa o controlador
			
			/*/ atualiza o tableView manualmente --------------------------------------------------------------------------
			DepartmentListController controller = loader.getController(); // pegando referencia do controller da View
			controller.setDepartmentService(new DepartmentService()); // injeta dependencia no controller do SceneBuilder
			controller.updateTableView(); // força atualização com as informações dos departamentos na tableView
			// atualiza o tableView manualmente --------------------------------------------------------------------------
			*/
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error Loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
