package br.com.alcheno.buscadorfilmes.task;

public interface IInterface {
	
	public void processFinish(Object output);

	public void processFinish(Object output, int quantidade);
	
	public void processFinishErro(String output);
}
