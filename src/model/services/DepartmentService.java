package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	// faz ponte com classes que acessam o BD para departamento
	
	private DepartmentDao dao = DaoFactory.createDepartmentDao(); // acessa no BD os departamentos

	// retorna uma lista de todos departamentos
	public List<Department> findAll(){
		return dao.findAll(); // vai no BD e busca todos os departamentos
	}
	
	// salva ou atualiza departamento do BD
	public void saveOrUpdate(Department obj) {
		
		// se nao tem ID é um novo obj
		if(obj.getId() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj); // se tem ID atualiza as infos
		}
	}
	
	// delete departamento do BD
	public void remove(Department obj) {
		dao.deleteById(obj.getId());
	}
	
}
