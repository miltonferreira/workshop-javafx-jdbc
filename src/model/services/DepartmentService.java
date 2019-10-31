package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dao = DaoFactory.createDepartmentDao(); // acessa no BD os departamentos

	// retorna uma lista de todos departamentos
	public List<Department> findAll(){
		
		return dao.findAll(); // vai no BD e busca todos os departamentos
		
	}
	
}
