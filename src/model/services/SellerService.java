package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	// faz ponte com classes que acessam o BD para vendedor/seller
	
	private SellerDao dao = DaoFactory.createSellerDao(); // acessa no BD os vendedores/sellers

	// retorna uma lista de todos vendedores
	public List<Seller> findAll(){
		return dao.findAll(); // vai no BD e busca todos os vendedores
	}
	
	// salva ou atualiza vendedor do BD
	public void saveOrUpdate(Seller obj) {
		
		// se nao tem ID é um novo obj
		if(obj.getId() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj); // se tem ID atualiza as infos
		}
	}
	
	// delete vendedor do BD
	public void remove(Seller obj) {
		dao.deleteById(obj.getId());
	}
	
}
