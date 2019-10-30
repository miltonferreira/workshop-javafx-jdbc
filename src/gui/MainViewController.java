package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

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
		System.out.println("OnMenuItemDepartmentAction");
	}
	
	@FXML
	public void OnMenuItemAboutAction() {
		System.out.println("OnMenuItemAboutAction");
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		
		
	}

}
