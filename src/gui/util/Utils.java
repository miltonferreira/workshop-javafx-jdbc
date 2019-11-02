package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	// 
	
	// pega a janela para colocar outra janela encima
	public static Stage currentStage(ActionEvent event) {
		
		return (Stage)((Node)event.getSource()).getScene().getWindow(); // pega a janela que o botao está 
		
	}
	
	// converte texto para inteiro
	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			return null; // se nao for um inteiro valido retorna null
		}
	}
	
}
