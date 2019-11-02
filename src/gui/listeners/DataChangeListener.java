package gui.listeners;

public interface DataChangeListener {
	// permite que um obj1 escute um event de outro obj2
	// quando obj1 fazer um evento  o obj2 vai recebe através do observer
	
	void onDataChanged(); // evento para ser chamado quando os dados mudarem
	
}
