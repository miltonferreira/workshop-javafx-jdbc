package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException{
	// exceção para validar o formulario de DepartmentForm.fxml
	
	private static final long serialVersionUID = 1L;
	
	// guarda os erros de cada campo do formulario
	private Map<String, String> errors = new HashMap<>();
	
	public ValidationException(String str) {
		super(str);
	}
	
	public Map<String, String> getErrors(){
		return errors;
	}
	
	public void addError(String fieldName, String errorMessage) {
		errors.put(fieldName, errorMessage);
	}
	
}
