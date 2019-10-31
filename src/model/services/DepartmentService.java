package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {

	// retorna uma lista de todos departamentos
	public List<Department> findAll(){
		
		List<Department> list = new ArrayList<Department>();
		// dados mockados - inserção provisoria como teste
		list.add(new Department(1, "Books"));
		list.add(new Department(2, "Computers"));
		list.add(new Department(3, "Electronics"));
		
		return list;
	}
	
}
