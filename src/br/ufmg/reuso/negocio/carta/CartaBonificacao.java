/*
 * Federal University of Minas Gerais 
 * Department of Computer Science
 * Simules-SPL Project
 *
 * Created by Michael David
 * Date: 16/07/2011
 */

package br.ufmg.reuso.negocio.carta;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Michael David
 *
 */

public class CartaBonificacao extends Carta
{
	private String referenciaBibliografica;
	private int duracaoEfeito;
	private int custoEfeito;
        
	/** contém quanto de efeito será gerado.*/
	private int quantidadePrimeiroEfeito;	
        
        /** contém quanto de efeito será gerado.*/
	private int quantidadeSegundoEfeito;
        
        /** será igual a uma das constantes de efeitos oriundos de carta conceito de Constants.java */
	private int tipoPrimeiroEfeito; 									
	
        /** será igual a uma das constantes de efeitos oriundos de carta conceito de Constants.java*/ 
        private int tipoSegundoEfeito;										
	
	/**
	 * Construtor que inicializa a Carta com tudo zerado.
	 */
	public CartaBonificacao (){
            
		//inicializando a superclasse explicitamente, texto significa a descricao do efeito ao utlizar a carta
		super (null, null, null, CONCEITO);												
		setReferenciaBibliografica(null);
		setDuracaoEfeito(0);
		setCustoEfeito(0);
		setQuantidadePrimeiroEfeito(0);
		setQuantidadeSegundoEfeito(0);
		setTipoPrimeiroEfeito(0);
		setTipoSegundoEfeito(0);
	}
	
	//construindo a carta de conceito
	public CartaBonificacao (String titulo,
			                 String codigo,
			                 String texto,
			                 String referencia,
			                 int duracao, 
			                 int custo,
			                 int efeito1,
			                 int efeito2,
			                 int quantidade1,
			                 int quantidade2)

	{
		//inicializando a superclasse explicitamente, texto significa a descricao do efeito ao utlizar a carta
		super (titulo, codigo, texto,CONCEITO);												
		setReferenciaBibliografica(referencia);
		setDuracaoEfeito(duracao);
		setCustoEfeito(custo);
		setQuantidadePrimeiroEfeito(quantidade1);
		setQuantidadeSegundoEfeito(quantidade2);
		setTipoPrimeiroEfeito(efeito1);
		setTipoSegundoEfeito(efeito2);
	}
	
	@Override
	public void mostrarCarta()
	{
            // printa carta
	}
	
	public void inserirEfeito()					
	{
		if (codigoCarta=="a")
		{
			//TODO[MD]para cada codigo, descrever o que será feito
		}
	}
	
	public int getDuracaoEfeito() 
	{
		return duracaoEfeito;
	}

	public void setDuracaoEfeito(int duracaoEfeito) 
	{
		this.duracaoEfeito = duracaoEfeito;
	}

	public int getCustoEfeito() 
	{
		return custoEfeito;
	}

	public void setCustoEfeito(int custoEfeito) 
	{
		this.custoEfeito = custoEfeito;
	}

	public String getReferenciaBibliografica() 
	{
		return referenciaBibliografica;
	}

	public void setReferenciaBibliografica(String referenciaBibliografica) 
	{
		this.referenciaBibliografica = referenciaBibliografica;
	}

	public int getQuantidadePrimeiroEfeito()
	{
		return quantidadePrimeiroEfeito;
	}

	public void setQuantidadePrimeiroEfeito(int quantidadePrimeiroEfeito) 
	{
		this.quantidadePrimeiroEfeito = quantidadePrimeiroEfeito;
	}

	public int getQuantidadeSegundoEfeito() 
	{
		return quantidadeSegundoEfeito;
	}

	public void setQuantidadeSegundoEfeito(int quantidadeSegundoEfeito)
	{
		this.quantidadeSegundoEfeito = quantidadeSegundoEfeito;
	}

	public int getTipoPrimeiroEfeito() 
	{
		return tipoPrimeiroEfeito;
	}

	public void setTipoPrimeiroEfeito(int tipoPrimeiroEfeito) 
	{
		this.tipoPrimeiroEfeito = tipoPrimeiroEfeito;
	}

	public int getTipoSegundoEfeito() 
	{
		return tipoSegundoEfeito;
	}

	public void setTipoSegundoEfeito(int tipoSegundoEfeito)
	{		
		this.tipoSegundoEfeito = tipoSegundoEfeito;
	}
	
	public void dumpProperties(String path) {
		try {
			FileWriter writer = new FileWriter(path, true);
			writer.write("codigo = " + this.codigoCarta + "\n");
			writer.write("titulo = " + this.tituloCarta + "\n");
			writer.write("texto = " + this.textoCarta + "\n");
			writer.write("referenciaBibliografica = " + this.referenciaBibliografica + "\n");
			writer.write("duracaoEfeito = " + this.duracaoEfeito + "\n");
			writer.write("custo = " + this.custoEfeito + "\n");
			writer.write("quantidadePrimeiroEfeito = " + this.quantidadePrimeiroEfeito + "\n");
			writer.write("quantidadeSegundoEfeito = " + this.quantidadeSegundoEfeito + "\n");
			writer.write("tipoPrimeiroEfeito = " + this.tipoPrimeiroEfeito + "\n");
			writer.write("tipoSegundoEfeito = " + this.tipoSegundoEfeito + "\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block			
		}
	}
}
