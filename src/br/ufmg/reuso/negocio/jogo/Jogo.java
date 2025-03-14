/*
 * Federal University of Minas Gerais 
 * Department of Computer Science
 * Simules-SPL Project
 *
 * Created by Michael David
 * Date: 16/07/2011
 */

package br.ufmg.reuso.negocio.jogo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import br.ufmg.reuso.negocio.baralho.BaralhoArtefatosBons;
import br.ufmg.reuso.negocio.baralho.BaralhoArtefatosRuins;
import br.ufmg.reuso.negocio.baralho.BaralhoCartas;
import br.ufmg.reuso.negocio.baralho.factory.AbstractCreatorBaralhoArtefatos;
import br.ufmg.reuso.negocio.baralho.factory.CreatorBaralhoArtefatos;
import br.ufmg.reuso.negocio.carta.Artefato;
import br.ufmg.reuso.negocio.carta.CardsConstants;
import br.ufmg.reuso.negocio.carta.Carta;
//#ifdef ConceptCard
import br.ufmg.reuso.negocio.carta.CartaBonificacao;
//#endif
import br.ufmg.reuso.negocio.carta.CartaEngenheiro;
import br.ufmg.reuso.negocio.carta.CartaPenalizacao;
import br.ufmg.reuso.negocio.carta.CartaoProjeto;
import br.ufmg.reuso.negocio.dado.Dado;
import br.ufmg.reuso.negocio.jogador.Jogador;
import br.ufmg.reuso.negocio.mesa.Mesa;
import br.ufmg.reuso.negocio.mesa.Modulo;
import br.ufmg.reuso.negocio.questao.Questao;
import br.ufmg.reuso.negocio.questao.factory.BancoQuestoes;
import br.ufmg.reuso.negocio.questao.factory.BancoQuestoesAleatorio;
import br.ufmg.reuso.negocio.questao.factory.BancoQuestoesTopicos;
import br.ufmg.reuso.negocio.tabuleiro.SetupInteraction;
import br.ufmg.reuso.negocio.tabuleiro.Tabuleiro;
import br.ufmg.reuso.ui.ScreenInteraction;
import br.ufmg.reuso.ui.ScreenRescueQuestion;
/**
 * @author Michael David
 *
 */

public final class Jogo {
  private int contador = 0;
  private int habilidadeTemporaria = 0;

	private static Jogo jogo;

	public static enum Status {
		CONTINUE, WINNER_END
	};

	public static final int BARALHO_PRINCIPAL = 0;
	public static final int BARALHO_AUXILIAR = 1;
	public static final String FACIL = "facil";
	public static final String MODERADO = "moderado";
	public static final String DIFICIL = "dificil";
	public static final int NUMERO_TOTAL_ARTEFATOS = 400;

	/**
	 * numero total de artefatos, sendo metade bons(brancos) e metade
	 * ruins(cinzas)
	 */
	private static final int ANY_ARTIFACTS = -1;
	private static final int ALL_ARTIFACTS = 1000;

	private Status gameStatus;
	private Jogador[] jogadores;
	public CartaoProjeto projeto;
	private List<Questao> questoes;
	private BaralhoCartas[] baralhoCartas;
	private BaralhoArtefatosBons[] baralhoArtefatosBons;
	private BaralhoArtefatosRuins[] baralhoArtefatosRuins;
	public SetupInteraction setupController = ScreenInteraction.getScreenInteraction();
	public ConceptsInteraction conceptsInteraction;
	public Random sorteio;

	private Jogo() {
		this.conceptsInteraction = new ConceptsInteraction();
		this.sorteio = new Random();
	}

	public Jogador[] getJogadores() {
		return jogadores;
	}

	public void setJogadores(Jogador[] jogadores) {
		this.jogadores = jogadores;
	}

	public CartaoProjeto getProjeto() {
		return projeto;
	}

	public Status getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(Status gameStatus) {
		this.gameStatus = gameStatus;
	}

	public BaralhoCartas[] getBaralhoCartas() {
		return baralhoCartas;
	}

	public BaralhoArtefatosBons[] getBaralhoArtefatosBons() {
		return baralhoArtefatosBons;
	}

	public BaralhoArtefatosRuins[] getBaralhoArtefatosRuins() {
		return baralhoArtefatosRuins;
	}
	
	public List<Questao> getQuestoes() {
		return questoes;
	}

	public void setQuestoes(List<Questao> questoes) {
		this.questoes = questoes;
	}

	public static Jogo getJogo() {
		if (jogo == null) {
			jogo = new Jogo();
		}
		return jogo;
	}

	private void init() {
                
                // exibe GUI(tela) para
		int mode = setupController.exibirStart(); 
		
                
                // iniciar o jogo. Retorna 1
		// caso ha a necessidade de
		// configurar o jogo.
		// Retorna 0 caso contrario
		int dificuldade;
		//#ifdef ConceptCard
		int[] cartasConceito;
		//#endif
		int[] cartasProblema;
                
                // caso mode seja diferente do default
		if (mode != ModeGameConstants.MODE_DEFAULT) 
													
		{
                        //modo de jogo escolhido e inserido na variavel dificuldade
			dificuldade = setupController.inserirDificuldadeJogo(); 
																	
			//#ifdef ConceptCard
			cartasConceito = setupController.inserirCartasConceitoSelecionadas();
			//#endif
			cartasProblema = setupController.inserirCartasProblemaSelecionadas();
		} else {
			dificuldade = SetupInteraction.HARD;
			//#ifdef ConceptCard
			cartasConceito = new int[1];
			cartasConceito[0] = ModeGameConstants.ALL_CARDS_CONCEITO;
			//#endif
			cartasProblema = new int[1];
			cartasProblema[0] = ModeGameConstants.ALL_CARDS_PROBLEMA;
		}
                
                //insere nome dos jogadores no vetor de string
		String[] nomeJogadores = setupController.inserirNomesJogadores(); 
		switch (dificuldade) {
		case 1:
			configurarJogo(FACIL, nomeJogadores, 
					//#ifdef ConceptCard
					cartasConceito, 
					//#endif
					cartasProblema);
			break;
		case 2:
			configurarJogo(MODERADO, nomeJogadores, 
					//#ifdef ConceptCard
					cartasConceito, 
					//#endif
					cartasProblema);
			break;
		case 3:
			configurarJogo(DIFICIL, nomeJogadores, 
					//#ifdef ConceptCard
					cartasConceito,
					//#endif
					cartasProblema);
			break;
		}
		gameStatus = Status.CONTINUE;
	}

	public void start(Jogo jogoAtual) {
		int jogador = 0;
		jogo.init();

		while (getGameStatus() == Status.CONTINUE) 
		{
			setupController.exibirDefault(jogoAtual, getJogadores()[jogador]);
			diminuirDuracaoEfeitosTemporario(jogador);
                        
                         // **Acrescentando jogador, ha a troca de jogador *//*
			jogador++;
			if (jogador >= getJogadores().length) 
			{
                            
                                 // **Logo, retorna-se ao jogador inicial*//*
				jogador = 0;
				adicionarEfeitosFimTurno();
			}
		}

	}

	public void configurarJogo(String facilidade, String[] nomeJogadores, 
			//#ifdef ConceptCard
			int[] cartasConceito, 
			//#endif
			int[] cartasProblema) {

		/* F?brica de Baralhos de Artefatos */
		AbstractCreatorBaralhoArtefatos fabricaBaralhoArtefatos = new CreatorBaralhoArtefatos();		

		this.baralhoCartas = new BaralhoCartas[2];
		this.baralhoArtefatosBons = new BaralhoArtefatosBons[2];
		this.baralhoArtefatosRuins = new BaralhoArtefatosRuins[2];
		
		// #ifdef AllQuestions		
		this.questoes = new BancoQuestoes().criarBanco();
		// #endif
		
		// #ifdef RandomQuestions
//@		this.questoes = new BancoQuestoesAleatorio(5).criarBanco();
		// #endif
		
		// #ifdef TopicQuestions
//@		this.questoes = new BancoQuestoesTopicos("Arquitetura de Software").criarBanco();
		// #endif		

		// sortearProjeto(facilidade);
		projeto = new CartaoProjeto(facilidade);

		// formarBaralhoCarta(facilidade,cartasConceito,cartasProblema);
		this.baralhoCartas[BARALHO_PRINCIPAL] = new BaralhoCartas(facilidade, 
				//#ifdef ConceptCard
				cartasConceito, 
				//#endif
				cartasProblema);
		this.baralhoCartas[BARALHO_AUXILIAR] = new BaralhoCartas(baralhoCartas[BARALHO_PRINCIPAL]);

		// formarBaralhoArtefato();
		this.baralhoArtefatosBons[BARALHO_PRINCIPAL] = (BaralhoArtefatosBons) fabricaBaralhoArtefatos
				.getBaralho(ModeGameConstants.BARALHO_ARTEFATOS_BONS, null);
		this.baralhoArtefatosBons[BARALHO_AUXILIAR] = (BaralhoArtefatosBons) fabricaBaralhoArtefatos
				.getBaralho(ModeGameConstants.BARALHO_ARTEFATOS_BONS, 0);

		this.baralhoArtefatosRuins[BARALHO_PRINCIPAL] = (BaralhoArtefatosRuins) fabricaBaralhoArtefatos
				.getBaralho(ModeGameConstants.BARALHO_ARTEFATOS_RUINS, null);
		this.baralhoArtefatosRuins[BARALHO_AUXILIAR] = (BaralhoArtefatosRuins) fabricaBaralhoArtefatos
				.getBaralho(ModeGameConstants.BARALHO_ARTEFATOS_RUINS, 0);

		cadastrarJogadores(nomeJogadores);
		ordenarJogadores();
		embaralharCartaseArtefatos();
		setupController.exibirProjeto(projeto);
                
		// baralhoCartas[BARALHO_PRINCIPAL].mostrarBaralho(); //TODO so pra
		// teste -> mostra baralho ja na ordem de distribuicao -> ok

	}

	public void cadastrarJogadores(String[] nomeJogadores) {
		jogadores = new Jogador[nomeJogadores.length]; 

		int i = 0; 
		while (i < jogadores.length) {
			String nomeJogador;
			nomeJogador = nomeJogadores[i]; 
                        
			// construindo o jogador com par?metros inicias iguais ao projeto
			jogadores[i] = new Jogador(nomeJogador, projeto.getOrcamento());
			inserirEngenheiroInicial(jogadores[i]);
			i++;
		}
	}

	public void inserirEngenheiroInicial(Jogador jogador) {
		Random sorteioEngenheiro = new Random();
		Carta novato = null;
		while (novato == null) {
			int sorteado = sorteioEngenheiro.nextInt(baralhoCartas[BARALHO_PRINCIPAL].getNumeroTotalEngenheiro()); 
			novato = baralhoCartas[BARALHO_PRINCIPAL].darCartaInicial(sorteado); 
		}
		jogador.contratarEngenheiro(novato, 0); 

	}
        
        // ordena jogada dos jogadores
	public void ordenarJogadores() 
	{
		int[] pontuacaoJogador = new int[jogadores.length]; 
		for (int i = 0; i < pontuacaoJogador.length; i++) {
			pontuacaoJogador[i] = 0; 
		}
		for (int i = 0; i < jogadores.length; i++) {
                    
			// passando o nome do jogador i para que a GUI exiba o nome dele
			// pedindo rolagem de dados,serve para dar interatividade com
			// usuario
			setupController.pedirRolarDadosInicial(jogadores[i].getNome());

			pontuacaoJogador[i] = Dado.sortearValor(); 

			while (desempatarPontuacao(pontuacaoJogador[i], pontuacaoJogador) == true)
			{
				// passando pontuacao obtida por sorteio para que a GUI exiba
				// tal pontuacao
				setupController.mostrarPontosObtidosInicial(pontuacaoJogador[i]);

				// passando o nome do jogador i para que a GUI exiba o nome dele
				// pedindo nova rolagem de dados devido e empate.
				setupController.mostrarEmpatePontosObtidosInicial(jogadores[i].getNome());
				pontuacaoJogador[i] = Dado.sortearValor(); 
			}

			// passando pontuacao obtida por sorteio para que a GUI exiba tal
			// pontuacao
			setupController.mostrarPontosObtidosInicial(pontuacaoJogador[i]);
		}

		// metodo de ordencacao
		int max = 0;
		int posicao = 0;
		for (int j = 0; j < jogadores.length; j++) {
			for (int i = j; i < jogadores.length; i++) {
				if (pontuacaoJogador[i] > max) 
				{
					posicao = i;
					max = pontuacaoJogador[i];
				}
			}
			max = 0;
			pontuacaoJogador[posicao] = pontuacaoJogador[j]; 
			trocarOrdem(j, posicao);

		}
                
                // mostra ordem do jogo
		mostrarOrdemJogo(); 
	}

	public void trocarOrdem(int posicao1, int posicao2) 
	{
		Jogador temporaria = jogadores[posicao1];
		jogadores[posicao1] = jogadores[posicao2];
		jogadores[posicao2] = temporaria;

	}

	public boolean desempatarPontuacao(int valor, int[] pontuacao) 
	{
		int repeticao = 0;
		for (int i = 0; i < pontuacao.length; i++) {
			if (pontuacao[i] == valor)
				repeticao++;
		}
                
                // este retorno significa que nao ha empate na
							// pontuacao entre os jogadores
		if (repeticao < 2)
			return false; 
		
                // este retorno significa que ha empate na pontuacao entre
						// os jogadores
                return true; 
	}
        
        // mostra ordem do jogo
	public void mostrarOrdemJogo() 
	{
		String[] nomeJogadoresOrdenados = new String[jogadores.length];

		for (int i = 0; i < jogadores.length; i++)
			nomeJogadoresOrdenados[i] = jogadores[i].getNome();

		setupController.mostrarOrdemJogo(nomeJogadoresOrdenados);
	}

	public void embaralharCartaseArtefatos() {
		baralhoCartas[BARALHO_PRINCIPAL].embaralharInicial();
		baralhoArtefatosBons[BARALHO_PRINCIPAL].embaralhar();
		baralhoArtefatosRuins[BARALHO_PRINCIPAL].embaralhar();
	}

	public Jogador jogarDado(
			Jogador jogador) 
	{
		int numberCardsDelivered = jogador.analisarPontuacao();		
		
		// Feature de Questão de Resgate
		// Se o jogador tiver sem cartas na mão, e tirar 1 no dado, terá oportunidade
		// de responder uma questão, caso acerte, irá obter o número máximo de cartas (5)
		// #ifdef RescueQuestions
		if (numberCardsDelivered == 1 && jogador.getNumeroCartasMaoAtual() == 0) {
			int numQuestoes = this.questoes.size();
			if (numQuestoes > 0) {				
				boolean acertou =  this.setupController.exibirQuestao(this.questoes.get(new Random().nextInt(numQuestoes)));				
				setupController.exibirResultadoQuestao(acertou);
				if (acertou)
					numberCardsDelivered = 5;
			}			
		}
		// #endif
		
		/**
		 * recebe o numero de cartas a receber conforme pontuacao obtida nos
		 * dados e limite de carta em maos
		 */

		for (int i = 0; i < numberCardsDelivered; i++) {
			if (baralhoCartas[BARALHO_PRINCIPAL].getNumeroCartasBaralhoAtual() > 0)
                            
				/** verifica se ainda ha cartas no baralho principal */
				jogador.receberCarta(baralhoCartas[BARALHO_PRINCIPAL].darCarta());
                        
			else {
				trocarBaralhoCartas();
				jogador.receberCarta(baralhoCartas[BARALHO_PRINCIPAL].darCarta());
                                
				/** concede carta ao jogador */
			}
		}
		jogador.mostrarCartaMao();
                
		/*************/
		// TODO so pra teste -> mostra carta do jogador recebida

		return jogador;
	}

	public void trocarBaralhoCartas() 
	{
		BaralhoCartas temporario = baralhoCartas[BARALHO_PRINCIPAL];
		baralhoCartas[BARALHO_AUXILIAR].embaralhar();
                
		/** embaralhando as cartas que foram retiradas */
		baralhoCartas[BARALHO_PRINCIPAL] = baralhoCartas[BARALHO_AUXILIAR];
		baralhoCartas[BARALHO_AUXILIAR] = temporario;
		baralhoCartas[BARALHO_AUXILIAR].setCurrentCard(0);
                
		/** o novo baralho auxiliar tem o indice retornada para zero */
	}

	/**
	 * Retira cartas da mao do jogador e as coloca no baralho auxiliar. Retorna
	 * o mesmo jogador do par?metro
	 */
	public Jogador retirarCartas(Jogador jogador, Carta[] cartasRetiradas) {
		for (int i = 0; i < cartasRetiradas.length; i++) {
			jogador.retirarCarta(cartasRetiradas[i]);
			baralhoCartas[BARALHO_AUXILIAR].recolherCarta(cartasRetiradas[i]);
		}
		
		jogador.mostrarCartaMao();
		/*************/

		return jogador;
	}

	public Jogador admitirEngenheiro(Jogador jogador, CartaEngenheiro engenheiroContratado, int posicaoMesa) {
		jogador.contratarEngenheiro(engenheiroContratado, posicaoMesa);
                
		/** Tenta contratar o engenheiro */

		mostrarCartasDasMesasDoTabuleiro(jogador);

		return jogador;
	}

	public Jogador despedirEngenheiro(Jogador jogador, CartaEngenheiro engenheiroDemitido) {
		if (jogador.removerCarta(engenheiroDemitido) == true)
                    
			/** se pode demitir engenheiro */
			baralhoCartas[BARALHO_AUXILIAR].recolherCarta(engenheiroDemitido);
																							// teste
		mostrarCartasDasMesasDoTabuleiro(jogador);

		return jogador;
                
	}// TODO tem que conferir se o engenheiro a ser demitido ainda nao trabalhou
		// na rodade.

	/**
	 * @param jogador
	 */
        // TODO teste
	private void mostrarCartasDasMesasDoTabuleiro(Jogador jogador) {
		for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) 
																			// teste
		{ 
			if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
																			// teste
				continue; 
			else 
			{ 
				jogador.getTabuleiro().getMesas()[i].getCartaMesa().mostrarCarta();
			} 
		} 
	}

	public Jogador inserirArtefato(Jogador jogador, CartaEngenheiro engenheiroProduzindo, int mesaTrabalho) {
		this.habilidadeTemporaria = engenheiroProduzindo.getHabilidadeEngenheiroAtual();
                
		/**
		 * Se engenheiro for trabalhar em mesa distinta da sua, codigo das
		 * cartas das mesas comparadas sao disintas
		 */
		if (jogador.getTabuleiro().getMesas()[mesaTrabalho].getCartaMesa().getCodigoCarta()
				.compareTo(engenheiroProduzindo.getCodigoCarta()) != 0)
			habilidadeTemporaria--;
                
		/** Se engenheiro ajuda outro engenheiro, habilidade decresce 1 */



		Modulo[] pedido = setupController.exibirTabelaProducao(habilidadeTemporaria, projeto.getComplexidade());

		if (pedido == null) 
		{			
			return jogador;
		} else 
		{			
			int numeroArtefatoBons = pedido[Mesa.ARTEFATOS_BONS].somatorioModulo();
			int numeroArtefatosRuins = pedido[Mesa.ARTEFATOS_RUINS].somatorioModulo();
			int somatorioComplexidade = numeroArtefatoBons * projeto.getComplexidade()
					+ ((int) numeroArtefatosRuins * projeto.getComplexidade() / 2);

			/** atualizando habilidade atual do engenheiro */
			engenheiroProduzindo.setHabilidadeEngenheiroAtual(habilidadeTemporaria - somatorioComplexidade);

			/** engenheiro trabalhou na rodada, logo atualizando isso */
			engenheiroProduzindo.setEngenheiroTrabalhouNestaRodada(true);

			jogador.getTabuleiro().getMesas()[mesaTrabalho].receberArtefatos(pedido, baralhoArtefatosBons,
					baralhoArtefatosRuins);

			return jogador;
		}
	}

	public Jogador conferirArtefato(Jogador jogador, CartaEngenheiro engenheiroInspecionando, int mesaTrabalho) {
		this.habilidadeTemporaria = engenheiroInspecionando.getHabilidadeEngenheiroAtual();		

		/**
		 * Se engenheiro for trabalhar em mesa distinta da sua, codigo das
		 * cartas das mesas comparadas sao disintas
		 */
		if (jogador.getTabuleiro().getMesas()[mesaTrabalho].getCartaMesa().getCodigoCarta()
				.compareTo(engenheiroInspecionando.getCodigoCarta()) != 0)
			habilidadeTemporaria--;
                
		/** Se engenheiro ajuda outro engenheiro, habilidade decresce 1 */


		int[] artefatosAjudasNotInspecionados = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosNotInspecionadosSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getAjudas());
		int[] artefatosCodigosNotInspecionados = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosNotInspecionadosSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getCodigos());
		int[] artefatosDesenhosNotInspecionados = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosNotInspecionadosSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getDesenhos());
		int[] artefatosRastrosNotInspecionados = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosNotInspecionadosSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getRastros());
		int[] artefatosRequisitosNotInspecionados = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosNotInspecionadosSeparados(
						jogador.getTabuleiro().getMesas()[mesaTrabalho].getRequisitos());
		Modulo[] artefatosNotInspecionados = new Modulo[2];
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS] = new Modulo();
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS] = new Modulo();
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS].setAjudas(artefatosAjudasNotInspecionados[Mesa.ARTEFATOS_BONS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS]
				.setCodigos(artefatosCodigosNotInspecionados[Mesa.ARTEFATOS_BONS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS]
				.setDesenhos(artefatosDesenhosNotInspecionados[Mesa.ARTEFATOS_BONS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS]
				.setRastros(artefatosRastrosNotInspecionados[Mesa.ARTEFATOS_BONS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS]
				.setRequisitos(artefatosRequisitosNotInspecionados[Mesa.ARTEFATOS_BONS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS]
				.setAjudas(artefatosAjudasNotInspecionados[Mesa.ARTEFATOS_RUINS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS]
				.setCodigos(artefatosCodigosNotInspecionados[Mesa.ARTEFATOS_RUINS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS]
				.setDesenhos(artefatosDesenhosNotInspecionados[Mesa.ARTEFATOS_RUINS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS]
				.setRastros(artefatosRastrosNotInspecionados[Mesa.ARTEFATOS_RUINS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS]
				.setRequisitos(artefatosRequisitosNotInspecionados[Mesa.ARTEFATOS_RUINS]);

		Modulo[] pedido = setupController.exibirTabelaInspecao(habilidadeTemporaria, artefatosNotInspecionados);

		if (pedido == null) 
		{
			return jogador;
		} else 
		{
			int numeroArtefatoBons = pedido[Mesa.ARTEFATOS_BONS].somatorioModulo();
			int numeroArtefatosRuins = pedido[Mesa.ARTEFATOS_RUINS].somatorioModulo();
			int somatorioComplexidade = numeroArtefatoBons * projeto.getComplexidade()
					+ ((int) numeroArtefatosRuins * projeto.getComplexidade() / 2);

			/** atualizando habilidade atual do engenheiro */
			engenheiroInspecionando.setHabilidadeEngenheiroAtual(habilidadeTemporaria - somatorioComplexidade);

			/** engenheiro trabalhou na rodada, logo atualizando isso */
			engenheiroInspecionando.setEngenheiroTrabalhouNestaRodada(true);

			jogador.getTabuleiro().getMesas()[mesaTrabalho].virarArtefatos(pedido, baralhoArtefatosBons,
					baralhoArtefatosRuins);

			return jogador;
		}
	}

	public Jogador repararArtefato(Jogador jogador, CartaEngenheiro engenheiroCorrigindo, int mesaTrabalho) {
		this.habilidadeTemporaria = engenheiroCorrigindo.getHabilidadeEngenheiroAtual();		

		/**
		 * Se engenheiro for trabalhar em mesa distinta da sua, codigo das
		 * cartas das mesas comparadas sao disintas
		 */
		if (jogador.getTabuleiro().getMesas()[mesaTrabalho].getCartaMesa().getCodigoCarta()
				.compareTo(engenheiroCorrigindo.getCodigoCarta()) != 0)
			habilidadeTemporaria--;
                
		/** Se engenheiro ajuda outro engenheiro, habilidade decresce 1 */


		int[] artefatosAjudasInspecionadosBug = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosInspecionadosBugSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getAjudas());
		int[] artefatosCodigosInspecionadosBug = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosInspecionadosBugSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getCodigos());
		int[] artefatosDesenhosInspecionadosBug = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosInspecionadosBugSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getDesenhos());
		int[] artefatosRastrosInspecionadosBug = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosInspecionadosBugSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getRastros());
		int[] artefatosRequisitosInspecionadosBug = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosInspecionadosBugSeparados(
						jogador.getTabuleiro().getMesas()[mesaTrabalho].getRequisitos());
		Modulo[] artefatosInspecionadosBug = new Modulo[2];
		artefatosInspecionadosBug[Mesa.ARTEFATOS_BONS] = new Modulo();
		artefatosInspecionadosBug[Mesa.ARTEFATOS_RUINS] = new Modulo();
		artefatosInspecionadosBug[Mesa.ARTEFATOS_BONS].setAjudas(artefatosAjudasInspecionadosBug[Mesa.ARTEFATOS_BONS]);
		artefatosInspecionadosBug[Mesa.ARTEFATOS_BONS]
				.setCodigos(artefatosCodigosInspecionadosBug[Mesa.ARTEFATOS_BONS]);
		artefatosInspecionadosBug[Mesa.ARTEFATOS_BONS]
				.setDesenhos(artefatosDesenhosInspecionadosBug[Mesa.ARTEFATOS_BONS]);
		artefatosInspecionadosBug[Mesa.ARTEFATOS_BONS]
				.setRastros(artefatosRastrosInspecionadosBug[Mesa.ARTEFATOS_BONS]);
		artefatosInspecionadosBug[Mesa.ARTEFATOS_BONS]
				.setRequisitos(artefatosRequisitosInspecionadosBug[Mesa.ARTEFATOS_BONS]);
		artefatosInspecionadosBug[Mesa.ARTEFATOS_RUINS]
				.setAjudas(artefatosAjudasInspecionadosBug[Mesa.ARTEFATOS_RUINS]);
		artefatosInspecionadosBug[Mesa.ARTEFATOS_RUINS]
				.setCodigos(artefatosCodigosInspecionadosBug[Mesa.ARTEFATOS_RUINS]);
		artefatosInspecionadosBug[Mesa.ARTEFATOS_RUINS]
				.setDesenhos(artefatosDesenhosInspecionadosBug[Mesa.ARTEFATOS_RUINS]);
		artefatosInspecionadosBug[Mesa.ARTEFATOS_RUINS]
				.setRastros(artefatosRastrosInspecionadosBug[Mesa.ARTEFATOS_RUINS]);
		artefatosInspecionadosBug[Mesa.ARTEFATOS_RUINS]
				.setRequisitos(artefatosRequisitosInspecionadosBug[Mesa.ARTEFATOS_RUINS]);

		Modulo[] pedido = setupController.exibirTabelaCorrecao(habilidadeTemporaria, artefatosInspecionadosBug);

		if (pedido == null) 
		{
			return jogador;
		} else 
    {
			int numeroArtefatoBons = pedido[Mesa.ARTEFATOS_BONS].somatorioModulo();
			int numeroArtefatosRuins = pedido[Mesa.ARTEFATOS_RUINS].somatorioModulo();
			int somatorioComplexidade = numeroArtefatoBons * projeto.getComplexidade()
					+ ((int) numeroArtefatosRuins * projeto.getComplexidade() / 2);

			/** atualizando habilidade atual do engenheiro */
			engenheiroCorrigindo.setHabilidadeEngenheiroAtual(habilidadeTemporaria - somatorioComplexidade);

			/** engenheiro trabalhou na rodada, logo atualizando isso */
			engenheiroCorrigindo.setEngenheiroTrabalhouNestaRodada(true);

			jogador.getTabuleiro().getMesas()[mesaTrabalho].trocarArtefatos(pedido, baralhoArtefatosBons,
					baralhoArtefatosRuins);

			return jogador;
		}
	}

	public Jogador integrarModuloJ(Jogador jogador, CartaEngenheiro engenheiroCorrigindo, int mesaTrabalho,
			int moduloEscolhido, int[][] artefatosEscolhidos) {
		this.habilidadeTemporaria = engenheiroCorrigindo.getHabilidadeEngenheiroAtual();		

		/**
		 * Se engenheiro for trabalhar em mesa distinta da sua, codigo das
		 * cartas das mesas comparadas sao disintas
		 */
		if (jogador.getTabuleiro().getMesas()[mesaTrabalho].getCartaMesa().getCodigoCarta()
				.compareTo(engenheiroCorrigindo.getCodigoCarta()) != 0)
			habilidadeTemporaria--;
                
		/** Se engenheiro ajuda outro engenheiro, habilidade decresce 1 */

		for (int i = 0; i < jogador.getTabuleiro()
				.getMesas().length; i++) 
		{
			if (jogador.getTabuleiro().getMesas()[i].getEspecificacaoModuloIntegrado() == moduloEscolhido) {
				setupController.exibirModuloJaIntegrado(i + 1);
				return jogador;
			}
		}

		if (engenheiroCorrigindo.isEngenheiroTrabalhouNestaRodada() == true) {
			setupController.exibirEngenheiroNaoIntegra();
			return jogador;
		}

		if (conferirQuantidadeArtefatosSuficiente(jogador, projeto, mesaTrabalho, moduloEscolhido,
				artefatosEscolhidos) == false) {
			setupController.exibirQuantidadeArtefatosInsuficientes();
			return jogador;
		}
		if (habilidadeTemporaria <= 0) {
			setupController.exibirHabilidadeInsuficiente();
			return jogador;
		}

		ScreenInteraction.getScreenInteraction().exibirMensagem("Modulo com integracao valida", "Integracaod e modulo");

		/** atualizando habilidade atual do engenheiro */
		engenheiroCorrigindo.setHabilidadeEngenheiroAtual(0);
                
		/** Engenheiro nao faz mais nada na rodada depois de integrar modulo */

		/** engenheiro trabalhou na rodada, logo atualizando isso */
		engenheiroCorrigindo.setEngenheiroTrabalhouNestaRodada(true);

		ArrayList<Artefato>[] moduloIntegrado = construirModuloIntegrado(jogador, projeto, mesaTrabalho,
				moduloEscolhido, artefatosEscolhidos);

		jogador.getTabuleiro().getMesas()[mesaTrabalho].setModuloIntegrado(moduloIntegrado);
		jogador.getTabuleiro().getMesas()[mesaTrabalho].setEspecificacaoModuloIntegrado(moduloEscolhido);
		jogador.getTabuleiro().getMesas()[mesaTrabalho].setModuloJaIntegrado(true);
		return jogador;

	}

	public boolean conferirQuantidadeArtefatosSuficiente(Jogador jogador, CartaoProjeto projeto, int mesaTrabalho,
			int moduloEscolhido, int[][] artefatosEscolhidos) {
		contador = 0;
		for (int i = 0; i < artefatosEscolhidos[Mesa.ARTEFATOS_AJUDA].length; i++) {
			if (artefatosEscolhidos[Mesa.ARTEFATOS_AJUDA][i] == GameController.ARTEFATOS_SELECIONADO)
				contador++;
		}

		if (contador != projeto.getModulos()[moduloEscolhido].getAjudas())
			return false;

		contador = 0;
		for (int i = 0; i < artefatosEscolhidos[Mesa.ARTEFATOS_CODIGO].length; i++) {
			if (artefatosEscolhidos[Mesa.ARTEFATOS_CODIGO][i] == GameController.ARTEFATOS_SELECIONADO)
				contador++;
		}
		if (contador != projeto.getModulos()[moduloEscolhido].getCodigos())
			return false;

		contador = 0;
		for (int i = 0; i < artefatosEscolhidos[Mesa.ARTEFATOS_DESENHO].length; i++) {
			if (artefatosEscolhidos[Mesa.ARTEFATOS_DESENHO][i] == GameController.ARTEFATOS_SELECIONADO)
				contador++;
		}

		if (contador != projeto.getModulos()[moduloEscolhido].getDesenhos())
			return false;

		contador = 0;
		for (int i = 0; i < artefatosEscolhidos[Mesa.ARTEFATOS_RASTROS].length; i++) {
			if (artefatosEscolhidos[Mesa.ARTEFATOS_RASTROS][i] == GameController.ARTEFATOS_SELECIONADO)
				contador++;
		}

		if (contador != projeto.getModulos()[moduloEscolhido].getRastros())
			return false;

		contador = 0;
		for (int i = 0; i < artefatosEscolhidos[Mesa.ARTEFATOS_REQUISITOS].length; i++) {
			if (artefatosEscolhidos[Mesa.ARTEFATOS_REQUISITOS][i] == GameController.ARTEFATOS_SELECIONADO)
				contador++;
		}

		if (contador != projeto.getModulos()[moduloEscolhido].getRequisitos())
			return false;

		return true;
	}

	public ArrayList<Artefato>[] construirModuloIntegrado(Jogador jogador, CartaoProjeto projeto, int mesaTrabalho,
			int moduloEscolhido, int[][] artefatosEscolhidos) {
		@SuppressWarnings("unchecked")
		ArrayList<Artefato>[] moduloIntegrado = new ArrayList[5];
		for (int i = 0; i < moduloIntegrado.length; i++)
			moduloIntegrado[i] = new ArrayList<Artefato>();

		for (int i = 0; i < artefatosEscolhidos[Mesa.ARTEFATOS_AJUDA].length; i++) {
			if (artefatosEscolhidos[Mesa.ARTEFATOS_AJUDA][i] == GameController.ARTEFATOS_SELECIONADO) {
				Artefato temporario = jogador.getTabuleiro().getMesas()[mesaTrabalho].getAjudas()
						.get(artefatosEscolhidos[Mesa.ARTEFATOS_AJUDA][i]);
                                
				/** copiando um artefato escolhido numa variavel temporaria */
				jogador.getTabuleiro().getMesas()[mesaTrabalho].getAjudas()
						.remove(artefatosEscolhidos[Mesa.ARTEFATOS_AJUDA][i]);
                                
				/** retira artefato da mesa para o modulo a ser integrado */
				moduloIntegrado[Mesa.ARTEFATOS_AJUDA].add(temporario);
			}

		}
		for (int i = 0; i < artefatosEscolhidos[Mesa.ARTEFATOS_CODIGO].length; i++) {
			if (artefatosEscolhidos[Mesa.ARTEFATOS_CODIGO][i] == GameController.ARTEFATOS_SELECIONADO) {
				Artefato temporario = jogador.getTabuleiro().getMesas()[mesaTrabalho].getCodigos()
						.get(artefatosEscolhidos[Mesa.ARTEFATOS_CODIGO][i]);
				jogador.getTabuleiro().getMesas()[mesaTrabalho].getCodigos()
						.remove(artefatosEscolhidos[Mesa.ARTEFATOS_CODIGO][i]);
				moduloIntegrado[Mesa.ARTEFATOS_CODIGO].add(temporario);
			}

		}

		for (int i = 0; i < artefatosEscolhidos[Mesa.ARTEFATOS_DESENHO].length; i++) {
			if (artefatosEscolhidos[Mesa.ARTEFATOS_DESENHO][i] == GameController.ARTEFATOS_SELECIONADO) {
				Artefato temporario = jogador.getTabuleiro().getMesas()[mesaTrabalho].getDesenhos()
						.get(artefatosEscolhidos[Mesa.ARTEFATOS_DESENHO][i]);
				jogador.getTabuleiro().getMesas()[mesaTrabalho].getDesenhos()
						.remove(artefatosEscolhidos[Mesa.ARTEFATOS_DESENHO][i]);
				moduloIntegrado[Mesa.ARTEFATOS_DESENHO].add(temporario);
			}

		}

		for (int i = 0; i < artefatosEscolhidos[Mesa.ARTEFATOS_RASTROS].length; i++) {
			if (artefatosEscolhidos[Mesa.ARTEFATOS_RASTROS][i] == GameController.ARTEFATOS_SELECIONADO) {
				Artefato temporario = jogador.getTabuleiro().getMesas()[mesaTrabalho].getRastros()
						.get(artefatosEscolhidos[Mesa.ARTEFATOS_RASTROS][i]);
				jogador.getTabuleiro().getMesas()[mesaTrabalho].getRastros()
						.remove(artefatosEscolhidos[Mesa.ARTEFATOS_RASTROS][i]);
				moduloIntegrado[Mesa.ARTEFATOS_RASTROS].add(temporario);
			}

		}

		for (int i = 0; i < artefatosEscolhidos[Mesa.ARTEFATOS_REQUISITOS].length; i++) {
			if (artefatosEscolhidos[Mesa.ARTEFATOS_REQUISITOS][i] == GameController.ARTEFATOS_SELECIONADO) {
				Artefato temporario = jogador.getTabuleiro().getMesas()[mesaTrabalho].getRequisitos()
						.get(artefatosEscolhidos[Mesa.ARTEFATOS_REQUISITOS][i]);
				jogador.getTabuleiro().getMesas()[mesaTrabalho].getRequisitos()
						.remove(artefatosEscolhidos[Mesa.ARTEFATOS_REQUISITOS][i]);
				moduloIntegrado[Mesa.ARTEFATOS_REQUISITOS].add(temporario);
			}

		}

		return moduloIntegrado;
	}

	public Jogador trocarModuloMesa(Jogador jogadorAtual, CartaEngenheiro engenheiroTransferindo, int mesaEscolhida) {
		for (int i = 0; i < jogadorAtual.getTabuleiro().getMesas().length; i++) {
			if (jogadorAtual.getTabuleiro().getMesas()[i].getCartaMesa() == null)
				continue;
			if (!(jogadorAtual.getTabuleiro().getMesas()[i].getCartaMesa().getCodigoCarta()
					.equals(engenheiroTransferindo.getCodigoCarta())))
				continue;
                        
			/** encontrando a mesa do engenheiro que transfere o modulo */
			if (jogadorAtual.getTabuleiro().getMesas()[i].getModuloJaIntegrado() == false)
                            
				/** so por seguranca */
				return jogadorAtual;

			ArrayList<Artefato>[] temporario = jogadorAtual.getTabuleiro().getMesas()[mesaEscolhida]
					.getModuloIntegrado();
                        
			/** trocando os modulos das mesas */
			jogadorAtual.getTabuleiro().getMesas()[mesaEscolhida]
					.setModuloIntegrado(jogadorAtual.getTabuleiro().getMesas()[i].getModuloIntegrado());
			jogadorAtual.getTabuleiro().getMesas()[i].setModuloIntegrado(temporario);

			jogadorAtual.getTabuleiro().getMesas()[mesaEscolhida].setModuloJaIntegrado(true);
			jogadorAtual.getTabuleiro().getMesas()[i].setModuloJaIntegrado(false);
			return jogadorAtual;
		}
		return jogadorAtual;
	}

	//#ifdef ConceptCard
	public Jogador usarConceito(Jogador jogador, CartaBonificacao cartaUtilizada) {
		// insere PRIMEIRO efeito no tabuleiro do jogador
		
		this.aplicarPrimeiroEfeito(jogador, cartaUtilizada);
		
        // insere SEGUNDO efeito no tabuleiro do jogador
		this.aplicarSegundoEfeito(jogador, cartaUtilizada);


		Carta[] carta = new Carta[1];
		carta[0] = cartaUtilizada;
		retirarCartas(jogador, carta);
		
		// removendo carta utilizada
		setupController.exibirEfeitoinserido(jogador, cartaUtilizada);
		return jogador;
	}
	//#endif


	private void aplicarPrimeiroEfeito(Jogador jogador, CartaBonificacao cartaUtilizada) {
		switch (cartaUtilizada.getTipoPrimeiroEfeito())
		{
			case (CardsConstants.NO_BENEFITS):
				break;
			case (CardsConstants.BUDGET_INCREASE):
				jogador.getTabuleiro().setEfeitoPositivoOrcamento(cartaUtilizada.getQuantidadePrimeiroEfeito());
				break;			
			case (CardsConstants.CHANGE_GRAY_ARTIFACTS_BY_WHITE_ARTIFACTS):
				changeGrayArtifactsByWhiteArtifacts(jogador);
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_HELP_ARTIFACTS): 				
				insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadePrimeiroEfeito(), Mesa.ARTEFATOS_AJUDA, this.sorteio.nextInt(2));
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_MATURITY_POINTS_NOW): 		
				this.conceptsInteraction.choseReceiveMaturityPointsNow(jogador, cartaUtilizada, this.setupController);
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_REQUIREMENTS_ARTIFACTS):				
				this.insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadePrimeiroEfeito(), Mesa.ARTEFATOS_REQUISITOS, this.sorteio.nextInt(2));
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_SKILL_POINTS_LATER):	
				this.conceptsInteraction.choseReceiveSkillPointsLater(jogador, cartaUtilizada, this.setupController);
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_SKILL_POINTS_NOW): 			
				this.conceptsInteraction.choseReceiveSkillPointsNow(jogador, cartaUtilizada, this.setupController);
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_TRAIL_ARTIFACTS):			
				insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadePrimeiroEfeito(), Mesa.ARTEFATOS_RASTROS,this.sorteio.nextInt(2));
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_WHITE_ARTIFACT): 			
				insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadePrimeiroEfeito(), this.sorteio.nextInt(5),Mesa.ARTEFATOS_BONS);
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_WHITE_CODE_ARTIFACT): 
				insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadePrimeiroEfeito(), Mesa.ARTEFATOS_CODIGO, Mesa.ARTEFATOS_BONS);
				break;			
			case (CardsConstants.ENGINEER_CHOSEN_INSPECT_FREE_ARTIFACTS): {
				String[] engenheiro = setupController.escolherEngenheiro(jogador, 1);
				if (engenheiro[0] == null)
					break;
				
				// percorrendo mesas do tabuleiro
				for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++)
				{
					CartaEngenheiro cartaMesa = jogador.getTabuleiro().getMesas()[i].getCartaMesa(); 
					if (cartaMesa != null && cartaMesa.getEngenheiro().getNomeEngenheiro().equals(engenheiro[0])) 
						inspecionarArtefatoByEfeito(jogador, cartaUtilizada.getQuantidadePrimeiroEfeito());
				}
				break;
			}
			case (CardsConstants.TWO_ENGINNERS_CHOSEN_RECEIVE_DESIGN_ARTIFACTS): {
				String[] engenheiros = setupController.escolherEngenheiro(jogador, 2);
				if (engenheiros[0] != null)
					insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadePrimeiroEfeito(), Mesa.ARTEFATOS_DESENHO,
							this.sorteio.nextInt(2), engenheiros[0]);				
				
				if (engenheiros[1] != null)
					insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadePrimeiroEfeito(), Mesa.ARTEFATOS_DESENHO,
							this.sorteio.nextInt(2), engenheiros[1]);
				
				break;
			}
			case (CardsConstants.TWO_ENGINNERS_CHOSEN_RECEIVE_TRAIL_ARTIFACTS): {
				String[] engenheiros = setupController.escolherEngenheiro(jogador, 2);			
				if (engenheiros[0] != null)
					insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadePrimeiroEfeito(), Mesa.ARTEFATOS_RASTROS,
							this.sorteio.nextInt(2), engenheiros[0]);				
				if (engenheiros[1] != null)
					insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadePrimeiroEfeito(), Mesa.ARTEFATOS_RASTROS,
							this.sorteio.nextInt(2), engenheiros[1]);						
				break;
			}
			case (CardsConstants.UNIZING_COMPONENT_VALIDATION_PHASE): {
				jogador.getTabuleiro().setEfeitoModuloIntegradoNeutralizadoValidacao(true);
				break;
			}
			case (CardsConstants.UNIZING_HELP_ARTIFACTS_VALIDATION_PHASE): {
				jogador.getTabuleiro().setEfeitoHelpArtifactsNeutralizadoValidacao(true);
				break;
			}
			case (CardsConstants.UNIZING_PROBLEM_CARD_RELATED_CODE): {
				jogador.getTabuleiro().setEfeitoProblemaArtefatoCodigoNeutralizado(true);
				break;
			}
			case (CardsConstants.UNIZING_PROBLEM_CARD_RELATED_REQUIREMENTS): {
				jogador.getTabuleiro().setEfeitoProblemaArtefatoRequisitosNeutralizado(true);
				break;
			}
			case (CardsConstants.UNIZING_PROBLEM_CARD_RELATED_TRAILSS): {
				jogador.getTabuleiro().setEfeitoProblemaArtefatoRequisitosNeutralizado(true);
				break;
			}
			default:
				break;
		}

	}
	
	private void aplicarSegundoEfeito(Jogador jogador, CartaBonificacao cartaUtilizada) {
		switch (cartaUtilizada.getTipoSegundoEfeito())
		{
			case (CardsConstants.NO_BENEFITS):
				break;
			case (CardsConstants.BUDGET_INCREASE): {
				jogador.getTabuleiro().setEfeitoPositivoOrcamento(cartaUtilizada.getQuantidadeSegundoEfeito());
				break;
			}
			case (CardsConstants.CHANGE_GRAY_ARTIFACTS_BY_WHITE_ARTIFACTS): {
				changeGrayArtifactsByWhiteArtifacts(jogador);
				break;
			}
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_HELP_ARTIFACTS): {
				Random sorteio = new Random();
				int sorteado = sorteio.nextInt(2);
				insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadeSegundoEfeito(), Mesa.ARTEFATOS_AJUDA,
						sorteado);
				break;
			}
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_MATURITY_POINTS_NOW): {
				String[] engenheiro = setupController.escolherEngenheiro(jogador, 1);
				if (engenheiro[0] == null)
					break;
				
				// percorrendo mesas do tabuleiro
				for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++)
				{
					CartaEngenheiro cartaMesa = jogador.getTabuleiro().getMesas()[i].getCartaMesa();
					
					// encontra engenheiro que recebera efeito
					if (cartaMesa != null && cartaMesa.getEngenheiro().getNomeEngenheiro().equals(engenheiro[0])) {
						jogador.getTabuleiro().getMesas()[i].setEfeitoAumentarMaturidadeEngenheiro(cartaUtilizada.getQuantidadeSegundoEfeito());
					}					
				}
				break;
			}

			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_REQUIREMENTS_ARTIFACTS):				
				insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadeSegundoEfeito(), Mesa.ARTEFATOS_REQUISITOS,
						this.sorteio.nextInt(2));
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_SKILL_POINTS_LATER): {
				String[] engenheiro = setupController.escolherEngenheiro(jogador, 1);
				
				// percorrendo mesas do tabuleiro
				for (int j = 0; j < engenheiro.length; j++) {
					if (engenheiro[j] == null)
						continue;					
					
					for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) 
					{
						CartaEngenheiro cartaMesa = jogador.getTabuleiro().getMesas()[i].getCartaMesa();
						
						// encontra engenheiro que recebera efeito
						if (cartaMesa != null && cartaMesa.getEngenheiro().getNomeEngenheiro().equals(engenheiro[j])){
							String[] auxiliar = new String[2];
							auxiliar[0] = engenheiro[j];
							auxiliar[1] = Integer.toString(cartaUtilizada.getQuantidadeSegundoEfeito());
							jogador.getTabuleiro().getEfeitoAumentarHabilidadeEngenheiroLater().add(auxiliar);							
						}
					}
				}
				break;

			}
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_SKILL_POINTS_NOW): {
				String[] engenheiro = setupController.escolherEngenheiro(jogador, 1);
				if (engenheiro[0] == null)
					break;
				
				// percorrendo mesas do tabuleiro
				for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
					CartaEngenheiro cartaMesa = jogador.getTabuleiro().getMesas()[i].getCartaMesa();
					
					// encontra engenheiro que recebera efeito
					if (cartaMesa != null && cartaMesa.getEngenheiro().getNomeEngenheiro().equals(engenheiro[0])) {
						jogador.getTabuleiro().getMesas()[i]
								.setEfeitoAumentarMaturidadeEngenheiro(cartaUtilizada.getQuantidadeSegundoEfeito());
					}
				}
				break;
			}
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_TRAIL_ARTIFACTS):			
				insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadeSegundoEfeito(), Mesa.ARTEFATOS_RASTROS, this.sorteio.nextInt(2));
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_WHITE_ARTIFACT):				
				insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadeSegundoEfeito(), this.sorteio.nextInt(5),
						Mesa.ARTEFATOS_BONS);
				break;			
			case (CardsConstants.ENGINNER_CHOSEN_RECEIVE_WHITE_CODE_ARTIFACT): {
				insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadeSegundoEfeito(), Mesa.ARTEFATOS_CODIGO,
						Mesa.ARTEFATOS_BONS);
				break;

			}
			case (CardsConstants.ENGINEER_CHOSEN_INSPECT_FREE_ARTIFACTS): {
				String[] engenheiro = setupController.escolherEngenheiro(jogador, 1);
				if (engenheiro[0] == null)
					break;
				
				// percorrendo mesas do tabuleiro
				for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) 
				{
					CartaEngenheiro cartaMesa = jogador.getTabuleiro().getMesas()[i].getCartaMesa();
					
					// encontra engenheiro que recebera efeito
					if (cartaMesa != null && cartaMesa.getEngenheiro().getNomeEngenheiro().equals(engenheiro[0]))
						inspecionarArtefatoByEfeito(jogador, cartaUtilizada.getQuantidadeSegundoEfeito());					
				}
				break;
			}
			case (CardsConstants.TWO_ENGINNERS_CHOSEN_RECEIVE_DESIGN_ARTIFACTS): {
				String[] engenheiros = setupController.escolherEngenheiro(jogador, 2);				
				if (engenheiros[0] != null)
					insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadeSegundoEfeito(), Mesa.ARTEFATOS_DESENHO,
							this.sorteio.nextInt(2), engenheiros[0]);					
				if (engenheiros[1] != null)
					insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadeSegundoEfeito(), Mesa.ARTEFATOS_DESENHO,
							this.sorteio.nextInt(2), engenheiros[1]);
			
				break;
			}
			case (CardsConstants.TWO_ENGINNERS_CHOSEN_RECEIVE_TRAIL_ARTIFACTS): {
				String[] engenheiros = setupController.escolherEngenheiro(jogador, 2);				
				if (engenheiros[0] != null)
					insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadeSegundoEfeito(), Mesa.ARTEFATOS_RASTROS,
							this.sorteio.nextInt(2), engenheiros[0]);
				
				if (engenheiros[1] != null)									
					insertArtifactByEffect(jogador, cartaUtilizada.getQuantidadeSegundoEfeito(), Mesa.ARTEFATOS_RASTROS,
							this.sorteio.nextInt(2), engenheiros[1]);			
				break;
			}
			case (CardsConstants.UNIZING_COMPONENT_NOW): {
				int mesa = setupController.escolherMesaNeutralizaComponente();
				jogador.getTabuleiro().getMesas()[mesa].setEfeitoModuloIntegradoNeutralizado(true);
			}
			case (CardsConstants.UNIZING_COMPONENT_VALIDATION_PHASE): {
				jogador.getTabuleiro().setEfeitoModuloIntegradoNeutralizadoValidacao(true);
				break;
			}
			case (CardsConstants.UNIZING_HELP_ARTIFACTS_VALIDATION_PHASE): {
				jogador.getTabuleiro().setEfeitoHelpArtifactsNeutralizadoValidacao(true);
				break;
			}
			case (CardsConstants.UNIZING_PROBLEM_CARD_RELATED_CODE): {
				jogador.getTabuleiro().setEfeitoProblemaArtefatoCodigoNeutralizado(true);
				break;
			}
			case (CardsConstants.UNIZING_PROBLEM_CARD_RELATED_REQUIREMENTS): {
				jogador.getTabuleiro().setEfeitoProblemaArtefatoRequisitosNeutralizado(true);
				break;
			}
			case (CardsConstants.UNIZING_PROBLEM_CARD_RELATED_TRAILSS): {
				jogador.getTabuleiro().setEfeitoProblemaArtefatoRequisitosNeutralizado(true);
				break;
			}
			default:
				break;
		}
	}

	

	public void insertArtifactByEffect(Jogador jogador, int quantidade, int tipoArtefato, int sorteado) {
		String[] engenheiro = setupController.escolherEngenheiro(jogador, 1);
		if (engenheiro[0] == null)
                    
			return;
		/** encerra metodo */
		for (int i = 0; i < jogador.getTabuleiro()
				.getMesas().length; i++) 
		{
			if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
				continue;
      
      //encontra engenheiro que recebera efeito
			if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getNomeEngenheiro().equals(
					engenheiro[0])) 
			{
				for (int j = 0; j < quantidade; j++) 
				{
					if (tipoArtefato == Mesa.ARTEFATOS_AJUDA) {
						if (sorteado == Mesa.ARTEFATOS_BONS)
                                                    
							/** inserindo efeito */
							jogador.getTabuleiro().getMesas()[i].getAjudas()
									.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						else
							jogador.getTabuleiro().getMesas()[i].getAjudas()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
					}
					if (tipoArtefato == Mesa.ARTEFATOS_CODIGO) {
						if (sorteado == Mesa.ARTEFATOS_BONS)
                                                    
							/** inserindo efeito */
							jogador.getTabuleiro().getMesas()[i].getCodigos()
									.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						else
							jogador.getTabuleiro().getMesas()[i].getCodigos()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
					}
					if (tipoArtefato == Mesa.ARTEFATOS_DESENHO) {
						if (sorteado == Mesa.ARTEFATOS_BONS)
                                                    
							/** inserindo efeito */
							jogador.getTabuleiro().getMesas()[i].getDesenhos()
									.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						else
							jogador.getTabuleiro().getMesas()[i].getDesenhos()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
					}
					if (tipoArtefato == Mesa.ARTEFATOS_RASTROS) {
						if (sorteado == Mesa.ARTEFATOS_BONS)
                                                    
							/** inserindo efeito */
							jogador.getTabuleiro().getMesas()[i].getRastros()
									.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						else
							jogador.getTabuleiro().getMesas()[i].getRastros()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
					}
					if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS) {
						if (sorteado == Mesa.ARTEFATOS_BONS)
                                                    
							/** inserindo efeito */
							jogador.getTabuleiro().getMesas()[i].getRequisitos()
									.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						else
							jogador.getTabuleiro().getMesas()[i].getRequisitos()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
					}
				}
			}
		}
	}


	public void insertArtifactByEffect(Jogador jogador, int quantidade, int tipoArtefato, int sorteado,
			String engenheiro) {
		for (int i = 0; i < jogador.getTabuleiro()
				.getMesas().length; i++) 
		{
			if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
				continue;
      
      
									 // encontra engenheiro que recebera efeito
			if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getNomeEngenheiro().equals(
					engenheiro)) 
			{
				for (int j = 0; j < quantidade; j++) 
				{
					if (tipoArtefato == Mesa.ARTEFATOS_AJUDA) {
						if (sorteado == Mesa.ARTEFATOS_BONS)
                                                    
							/** inserindo efeito */
							jogador.getTabuleiro().getMesas()[i].getAjudas()
									.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						else
							jogador.getTabuleiro().getMesas()[i].getAjudas()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
					}
					if (tipoArtefato == Mesa.ARTEFATOS_CODIGO) {
						if (sorteado == Mesa.ARTEFATOS_BONS)
                                                    
							/** inserindo efeito */
							jogador.getTabuleiro().getMesas()[i].getCodigos()
									.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						else
							jogador.getTabuleiro().getMesas()[i].getCodigos()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
					}
					if (tipoArtefato == Mesa.ARTEFATOS_DESENHO) {
						if (sorteado == Mesa.ARTEFATOS_BONS)
                                                    
							/** inserindo efeito */
							jogador.getTabuleiro().getMesas()[i].getDesenhos()
									.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						else
							jogador.getTabuleiro().getMesas()[i].getDesenhos()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
					}
					if (tipoArtefato == Mesa.ARTEFATOS_RASTROS) {
						if (sorteado == Mesa.ARTEFATOS_BONS)
                                                    
							/** inserindo efeito */
							jogador.getTabuleiro().getMesas()[i].getRastros()
									.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						else
							jogador.getTabuleiro().getMesas()[i].getRastros()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
					}
					if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS) {
						if (sorteado == Mesa.ARTEFATOS_BONS)
                                                    
							/** inserindo efeito */
							jogador.getTabuleiro().getMesas()[i].getRequisitos()
									.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						else
							jogador.getTabuleiro().getMesas()[i].getRequisitos()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
					}
				}
			}
		}
	}

	public void changeGrayArtifactsByWhiteArtifacts(Jogador jogador) {
		for (int i = 0; i < jogador.getTabuleiro()
				.getMesas().length; i++) 
		{
			boolean percorreuTudo = false;
			while (percorreuTudo == false) {
				for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getAjudas()
						.size(); j++) 
				{
					if (jogador.getTabuleiro().getMesas()[i].getAjudas().get(j).isPoorQuality() == true) {
						baralhoArtefatosRuins[BARALHO_AUXILIAR]
								.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getAjudas().remove(j));
						jogador.getTabuleiro().getMesas()[i].getAjudas()
								.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						break;
                                                
						/**
						 * nao pode-se percorrer um arrayList por for ja que ha
						 * reorganizacao do array,logo deve-se retirar somente
						 * um elemento por vez
						 */
					}
				}
				percorreuTudo = true;
                                
				/** se percorreu todo o array, nao ha artefatos cinzas nele */
			}

			percorreuTudo = false;
			while (percorreuTudo == false) {
				for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getCodigos()
						.size(); j++) 
				{
					if (jogador.getTabuleiro().getMesas()[i].getCodigos().get(j).isPoorQuality() == true) {
						baralhoArtefatosRuins[BARALHO_AUXILIAR]
								.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getCodigos().remove(j));
						jogador.getTabuleiro().getMesas()[i].getCodigos()
								.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						break;
                                                
						/**
						 * nao pode-se percorrer um arrayList por for ja que ha
						 * reorganizacao do array,logo deve-se retirar somente
						 * um elemento por vez
						 */
					}
				}
				percorreuTudo = true;
                                
				/** se percorreu todo o array, nao ha artefatos cinzas nele */
			}

			percorreuTudo = false;
			while (percorreuTudo == false) {
				for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getDesenhos()
						.size(); j++)
				{
					if (jogador.getTabuleiro().getMesas()[i].getDesenhos().get(j).isPoorQuality() == true) {
						baralhoArtefatosRuins[BARALHO_AUXILIAR]
								.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getDesenhos().remove(j));
						jogador.getTabuleiro().getMesas()[i].getDesenhos()
								.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						break;
                                                
						/**
						 * nao pode-se percorrer um arrayList por for ja que ha
						 * reorganizacao do array,logo deve-se retirar somente
						 * um elemento por vez
						 */
					}
				}
				percorreuTudo = true;
                                
				/** se percorreu todo o array, nao ha artefatos cinzas nele */
			}

			percorreuTudo = false;
			while (percorreuTudo == false) {
				for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getRastros()
						.size(); j++) 
				{
					if (jogador.getTabuleiro().getMesas()[i].getRastros().get(j).isPoorQuality() == true) {
						baralhoArtefatosRuins[BARALHO_AUXILIAR]
								.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getRastros().remove(j));
						jogador.getTabuleiro().getMesas()[i].getRastros()
								.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						break;
                                                
						/**
						 * nao pode-se percorrer um arrayList por for ja que ha
						 * reorganizacao do array,logo deve-se retirar somente
						 * um elemento por vez
						 */
					}
				}
				percorreuTudo = true;
                                
				/** se percorreu todo o array, nao ha artefatos cinzas nele */
			}

			percorreuTudo = false;
			while (percorreuTudo == false) {
				for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getRequisitos()
						.size(); j++) 
				{
					if (jogador.getTabuleiro().getMesas()[i].getRequisitos().get(j).isPoorQuality() == true) {
						baralhoArtefatosRuins[BARALHO_AUXILIAR]
								.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getRequisitos().remove(j));
						jogador.getTabuleiro().getMesas()[i].getRequisitos()
								.add(baralhoArtefatosBons[BARALHO_PRINCIPAL].darArtefato());
						break;
                                                
						/**
						 * nao pode-se percorrer um arrayList por for ja que ha
						 * reorganizacao do array,logo deve-se retirar somente
						 * um elemento por vez
						 */
					}
				}
				percorreuTudo = true;
                                
				/** se percorreu todo o array, nao ha artefatos cinzas nele */
			}
		}
	}

	public void inspecionarArtefatoByEfeito(Jogador jogador, int quantidadeArtefato) {
		int mesaTrabalho = setupController.escolherMesadeTrabalho();
		while ((mesaTrabalho == -1) || (jogador.getTabuleiro().getMesas()[mesaTrabalho].getCartaMesa() == null)) {
			mesaTrabalho = setupController.escolherMesadeTrabalho();
		}
		int[] artefatosAjudasNotInspecionados = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosNotInspecionadosSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getAjudas());
		int[] artefatosCodigosNotInspecionados = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosNotInspecionadosSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getCodigos());
		int[] artefatosDesenhosNotInspecionados = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosNotInspecionadosSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getDesenhos());
		int[] artefatosRastrosNotInspecionados = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosNotInspecionadosSeparados(jogador.getTabuleiro().getMesas()[mesaTrabalho].getRastros());
		int[] artefatosRequisitosNotInspecionados = jogador.getTabuleiro().getMesas()[mesaTrabalho]
				.somarArtefatosNotInspecionadosSeparados(
						jogador.getTabuleiro().getMesas()[mesaTrabalho].getRequisitos());
		Modulo[] artefatosNotInspecionados = new Modulo[2];
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS] = new Modulo();
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS] = new Modulo();
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS].setAjudas(artefatosAjudasNotInspecionados[Mesa.ARTEFATOS_BONS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS]
				.setCodigos(artefatosCodigosNotInspecionados[Mesa.ARTEFATOS_BONS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS]
				.setDesenhos(artefatosDesenhosNotInspecionados[Mesa.ARTEFATOS_BONS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS]
				.setRastros(artefatosRastrosNotInspecionados[Mesa.ARTEFATOS_BONS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_BONS]
				.setRequisitos(artefatosRequisitosNotInspecionados[Mesa.ARTEFATOS_BONS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS]
				.setAjudas(artefatosAjudasNotInspecionados[Mesa.ARTEFATOS_RUINS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS]
				.setCodigos(artefatosCodigosNotInspecionados[Mesa.ARTEFATOS_RUINS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS]
				.setDesenhos(artefatosDesenhosNotInspecionados[Mesa.ARTEFATOS_RUINS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS]
				.setRastros(artefatosRastrosNotInspecionados[Mesa.ARTEFATOS_RUINS]);
		artefatosNotInspecionados[Mesa.ARTEFATOS_RUINS]
				.setRequisitos(artefatosRequisitosNotInspecionados[Mesa.ARTEFATOS_RUINS]);

		Modulo[] pedido = null;
		while (pedido == null) {
			pedido = setupController.exibirTabelaInspecao(quantidadeArtefato, artefatosNotInspecionados);
			//#ifdef ConceptCard
			if (pedido == null)
				setupController.exibirQuantidadeBeneficio(quantidadeArtefato);
			//#endif
		}

		jogador.getTabuleiro().getMesas()[mesaTrabalho].virarArtefatos(pedido, baralhoArtefatosBons,
				baralhoArtefatosRuins);

	}

	public Jogador usarProblema(Jogador jogadorAtual, Jogador jogadorAlvo, CartaPenalizacao cartaUtilizada) {
		boolean condicaoSatisfeita = verificarCondicao(jogadorAlvo, cartaUtilizada);
		setupController.exibirEfeitoinserido(jogadorAtual, jogadorAlvo, cartaUtilizada, condicaoSatisfeita);
		if (condicaoSatisfeita == false) {
			Carta[] carta = new Carta[1];
			carta[0] = cartaUtilizada;
			retirarCartas(jogadorAtual, carta);
                        
			/** removendo carta utilizada */
			return jogadorAtual;
		}

		switch (cartaUtilizada
				.getTipoPrimeiroEfeito())
		{
		case (CardsConstants.NO_PROBLEM):
			break;
		case (CardsConstants.ADD_DIFFICULTY_INSPECT_ARTIFACTS): {
			jogadorAlvo.getTabuleiro()
					.setEfeitoAdicionaDificuldadeInspecionarArtefatos(cartaUtilizada.getQuantidadePrimeiroEfeito());
			break;
		}
		case (CardsConstants.ADD_DIFFICULTY_REPAIR_ARTIFACTS): {
			jogadorAlvo.getTabuleiro()
					.setEfeitoAdicionaDificuldadeCorrigirArtefatos(cartaUtilizada.getQuantidadePrimeiroEfeito());
			break;
		}
		case (CardsConstants.ADD_POINTS_IN_COMPLEXITY): {
			jogadorAlvo.getTabuleiro()
					.setEfeitoAdicionaComplexidadeProjeto(cartaUtilizada.getQuantidadePrimeiroEfeito());
			break;
		}
		case (CardsConstants.ADD_PROJECT_QUALITY): {
			jogadorAlvo.getTabuleiro().setEfeitoAdicionaQualidadeProjeto(cartaUtilizada.getQuantidadePrimeiroEfeito());
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_LOSE_ARTIFACT): {
			allEngineerLoseArtifacts(jogadorAlvo, cartaUtilizada.getQuantidadePrimeiroEfeito(), ANY_ARTIFACTS);
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_LOSE_DESIGN_ARTIFACT): {
			allEngineerLoseArtifacts(jogadorAlvo, cartaUtilizada.getQuantidadePrimeiroEfeito(), Mesa.ARTEFATOS_DESENHO);
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_LOSE_TRAIL_ARTIFACT): {
			allEngineerLoseArtifacts(jogadorAlvo, cartaUtilizada.getQuantidadePrimeiroEfeito(), Mesa.ARTEFATOS_RASTROS);
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_NO_WORK): {
			for (int i = 0; i < jogadorAlvo.getTabuleiro().getMesas().length; i++) {
                            
				/**
				 * colocando como se engenheiro ja estivesse trabalhado na
				 * rodada do jogadorAlvo
				 */
				jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().setEngenheiroTrabalhouNestaRodada(true);
				jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().setHabilidadeEngenheiroAtual(0);
			}
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_NOT_PRODUCE_ARTIFACTS): {
			for (int i = 0; i < jogadorAlvo.getTabuleiro().getMesas().length; i++)
				jogadorAlvo.getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_EnginnersNotProduceArtifacts(cartaUtilizada.getDuracaoEfeito());
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_WITH_SKILL_LESS_THAN_2_NOT_INSPECT_ARTIFACTS): {
			for (int i = 0; i < jogadorAlvo.getTabuleiro().getMesas().length; i++) {
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() < 2)
					jogadorAlvo.getTabuleiro().getMesas()[i].setDuracaoEfeito_TEMPORARIO_EnginnersNotProduceArtifacts(
							cartaUtilizada.getDuracaoEfeito());
			}
			break;
		}
		case (CardsConstants.ALL_LOSES_EFFECTS_CONCEPT_CARDS_ON_BOARD): {
			for (int i = 0; i < jogadores.length; i++) {
				jogadores[i].getTabuleiro().setEfeitoPositivoOrcamento(0);
                                
				/** retira efeito positivo sobre orcamento */

				String[] vazia = new String[2];
				vazia[0] = null;
				vazia[1] = null;
				jogadores[i].getTabuleiro().getEfeitoAumentarHabilidadeEngenheiroLater().clear();
                                
				/**
				 * retira efeito de ter habilidade de engenheiro aumentada
				 * depois
				 */

				jogadores[i].getTabuleiro().setEfeitoHelpArtifactsNeutralizadoValidacao(false);
				jogadores[i].getTabuleiro().setEfeitoModuloIntegradoNeutralizadoValidacao(false);
				jogadores[i].getTabuleiro().setEfeitoProblemaArtefatoCodigoNeutralizado(false);
				jogadores[i].getTabuleiro().setEfeitoProblemaArtefatoRastroNeutralizado(false);
				jogadores[i].getTabuleiro().setEfeitoProblemaArtefatoRequisitosNeutralizado(false);

				for (int j = 0; j < jogadores[i].getTabuleiro().getMesas().length; j++) {
					jogadores[i].getTabuleiro().getMesas()[j].setEfeitoAumentarHabilidadeEngenheiro(0);
					jogadores[i].getTabuleiro().getMesas()[j].setEfeitoAumentarMaturidadeEngenheiro(0);
					jogadores[i].getTabuleiro().getMesas()[j].setEfeitoModuloIntegradoNeutralizado(false);
				}
			}
			break;
		}
		case (CardsConstants.ALL_PLAYERS_WITH_MORE_2_ENGINEERS_DISMISS_ENGINEER): {
			contador = 0;
                        
                        
			for (int i = 0; i < jogadores.length; i++) 
			{
				for (int j = 0; j < jogadores[i].getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadores[i].getTabuleiro().getMesas()[j].getCartaMesa() != null)
						contador++;
				}
                                
                                /** se numero de engenheiros > 2 */
				if (contador > 2) 
				{
					for (int k = 0; k < cartaUtilizada
							.getQuantidadePrimeiroEfeito(); k++) 
					{
						Random sorteio = new Random();
						int engenheiroDemitido = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
                                                
						/** sorteia qual engenheiro sera demitido */
						boolean demitiu = false;
						while (demitiu == false) {
							if (jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa() != null) {
                                                            
								/**
								 * se o engenheiro trabalhou nesta rodada,
								 * significa que estamos no jogadorAtual,
								 * demitimos assim mesmo
								 */
								if (jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa()
										.isEngenheiroTrabalhouNestaRodada() == true)
									jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa()
											.setEngenheiroTrabalhouNestaRodada(false);
                                                                
								/**
								 * atualizacao da variavel para reusar a funcao
								 * abaixo
								 */

								
                                                                        despedirEngenheiro(jogadores[i],
										jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa());
								demitiu = true;
							} else
								engenheiroDemitido = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
						}
					}
				}
				contador = 0;
                                
				/**
				 * atualiza contador para conferir se proximo jogador deve
				 * demitir engenheiro
				 */
			}
			break;
		}
		case (CardsConstants.ALL_PLAYERS_WITH_MORE_3_ENGINEERS_DISMISS_ENGINEER): {
			contador = 0;
                        
                        /** para cada jogador */
			for (int i = 0; i < jogadores.length; i++) 
			{
				for (int j = 0; j < jogadores[i].getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadores[i].getTabuleiro().getMesas()[j].getCartaMesa() != null)
						contador++;
				}
                                
                                /** se numero de engenheiros > 3 */
				if (contador > 3) 
				{
					for (int k = 0; k < cartaUtilizada
							.getQuantidadePrimeiroEfeito(); k++)
					{
						Random sorteio = new Random();
						int engenheiroDemitido = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
                                                
						/** sorteia qual engenheiro sera demitido */
						boolean demitiu = false;
						while (demitiu == false) {
							if (jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa() != null) {
                                                            
								/**
								 * se o engenheiro trabalhou nesta rodada,
								 * significa que estamos no jogadorAtual,
								 * demitimos assim mesmo
								 */
								if (jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa()
										.isEngenheiroTrabalhouNestaRodada() == true)
									jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa()
											.setEngenheiroTrabalhouNestaRodada(false);
                                                                
								/**
								 * atualizacao da variavel para reusar a funcao
								 * abaixo
								 */

								/* TODO ver */despedirEngenheiro(jogadores[i],
										jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa());
								demitiu = true;
							} else
								engenheiroDemitido = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
						}
					}
				}
				contador = 0;
                                
				/**
				 * atualiza contador para conferir se proximo jogador deve
				 * demitir engenheiro
				 */
			}
			break;
		}
		case (CardsConstants.CHANGE_WHITE_DESIGN_ARTIFACTS_BY_GRAY_DESIGN_ARTIFACTS): {
			changeWhiteArtifactsByGrayArtifacts(jogadorAlvo, Mesa.ARTEFATOS_DESENHO);
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_IS_ONLY_QUANTITY_CODE_ARTIFACT): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;

					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 

					{
						if (jogadorAlvo.getTabuleiro().getMesas()[j].getCodigos().size() > cartaUtilizada
								.getQuantidadePrimeiroEfeito())
						{
							for (int k = 0; k < (jogadorAlvo.getTabuleiro().getMesas()[j].getCodigos().size()
									- cartaUtilizada.getQuantidadePrimeiroEfeito()); k++)
							{
								retirarArtefato(jogadorAlvo, j, Mesa.ARTEFATOS_CODIGO);
							}
						}
					}

				}

			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_LOSE_ALL_ARTIFACTS): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;

					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 

					{
						retirarTodosArtefatos(jogadorAlvo, j, Mesa.ARTEFATOS_AJUDA);
						retirarTodosArtefatos(jogadorAlvo, j, Mesa.ARTEFATOS_CODIGO);
						retirarTodosArtefatos(jogadorAlvo, j, Mesa.ARTEFATOS_DESENHO);
						retirarTodosArtefatos(jogadorAlvo, j, Mesa.ARTEFATOS_RASTROS);
						retirarTodosArtefatos(jogadorAlvo, j, Mesa.ARTEFATOS_REQUISITOS);

					}

				}

			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_LOSE_ARTIFACT): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;
          
          //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i]))

					{
						for (int k = 0; k < cartaUtilizada.getQuantidadePrimeiroEfeito(); k++) {
							Random sorteio = new Random();
							retirarArtefato(jogadorAlvo, j, sorteio.nextInt(Mesa.QUANTIDADE_TIPOS_DE_ARTEFATOS));
						}
					}

				}

			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_LOSE_CODE_ARTIFACT): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++)
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;
          
          //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 

					{
						for (int k = 0; k < cartaUtilizada.getQuantidadePrimeiroEfeito(); k++) {
							retirarArtefato(jogadorAlvo, j, Mesa.ARTEFATOS_CODIGO);
						}
					}

				}

			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_NO_WORK): {
                    
                        //para cada engenheiro escolhido
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;
          
          //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i]))
					{
                                            
						/**
						 * colocando como se engenheiro ja estivesse trabalhado
						 * na rodada do jogadorAlvo
						 */
						jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().setEngenheiroTrabalhouNestaRodada(true);
						jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().setHabilidadeEngenheiroAtual(0);
					}

				}

			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_PENALTY_GIVING_OR_RECEIVING_HELP): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;
          
            //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 
					{
						jogadorAlvo.getTabuleiro().getMesas()[j]
								.setEfeitoPenalizarAjuda(cartaUtilizada.getQuantidadePrimeiroEfeito());
					}
				}
			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_PRODUCE_ONLY_GRAY_ARTIFACTS): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++)
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
                                {
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;

            //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i]))

					{
						jogadorAlvo.getTabuleiro().getMesas()[j].setDuracaoEfeito_TEMPORARIO_ProduceOnlyGrayArtifacts(
								cartaUtilizada.getDuracaoEfeito());
					}
				}
			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_PRODUCE_ONLY_WHITE_ARTIFACTS): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;

            //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 
					{
						jogadorAlvo.getTabuleiro().getMesas()[j].setDuracaoEfeito_TEMPORARIO_ProduceOnlyWhiteArtifacts(
								cartaUtilizada.getDuracaoEfeito());
					}
				}
			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_UNEMPLOYMENT_LATER): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorrendo mesas do tabuleiro
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa() == null)
						continue;

            //encontra engenheiro que sera demitido
					if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[j]))
					{
						jogadorAlvo.getTabuleiro().getEfeitoDemitirEngenheiroLater().add(engenheiro[j]);
					}
				}
			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_UNEMPLOYMENT_NOW): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++)
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;

            //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 

					{
                                                        despedirEngenheiro(jogadorAlvo,
								jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa());
                                                
						/** demite engenheiro */
					}
				}
			}
			break;
		}
                
                //todos os engenheiros perdem todos os artefatos
		case (CardsConstants.LOSE_ALL_ARTIFACTS):
		{
			allEngineerLoseArtifacts(jogadorAlvo, ALL_ARTIFACTS, ANY_ARTIFACTS);
			break;
		}
		case (CardsConstants.LOSE_ALL_CODE_ARTIFACTS): {
			allEngineerLoseArtifacts(jogadorAlvo, ALL_ARTIFACTS, Mesa.ARTEFATOS_CODIGO);
			break;
		}
		case (CardsConstants.LOSE_ALL_GRAY_DESIGN_ARTIFACTS): {
			retirarTodosArtefatosCinzas(jogadorAlvo, Mesa.ARTEFATOS_DESENHO);
			break;
		}
		case (CardsConstants.LOSE_ALL_GRAY_REQUIREMENTS_ARTIFACTS): {
			retirarTodosArtefatosCinzas(jogadorAlvo, Mesa.ARTEFATOS_REQUISITOS);
			break;
		}
		case (CardsConstants.LOSE_ALL_DESIGN_ARTIFACTS): {
			allEngineerLoseArtifacts(jogadorAlvo, ALL_ARTIFACTS, Mesa.ARTEFATOS_DESENHO);
			break;
		}
		case (CardsConstants.LOSE_ALL_REQUIREMENTS_ARTIFACTS): {
			allEngineerLoseArtifacts(jogadorAlvo, ALL_ARTIFACTS, Mesa.ARTEFATOS_REQUISITOS);
			break;
		}
		case (CardsConstants.LOSE_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadePrimeiroEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				int tipoArtefatoSorteado = sorteio.nextInt(Mesa.QUANTIDADE_TIPOS_DE_ARTEFATOS);
				retirarArtefato(jogadorAlvo, mesaSorteada, tipoArtefatoSorteado);
			}
			break;
		}
		case (CardsConstants.LOSE_CODE_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadePrimeiroEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_CODIGO);
			}
			break;
		}
		case (CardsConstants.LOSE_DESIGN_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadePrimeiroEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_DESENHO);
			}
			break;
		}
		case (CardsConstants.LOSE_HELP_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadePrimeiroEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_AJUDA);
			}
			break;
		}
		case (CardsConstants.LOSE_REQUIREMENTS_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadePrimeiroEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_REQUISITOS);
			}
			break;
		}
		case (CardsConstants.LOSE_HALF_OF_ARTIFACTS): {
			Random sorteio = new Random();
			int totalArtefatos = 0;
			totalArtefatos += contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_AJUDA);
			totalArtefatos += contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_CODIGO);
			totalArtefatos += contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_DESENHO);
			totalArtefatos += contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_RASTROS);
			totalArtefatos += contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_REQUISITOS);

			int metadeArtefatos = (int) totalArtefatos / 2;

			for (int i = 0; i < metadeArtefatos; i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				int tipoArtefatoSorteado = sorteio.nextInt(Mesa.QUANTIDADE_TIPOS_DE_ARTEFATOS);
				retirarArtefato(jogadorAlvo, mesaSorteada, tipoArtefatoSorteado);
			}
			break;
		}
		case (CardsConstants.LOSE_HALF_OF_CODE_ARTIFACTS): {
			Random sorteio = new Random();
			int totalArtefatosCodigo = contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_CODIGO);

			int metadeArtefatos = (int) totalArtefatosCodigo / 2;

			for (int i = 0; i < metadeArtefatos; i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_CODIGO);
			}
			break;
		}
		case (CardsConstants.LOSE_HALF_OF_DESIGN_ARTIFACTS): {
			Random sorteio = new Random();
			int totalArtefatosDesenho = contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_DESENHO);

			int metadeArtefatos = (int) totalArtefatosDesenho / 2;

			for (int i = 0; i < metadeArtefatos; i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_DESENHO);
			}
			break;
		}
		case (CardsConstants.LOSE_REQUIREMENTS_ARTIFACTS_FOR_EACH_BUG): {
			Random sorteio = new Random();
			int totalArtefatosBug = contarBugs(jogadorAlvo);

			int quantidadeArtefatosPerdidos = totalArtefatosBug * cartaUtilizada.getQuantidadePrimeiroEfeito();

			for (int i = 0; i < quantidadeArtefatosPerdidos; i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_REQUISITOS);
			}
			break;
		}
		case (CardsConstants.LOSE_TRAIL_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadePrimeiroEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_RASTROS);
			}
			break;
		}
		case (CardsConstants.LOWER_MATURITY_ENGINEER_UNEMPLOYMENT): {
			int maturidadeMinima = 100;
			ArrayList<String> engenheiros = new ArrayList<String>();
                        
                        //percorre mesa
			for (int i = 0; i < jogadorAlvo.getTabuleiro()
					.getMesas().length; i++) 
			{
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa()
						.getEngenheiro().getMaturidadeEngenheiro() <= maturidadeMinima) {

					/** encontrando qual a maturidade minima */
					maturidadeMinima = jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa()
							.getEngenheiro().getMaturidadeEngenheiro();
				}
			}
                        
                        //percorre mesa
			for (int i = 0; i < jogadorAlvo.getTabuleiro()
					.getMesas().length; i++) 
			{
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro()
						.getMaturidadeEngenheiro() == maturidadeMinima) {
                                    
					/** encontrando engenheiros com maturidade minima */
					engenheiros.add(jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getNomeEngenheiro());
				}
			}
			Random sorteio = new Random();
			int engenheiroSorteado = sorteio.nextInt(engenheiros.size());
                        
			/** sorteando uma posicao do array cujo engenheiro sera demitido */
			String engenheiroDemitido = engenheiros.get(engenheiroSorteado - 1);
                        
			/** variavel contera nome do engenheiro sorteado a ser demitido */

                        //percorre mesa
			for (int i = 0; i < jogadorAlvo.getTabuleiro()
					.getMesas().length; i++) 
			{
        
        //acha engenheiro
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getNomeEngenheiro()
						.equals(engenheiroDemitido))
				{
                                                despedirEngenheiro(jogadorAlvo,
							jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa());
				}
			}
			break;
		}
		case (CardsConstants.ONLY_INSPECT_ARTIFACTS): {
			for (int i = 0; i < jogadorAlvo.getTabuleiro().getMesas().length; i++) {
				jogadorAlvo.getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_EnginnersNotProduceArtifacts(cartaUtilizada.getDuracaoEfeito());
				jogadorAlvo.getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_EnginnersNotCorrectArtifacts(cartaUtilizada.getDuracaoEfeito());
			}
			break;
		}
		case (CardsConstants.REDUCTION_IN_BUDGET): {
			jogadorAlvo.getTabuleiro().setEfeitoNegativoOrcamento(cartaUtilizada.getQuantidadePrimeiroEfeito());
			break;
		}
		case (CardsConstants.TABLE_CHOSEN_LOSE_ALL_CODE_ARTIFACT): {
			int mesa = setupController.escolherMesaSofrerProblema();
			retirarTodosArtefatos(jogadorAlvo, mesa, Mesa.ARTEFATOS_CODIGO);
			break;
		}
                
		// default: /**nao havera essa opcao, mas a colocamos por seguranca*/
		// break;
		}
                
                //insere SEGUNDO efeito no tabuleiro do jogadorAlvo
		switch (cartaUtilizada
				.getTipoSegundoEfeito()) 
		{
		case (CardsConstants.NO_PROBLEM):
			break;
		case (CardsConstants.ADD_DIFFICULTY_INSPECT_ARTIFACTS): {
			jogadorAlvo.getTabuleiro()
					.setEfeitoAdicionaDificuldadeInspecionarArtefatos(cartaUtilizada.getQuantidadeSegundoEfeito());
			break;
		}
		case (CardsConstants.ADD_DIFFICULTY_REPAIR_ARTIFACTS): {
			jogadorAlvo.getTabuleiro()
					.setEfeitoAdicionaDificuldadeCorrigirArtefatos(cartaUtilizada.getQuantidadeSegundoEfeito());
			break;
		}
		case (CardsConstants.ADD_POINTS_IN_COMPLEXITY): {
			jogadorAlvo.getTabuleiro()
					.setEfeitoAdicionaComplexidadeProjeto(cartaUtilizada.getQuantidadeSegundoEfeito());
			break;
		}
		case (CardsConstants.ADD_PROJECT_QUALITY): {
			jogadorAlvo.getTabuleiro().setEfeitoAdicionaQualidadeProjeto(cartaUtilizada.getQuantidadeSegundoEfeito());
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_LOSE_ARTIFACT): {
			allEngineerLoseArtifacts(jogadorAlvo, cartaUtilizada.getQuantidadeSegundoEfeito(), ANY_ARTIFACTS);
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_LOSE_DESIGN_ARTIFACT): {
			allEngineerLoseArtifacts(jogadorAlvo, cartaUtilizada.getQuantidadeSegundoEfeito(), Mesa.ARTEFATOS_DESENHO);
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_LOSE_TRAIL_ARTIFACT): {
			allEngineerLoseArtifacts(jogadorAlvo, cartaUtilizada.getQuantidadeSegundoEfeito(), Mesa.ARTEFATOS_RASTROS);
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_NO_WORK): {
			for (int i = 0; i < jogadorAlvo.getTabuleiro().getMesas().length; i++) {
                            
				/**
				 * colocando como se engenheiro ja estivesse trabalhado na
				 * rodada do jogadorAlvo
				 */
				jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().setEngenheiroTrabalhouNestaRodada(true);
				jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().setHabilidadeEngenheiroAtual(0);
			}
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_NOT_PRODUCE_ARTIFACTS): {
			for (int i = 0; i < jogadorAlvo.getTabuleiro().getMesas().length; i++)
				jogadorAlvo.getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_EnginnersNotProduceArtifacts(cartaUtilizada.getDuracaoEfeito());
			break;
		}
		case (CardsConstants.ALL_ENGINEERS_WITH_SKILL_LESS_THAN_2_NOT_INSPECT_ARTIFACTS): {
			for (int i = 0; i < jogadorAlvo.getTabuleiro().getMesas().length; i++) {
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() < 2)
					jogadorAlvo.getTabuleiro().getMesas()[i].setDuracaoEfeito_TEMPORARIO_EnginnersNotProduceArtifacts(
							cartaUtilizada.getDuracaoEfeito());
			}
			break;
		}
		case (CardsConstants.ALL_LOSES_EFFECTS_CONCEPT_CARDS_ON_BOARD): {
			for (int i = 0; i < jogadores.length; i++) {
				jogadores[i].getTabuleiro().setEfeitoPositivoOrcamento(0);
                                
				/** retira efeito positivo sobre orcamento */

				String[] vazia = new String[2];
				vazia[0] = null;
				vazia[1] = null;
				jogadores[i].getTabuleiro().getEfeitoAumentarHabilidadeEngenheiroLater().clear();
                                
				/**
				 * retira efeito de ter habilidade de engenheiro aumentada
				 * depois
				 */

				jogadores[i].getTabuleiro().setEfeitoHelpArtifactsNeutralizadoValidacao(false);
				jogadores[i].getTabuleiro().setEfeitoModuloIntegradoNeutralizadoValidacao(false);
				jogadores[i].getTabuleiro().setEfeitoProblemaArtefatoCodigoNeutralizado(false);
				jogadores[i].getTabuleiro().setEfeitoProblemaArtefatoRastroNeutralizado(false);
				jogadores[i].getTabuleiro().setEfeitoProblemaArtefatoRequisitosNeutralizado(false);

				for (int j = 0; j < jogadores[i].getTabuleiro().getMesas().length; j++) {
					jogadores[i].getTabuleiro().getMesas()[j].setEfeitoAumentarHabilidadeEngenheiro(0);
					jogadores[i].getTabuleiro().getMesas()[j].setEfeitoAumentarMaturidadeEngenheiro(0);
					jogadores[i].getTabuleiro().getMesas()[j].setEfeitoModuloIntegradoNeutralizado(false);
				}
			}
			break;
		}
		case (CardsConstants.ALL_PLAYERS_WITH_MORE_2_ENGINEERS_DISMISS_ENGINEER): {
			contador = 0;
                        
                         /** para cada jogador */
			for (int i = 0; i < jogadores.length; i++)
			{
                                //conto quantos engenheiros ele tem
				for (int j = 0; j < jogadores[i].getTabuleiro()
						.getMesas().length; j++)
				{
					if (jogadores[i].getTabuleiro().getMesas()[j].getCartaMesa() != null)
						contador++;
				}
                                
                                /** se numero de engenheiros > 2 */
				if (contador > 2) 
				{
                                    
                                        //demiti-se uma quantidade de engenheiros  do jogador
					for (int k = 0; k < cartaUtilizada
							.getQuantidadeSegundoEfeito(); k++) 
					{
						Random sorteio = new Random();
						int engenheiroDemitido = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
                                                
						/** sorteia qual engenheiro sera demitido */
						boolean demitiu = false;
						while (demitiu == false) {
							if (jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa() != null) {
                                                            
								/**
								 * se o engenheiro trabalhou nesta rodada,
								 * significa que estamos no jogadorAtual,
								 * demitimos assim mesmo
								 */
								if (jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa()
										.isEngenheiroTrabalhouNestaRodada() == true)
									jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa()
											.setEngenheiroTrabalhouNestaRodada(false);
                                                                
								/**
								 * atualizacao da variavel para reusar a funcao
								 * abaixo
								 */

                                                                        despedirEngenheiro(jogadores[i],
										jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa());
								demitiu = true;
							} else
								engenheiroDemitido = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
						}
					}
				}
				contador = 0;
                                
				/**
				 * atualiza contador para conferir se proximo jogador deve
				 * demitir engenheiro
				 */
			}
			break;
		}
		case (CardsConstants.ALL_PLAYERS_WITH_MORE_3_ENGINEERS_DISMISS_ENGINEER): {
			contador = 0;
                        
                        /** para cada jogador */
			for (int i = 0; i < jogadores.length; i++) 
			{
                            
                                //conto quantos engenheiros ele tem
				for (int j = 0; j < jogadores[i].getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadores[i].getTabuleiro().getMesas()[j].getCartaMesa() != null)
						contador++;
				}
                                
                                 /** se numero de engenheiros > 3 */
				if (contador > 3)
				{
                                    
                                        //demiti-se uma quantidade de engenheiros  do jogador
					for (int k = 0; k < cartaUtilizada
							.getQuantidadeSegundoEfeito(); k++) 
					{
						Random sorteio = new Random();
						int engenheiroDemitido = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
                                                
						/** sorteia qual engenheiro sera demitido */
						boolean demitiu = false;
						while (demitiu == false) {
							if (jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa() != null) {
                                                            
								/**
								 * se o engenheiro trabalhou nesta rodada,
								 * significa que estamos no jogadorAtual,
								 * demitimos assim mesmo
								 */
								if (jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa()
										.isEngenheiroTrabalhouNestaRodada() == true)
									jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa()
											.setEngenheiroTrabalhouNestaRodada(false);
                                                                
								/**
								 * atualizacao da variavel para reusar a funcao
								 * abaixo
								 */

                                                                        despedirEngenheiro(jogadores[i],
										jogadores[i].getTabuleiro().getMesas()[engenheiroDemitido].getCartaMesa());
								demitiu = true;
							} else
								engenheiroDemitido = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
						}
					}
				}
				contador = 0;
                                
				/**
				 * atualiza contador para conferir se proximo jogador deve
				 * demitir engenheiro
				 */
			}
			break;
		}
		case (CardsConstants.CHANGE_WHITE_DESIGN_ARTIFACTS_BY_GRAY_DESIGN_ARTIFACTS): {
			changeWhiteArtifactsByGrayArtifacts(jogadorAlvo, Mesa.ARTEFATOS_DESENHO);
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_IS_ONLY_QUANTITY_CODE_ARTIFACT): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;
           
          //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 
					{
                                            
                                                //se ele ter mais que a quatidade do limite inferior
						if (jogadorAlvo.getTabuleiro().getMesas()[j].getCodigos().size() > cartaUtilizada
								.getQuantidadeSegundoEfeito()) 
						{
                                                        //retira o restante dos artefatos do engenheiro 
							for (int k = 0; k < (jogadorAlvo.getTabuleiro().getMesas()[j].getCodigos().size()
									- cartaUtilizada.getQuantidadeSegundoEfeito()); k++)
							{
								retirarArtefato(jogadorAlvo, j, Mesa.ARTEFATOS_CODIGO);
							}
						}
					}

				}

			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_LOSE_ALL_ARTIFACTS): {
                        
                         //para cada engenheiro escolhido
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;
          
          //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 
					{
						retirarTodosArtefatos(jogadorAlvo, j, Mesa.ARTEFATOS_AJUDA);
						retirarTodosArtefatos(jogadorAlvo, j, Mesa.ARTEFATOS_CODIGO);
						retirarTodosArtefatos(jogadorAlvo, j, Mesa.ARTEFATOS_DESENHO);
						retirarTodosArtefatos(jogadorAlvo, j, Mesa.ARTEFATOS_RASTROS);
						retirarTodosArtefatos(jogadorAlvo, j, Mesa.ARTEFATOS_REQUISITOS);

					}

				}

			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_LOSE_ARTIFACT): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++)
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++)
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;
          
          //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i]))
					{
						for (int k = 0; k < cartaUtilizada.getQuantidadeSegundoEfeito(); k++) {
							Random sorteio = new Random();
							retirarArtefato(jogadorAlvo, j, sorteio.nextInt(Mesa.QUANTIDADE_TIPOS_DE_ARTEFATOS));
						}
					}

				}

			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_LOSE_CODE_ARTIFACT): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro  escolhido
			for (int i = 0; i < engenheiro.length; i++)
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++)
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;
          
          //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 
					{
						for (int k = 0; k < cartaUtilizada.getQuantidadeSegundoEfeito(); k++) {
							retirarArtefato(jogadorAlvo, j, Mesa.ARTEFATOS_CODIGO);
						}
					}

				}

			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_NO_WORK): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++)
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;

          //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i]))
					{
            
						/**
						 * colocando como se engenheiro ja estivesse trabalhado
						 * na rodada do jogadorAlvo
						 */
						jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().setEngenheiroTrabalhouNestaRodada(true);
						jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().setHabilidadeEngenheiroAtual(0);
					}

				}

			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_PENALTY_GIVING_OR_RECEIVING_HELP): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;

					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 
					{
						jogadorAlvo.getTabuleiro().getMesas()[j]
								.setEfeitoPenalizarAjuda(cartaUtilizada.getQuantidadeSegundoEfeito());
					}
				}
			}
			break;
		}
                
                //para cada engenheiro escolhido
		case (CardsConstants.ENGINEER_CHOSEN_PRODUCE_ONLY_GRAY_ARTIFACTS): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
			for (int i = 0; i < engenheiro.length; i++)
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++) 
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;
          
          //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 
					{
						jogadorAlvo.getTabuleiro().getMesas()[j].setDuracaoEfeito_TEMPORARIO_ProduceOnlyGrayArtifacts(
								cartaUtilizada.getDuracaoEfeito());
					}
				}
			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_PRODUCE_ONLY_WHITE_ARTIFACTS): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro  escolhido
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++)
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;
 
          //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i]))
					{
						jogadorAlvo.getTabuleiro().getMesas()[j].setDuracaoEfeito_TEMPORARIO_ProduceOnlyWhiteArtifacts(
								cartaUtilizada.getDuracaoEfeito());
					}
				}
			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_UNEMPLOYMENT_LATER): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        // para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++)
			{
				if (engenheiro[i] == null)
					continue;
                                // percorrendo mesas do tabuleiro
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++)
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa() == null)
						continue;
            
            //encontra engenheiro que sera demitido
					if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[j])) 
					{
						jogadorAlvo.getTabuleiro().getEfeitoDemitirEngenheiroLater().add(engenheiro[j]);
					}
				}
			}
			break;
		}
		case (CardsConstants.ENGINEER_CHOSEN_UNEMPLOYMENT_NOW): {
			String[] engenheiro = setupController.escolherEngenheiro(jogadorAlvo, 1);
                        
                        //para cada engenheiro escolhido
			for (int i = 0; i < engenheiro.length; i++) 
			{
				if (engenheiro[i] == null)
					continue;
                                
                                //percorre mesa
				for (int j = 0; j < jogadorAlvo.getTabuleiro()
						.getMesas().length; j++)
				{
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa() == null)
						continue;

            //acha engenheiro
					if (jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(engenheiro[i])) 
					{
                                                            despedirEngenheiro(jogadorAlvo,
								jogadorAlvo.getTabuleiro().getMesas()[j].getCartaMesa());
                                                
						/** demite engenheiro */
					}
				}
			}
			break;
		}
                
                //todos os engenheiros perdem todos os artefatos
		case (CardsConstants.LOSE_ALL_ARTIFACTS):
		{
			allEngineerLoseArtifacts(jogadorAlvo, ALL_ARTIFACTS, ANY_ARTIFACTS);
			break;
		}
		case (CardsConstants.LOSE_ALL_CODE_ARTIFACTS): {
			allEngineerLoseArtifacts(jogadorAlvo, ALL_ARTIFACTS, Mesa.ARTEFATOS_CODIGO);
			break;
		}
		case (CardsConstants.LOSE_ALL_GRAY_DESIGN_ARTIFACTS): {
			retirarTodosArtefatosCinzas(jogadorAlvo, Mesa.ARTEFATOS_DESENHO);
			break;
		}
		case (CardsConstants.LOSE_ALL_GRAY_REQUIREMENTS_ARTIFACTS): {
			retirarTodosArtefatosCinzas(jogadorAlvo, Mesa.ARTEFATOS_REQUISITOS);
			break;
		}
		case (CardsConstants.LOSE_ALL_DESIGN_ARTIFACTS): {
			allEngineerLoseArtifacts(jogadorAlvo, ALL_ARTIFACTS, Mesa.ARTEFATOS_DESENHO);
			break;
		}
		case (CardsConstants.LOSE_ALL_REQUIREMENTS_ARTIFACTS): {
			allEngineerLoseArtifacts(jogadorAlvo, ALL_ARTIFACTS, Mesa.ARTEFATOS_REQUISITOS);
			break;
		}
		case (CardsConstants.LOSE_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadeSegundoEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				int tipoArtefatoSorteado = sorteio.nextInt(Mesa.QUANTIDADE_TIPOS_DE_ARTEFATOS);
				retirarArtefato(jogadorAlvo, mesaSorteada, tipoArtefatoSorteado);
			}
			break;
		}
		case (CardsConstants.LOSE_CODE_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadeSegundoEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_CODIGO);
			}
			break;
		}
		case (CardsConstants.LOSE_DESIGN_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadeSegundoEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_DESENHO);
			}
			break;
		}
		case (CardsConstants.LOSE_HELP_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadeSegundoEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_AJUDA);
			}
			break;
		}
		case (CardsConstants.LOSE_REQUIREMENTS_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadeSegundoEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_REQUISITOS);
			}
			break;
		}
		case (CardsConstants.LOSE_HALF_OF_ARTIFACTS): {
			Random sorteio = new Random();
			int totalArtefatos = 0;
			totalArtefatos += contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_AJUDA);
			totalArtefatos += contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_CODIGO);
			totalArtefatos += contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_DESENHO);
			totalArtefatos += contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_RASTROS);
			totalArtefatos += contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_REQUISITOS);

			int metadeArtefatos = (int) totalArtefatos / 2;

			for (int i = 0; i < metadeArtefatos; i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				int tipoArtefatoSorteado = sorteio.nextInt(Mesa.QUANTIDADE_TIPOS_DE_ARTEFATOS);
				retirarArtefato(jogadorAlvo, mesaSorteada, tipoArtefatoSorteado);
			}
			break;
		}
		case (CardsConstants.LOSE_HALF_OF_CODE_ARTIFACTS): {
			Random sorteio = new Random();
			int totalArtefatosCodigo = contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_CODIGO);

			int metadeArtefatos = (int) totalArtefatosCodigo / 2;

			for (int i = 0; i < metadeArtefatos; i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_CODIGO);
			}
			break;
		}
		case (CardsConstants.LOSE_HALF_OF_DESIGN_ARTIFACTS): {
			Random sorteio = new Random();
			int totalArtefatosDesenho = contarArtefatos(jogadorAlvo, Mesa.ARTEFATOS_DESENHO);

			int metadeArtefatos = (int) totalArtefatosDesenho / 2;

			for (int i = 0; i < metadeArtefatos; i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_DESENHO);
			}
			break;
		}
		case (CardsConstants.LOSE_REQUIREMENTS_ARTIFACTS_FOR_EACH_BUG): {
			Random sorteio = new Random();
			int totalArtefatosBug = contarBugs(jogadorAlvo);

			int quantidadeArtefatosPerdidos = totalArtefatosBug * cartaUtilizada.getQuantidadeSegundoEfeito();

			for (int i = 0; i < quantidadeArtefatosPerdidos; i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_REQUISITOS);
			}
			break;
		}
		case (CardsConstants.LOSE_TRAIL_ARTIFACT): {
			Random sorteio = new Random();
			for (int i = 0; i < cartaUtilizada.getQuantidadeSegundoEfeito(); i++) {
				int mesaSorteada = sorteio.nextInt(Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO);
				retirarArtefato(jogadorAlvo, mesaSorteada, Mesa.ARTEFATOS_RASTROS);
			}
			break;
		}
		case (CardsConstants.LOWER_MATURITY_ENGINEER_UNEMPLOYMENT): {
			int maturidadeMinima = 100;
			ArrayList<String> engenheiros = new ArrayList<String>();
                        
                        //percorre mesa
			for (int i = 0; i < jogadorAlvo.getTabuleiro()
					.getMesas().length; i++) 
			{
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa()
						.getEngenheiro().getMaturidadeEngenheiro() <= maturidadeMinima) {

					/** encontrando qual a maturidade minima */
					maturidadeMinima = jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa()
							.getEngenheiro().getMaturidadeEngenheiro();
				}
			}
                        
                        //percorre mesa
			for (int i = 0; i < jogadorAlvo.getTabuleiro()
					.getMesas().length; i++)
			{
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa()
						.getEngenheiro().getMaturidadeEngenheiro() == maturidadeMinima) {
          
					/** encontrando engenheiros com maturidade minima */
					engenheiros.add(jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getNomeEngenheiro());
				}
			}
			Random sorteio = new Random();
			int engenheiroSorteado = sorteio.nextInt(engenheiros.size());
                        
			/** sorteando uma posicao do array cujo engenheiro sera demitido */
			String engenheiroDemitido = engenheiros.get(engenheiroSorteado - 1);
                        
			/** variavel contera nome do engenheiro sorteado a ser demitido */
                        
                        //percorre mesa
			for (int i = 0; i < jogadorAlvo.getTabuleiro()
					.getMesas().length; i++) 
			{
        
        //acha engenheiro
				if (jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getNomeEngenheiro()
						.equals(engenheiroDemitido))
				{
					/* TODO ver */despedirEngenheiro(jogadorAlvo,
							jogadorAlvo.getTabuleiro().getMesas()[i].getCartaMesa());
				}
			}
			break;
		}
		case (CardsConstants.ONLY_INSPECT_ARTIFACTS): {
			for (int i = 0; i < jogadorAlvo.getTabuleiro().getMesas().length; i++) {
				jogadorAlvo.getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_EnginnersNotProduceArtifacts(cartaUtilizada.getDuracaoEfeito());
				jogadorAlvo.getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_EnginnersNotCorrectArtifacts(cartaUtilizada.getDuracaoEfeito());
			}
			break;
		}
		case (CardsConstants.REDUCTION_IN_BUDGET): {
			jogadorAlvo.getTabuleiro().setEfeitoNegativoOrcamento(cartaUtilizada.getQuantidadeSegundoEfeito());
			break;
		}
                
                // default: /**nao havera essa opcao, mas a colocamos por seguranca*/
		case (CardsConstants.TABLE_CHOSEN_LOSE_ALL_CODE_ARTIFACT): {
			int mesa = setupController.escolherMesaSofrerProblema();
			retirarTodosArtefatos(jogadorAlvo, mesa, Mesa.ARTEFATOS_CODIGO);
			break;
		}
		
		// break;
		}

		Carta[] carta = new Carta[1];
		carta[0] = cartaUtilizada;
		retirarCartas(jogadorAtual, carta);
                
		/** removendo carta utilizada */
		return jogadorAtual;
	}

	public boolean verificarCondicao(Jogador jogador, CartaPenalizacao carta) {
		switch (carta
                                /** Conferindo PRIMEIRA condicao */
				.getTipoPrimeiraCondicao()) 
		{
		case (CardsConstants.AFFECTS_ALL_PLAYERS): {
			return true;
		}
		case (CardsConstants.BUG_IN_ARTIFACTS_GREATER_THAN_1): {
			if (contarBugs(jogador) <= 1)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_ARTIFACTS_GREATER_THAN_2): {
			if (contarBugs(jogador) <= 2)
				return false;
			break;

		}
		case (CardsConstants.BUG_IN_ARTIFACTS_GREATER_THAN_3): {
			if (contarBugs(jogador) <= 3)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_ARTIFACTS_GREATER_THAN_4): {
			if (contarBugs(jogador) <= 4)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_ARTIFACTS_GREATER_THAN_5): {
			if (contarBugs(jogador) <= 5)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 0)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS_GREATER_THAN_1): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS_GREATER_THAN_2): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS_GREATER_THAN_3): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS_GREATER_THAN_4): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS_GREATER_THAN_5): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 0)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS_GREATER_THAN_1): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS_GREATER_THAN_2): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS_GREATER_THAN_3): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS_GREATER_THAN_4): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS_GREATER_THAN_5): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 0)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS_GREATER_THAN_1): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 1)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS_GREATER_THAN_2): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 2)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS_GREATER_THAN_3): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 3)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS_GREATER_THAN_4): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 4)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS_GREATER_THAN_5): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 5)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 0)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS_GREATER_THAN_1): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 1)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS_GREATER_THAN_2): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 2)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS_GREATER_THAN_3): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 3)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS_GREATER_THAN_4): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 4)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS_GREATER_THAN_5): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 5)
				return false;
			break;
		}
		case (CardsConstants.CODE_GREATER_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.CODE_GREATER_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.CODE_GREATER_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.CODE_GREATER_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.CODE_GREATER_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.CODE_LESS_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.CODE_LESS_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.CODE_LESS_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.CODE_LESS_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) >= 4)
				return false;
			break;
		}
		case (CardsConstants.CODE_LESS_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) >= 5)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_GREATER_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_GREATER_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_GREATER_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_GREATER_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_GREATER_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 4)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 5)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_6): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 6)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_0): {
			if (contarArtefatosCinzas(jogador) <= 0)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_1): {
			if (contarArtefatosCinzas(jogador) <= 1)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_2): {
			if (contarArtefatosCinzas(jogador) <= 2)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_3): {
			if (contarArtefatosCinzas(jogador) <= 3)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_4): {
			if (contarArtefatosCinzas(jogador) <= 4)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_5): {
			if (contarArtefatosCinzas(jogador) <= 5)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_6): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 6)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 4)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 5)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_6): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 6)
				return false;
			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_1): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 1)
					return false;
			}

			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_2): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 2)
					return false;
			}

			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_3): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 3)
					return false;
			}

			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_4): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 4)
					return false;
			}

			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_5): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 5)
					return false;
			}

			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_6): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 6)
					return false;
			}

			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 0)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED_GREATER_THAN_1): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 1)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED_GREATER_THAN_2): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 2)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED_GREATER_THAN_3): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 3)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED_GREATER_THAN_4): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 4)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED_GREATER_THAN_5): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 5)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_GREATER_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_GREATER_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_GREATER_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_GREATER_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_GREATER_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 4)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 5)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_6): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 6)
				return false;
		}
		case (CardsConstants.SKILL_ENGINEER_GREATER_THAN_1): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() <= 1)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_GREATER_THAN_2): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() <= 2)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_GREATER_THAN_3): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() <= 3)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_GREATER_THAN_4): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() <= 4)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_GREATER_THAN_5): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() <= 5)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_LESS_THAN_1): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() >= 1)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_LESS_THAN_2): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() >= 2)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_LESS_THAN_3): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() >= 3)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_LESS_THAN_4): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() >= 4)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_LESS_THAN_5): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() >= 5)
					return false;
			}
		}
		case (CardsConstants.TABLE_WITH_BUG_CODE_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 0)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_BUG_TRAIL_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 0)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_CODE_LESS_THAN_1): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_CODIGO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_CODE_LESS_THAN_2): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_CODIGO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_CODE_LESS_THAN_3): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_CODIGO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_GREATER_THAN_1): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_GREATER_THAN_2): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_GREATER_THAN_3): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_LESS_THAN_1): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_LESS_THAN_2): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_LESS_THAN_3): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_REQUIREMENTS_LESS_THAN_1): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 1)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_REQUIREMENTS_LESS_THAN_2): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 2)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_REQUIREMENTS_LESS_THAN_3): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 3)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_TRAILS_LESS_THAN_1): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 1)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_TRAILS_LESS_THAN_2): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 2)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_TRAILS_LESS_THAN_3): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 3)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_GREATER_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) <= 1)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_GREATER_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) <= 2)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_GREATER_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) <= 3)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_GREATER_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) <= 4)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_GREATER_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) <= 5)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_LESS_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) >= 1)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_LESS_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) >= 2)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_LESS_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) >= 3)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_LESS_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) >= 4)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_LESS_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) >= 5)
				return false;
			break;
		}
		case (CardsConstants.TWO_ENGINEERS_MATURITY_LESS_THAN_1): {
			contador = 0;
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 1)
					contador++;
			}
			if (contador > (Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO - 2))
				return false;
			break;
		}
		case (CardsConstants.TWO_ENGINEERS_MATURITY_LESS_THAN_2): {
			contador = 0;
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 2)
					contador++;
			}
			if (contador > (Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO - 2))
				return false;
			break;
		}
		case (CardsConstants.TWO_ENGINEERS_MATURITY_LESS_THAN_3): {
			contador = 0;
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 3)
					contador++;
			}
			if (contador > (Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO - 2))
				return false;
			break;
		}
		case (CardsConstants.TWO_ENGINEERS_MATURITY_LESS_THAN_4): {
			contador = 0;
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 4)
					contador++;
			}
			if (contador > (Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO - 2))
				return false;
			break;
		}
		case (CardsConstants.TWO_ENGINEERS_MATURITY_LESS_THAN_5): {
			contador = 0;
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 5)
					contador++;
			}
			if (contador > (Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO - 2))
				return false;
			break;
		}
		default:
			break;
		}
		switch (carta
				.getTipoSegundaCondicao()) /** Conferindo SEGUNDA condicao */
		{
		case (CardsConstants.AFFECTS_ALL_PLAYERS): {
			return true;
		}
		case (CardsConstants.BUG_IN_ARTIFACTS_GREATER_THAN_1): {
			if (contarBugs(jogador) <= 1)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_ARTIFACTS_GREATER_THAN_2): {
			if (contarBugs(jogador) <= 2)
				return false;
			break;

		}
		case (CardsConstants.BUG_IN_ARTIFACTS_GREATER_THAN_3): {
			if (contarBugs(jogador) <= 3)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_ARTIFACTS_GREATER_THAN_4): {
			if (contarBugs(jogador) <= 4)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_ARTIFACTS_GREATER_THAN_5): {
			if (contarBugs(jogador) <= 5)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 0)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS_GREATER_THAN_1): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS_GREATER_THAN_2): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS_GREATER_THAN_3): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS_GREATER_THAN_4): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_CODE_ARTIFACTS_GREATER_THAN_5): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 0)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS_GREATER_THAN_1): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS_GREATER_THAN_2): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS_GREATER_THAN_3): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS_GREATER_THAN_4): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_DESIGN_ARTIFACTS_GREATER_THAN_5): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_DESENHO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 0)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS_GREATER_THAN_1): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 1)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS_GREATER_THAN_2): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 2)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS_GREATER_THAN_3): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 3)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS_GREATER_THAN_4): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 4)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_REQUIREMENTS_ARTIFACTS_GREATER_THAN_5): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 5)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 0)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS_GREATER_THAN_1): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 1)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS_GREATER_THAN_2): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 2)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS_GREATER_THAN_3): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 3)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS_GREATER_THAN_4): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 4)
				return false;
			break;
		}
		case (CardsConstants.BUG_IN_TRAIL_ARTIFACTS_GREATER_THAN_5): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 5)
				return false;
			break;
		}
		case (CardsConstants.CODE_GREATER_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.CODE_GREATER_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.CODE_GREATER_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.CODE_GREATER_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.CODE_GREATER_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.CODE_LESS_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.CODE_LESS_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.CODE_LESS_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.CODE_LESS_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) >= 4)
				return false;
			break;
		}
		case (CardsConstants.CODE_LESS_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_CODIGO) >= 5)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_GREATER_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_GREATER_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_GREATER_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_GREATER_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_GREATER_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 4)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 5)
				return false;
			break;
		}
		case (CardsConstants.DESIGN_LESS_THAN_6): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 6)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_0): {
			if (contarArtefatosCinzas(jogador) <= 0)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_1): {
			if (contarArtefatosCinzas(jogador) <= 1)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_2): {
			if (contarArtefatosCinzas(jogador) <= 2)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_3): {
			if (contarArtefatosCinzas(jogador) <= 3)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_4): {
			if (contarArtefatosCinzas(jogador) <= 4)
				return false;
			break;
		}
		case (CardsConstants.GREY_REQUIREMENTS_ARTIFACTS_GREATER_THAN_5): {
			if (contarArtefatosCinzas(jogador) <= 5)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.HELP_GREATER_THAN_6): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 6)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 4)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 5)
				return false;
			break;
		}
		case (CardsConstants.HELP_LESS_THAN_6): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 6)
				return false;
			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_1): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 1)
					return false;
			}

			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_2): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 2)
					return false;
			}

			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_3): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 3)
					return false;
			}

			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_4): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 4)
					return false;
			}

			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_5): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 5)
					return false;
			}

			break;
		}
		case (CardsConstants.MATURITY_LESS_THAN_6): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 6)
					return false;
			}

			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 0)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED_GREATER_THAN_1): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 1)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED_GREATER_THAN_2): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 2)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED_GREATER_THAN_3): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 3)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED_GREATER_THAN_4): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 4)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_ARE_NOT_INSPECTED_GREATER_THAN_5): {
			if (contarArtefatosNaoInspecionados(jogador, Mesa.ARTEFATOS_REQUISITOS) <= 5)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_GREATER_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_GREATER_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_GREATER_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_GREATER_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 4)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_GREATER_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) <= 5)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 4)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 5)
				return false;
			break;
		}
		case (CardsConstants.REQUIREMENTS_LESS_THAN_6): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_DESENHO) >= 6)
				return false;
		}
		case (CardsConstants.SKILL_ENGINEER_GREATER_THAN_1): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() <= 1)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_GREATER_THAN_2): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() <= 2)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_GREATER_THAN_3): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() <= 3)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_GREATER_THAN_4): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() <= 4)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_GREATER_THAN_5): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() <= 5)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_LESS_THAN_1): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() >= 1)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_LESS_THAN_2): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() >= 2)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_LESS_THAN_3): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() >= 3)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_LESS_THAN_4): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() >= 4)
					return false;
			}
		}
		case (CardsConstants.SKILL_ENGINEER_LESS_THAN_5): {
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getHabilidadeEngenheiro() >= 5)
					return false;
			}
		}
		case (CardsConstants.TABLE_WITH_BUG_CODE_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_CODIGO) <= 0)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_BUG_TRAIL_ARTIFACTS): {
			if (contarBugs(jogador, Mesa.ARTEFATOS_RASTROS) <= 0)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_CODE_LESS_THAN_1): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_CODIGO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_CODE_LESS_THAN_2): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_CODIGO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_CODE_LESS_THAN_3): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_CODIGO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_GREATER_THAN_1): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) <= 1)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_GREATER_THAN_2): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) <= 2)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_GREATER_THAN_3): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) <= 3)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_LESS_THAN_1): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) >= 1)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_LESS_THAN_2): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) >= 2)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_DESIGN_LESS_THAN_3): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_DESENHO) >= 3)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_REQUIREMENTS_LESS_THAN_1): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 1)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_REQUIREMENTS_LESS_THAN_2): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 2)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_REQUIREMENTS_LESS_THAN_3): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 3)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_TRAILS_LESS_THAN_1): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 1)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_TRAILS_LESS_THAN_2): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 2)
				return false;
			break;
		}
		case (CardsConstants.TABLE_WITH_TRAILS_LESS_THAN_3): {
			if (contarArtefatosMesa(jogador, Mesa.ARTEFATOS_REQUISITOS) >= 3)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_GREATER_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) <= 1)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_GREATER_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) <= 2)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_GREATER_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) <= 3)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_GREATER_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) <= 4)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_GREATER_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) <= 5)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_LESS_THAN_1): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) >= 1)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_LESS_THAN_2): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) >= 2)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_LESS_THAN_3): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) >= 3)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_LESS_THAN_4): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) >= 4)
				return false;
			break;
		}
		case (CardsConstants.TRAIL_LESS_THAN_5): {
			if (contarArtefatos(jogador, Mesa.ARTEFATOS_RASTROS) >= 5)
				return false;
			break;
		}
		case (CardsConstants.TWO_ENGINEERS_MATURITY_LESS_THAN_1): {
			contador = 0;
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 1)
					contador++;
			}
			if (contador > (Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO - 2))
				return false;
			break;
		}
		case (CardsConstants.TWO_ENGINEERS_MATURITY_LESS_THAN_2): {
			contador = 0;
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 2)
					contador++;
			}
			if (contador > (Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO - 2))
				return false;
			break;
		}
		case (CardsConstants.TWO_ENGINEERS_MATURITY_LESS_THAN_3): {
			contador = 0;
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 3)
					contador++;
			}
			if (contador > (Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO - 2))
				return false;
			break;
		}
		case (CardsConstants.TWO_ENGINEERS_MATURITY_LESS_THAN_4): {
			contador = 0;
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 4)
					contador++;
			}
			if (contador > (Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO - 2))
				return false;
			break;
		}
		case (CardsConstants.TWO_ENGINEERS_MATURITY_LESS_THAN_5): {
			contador = 0;
			for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
					continue;
				if (jogador.getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getMaturidadeEngenheiro() >= 5)
					contador++;
			}
			if (contador > (Tabuleiro.NUMERO_MAX_MESAS_TABULEIRO - 2))
				return false;
			break;
		}
		default:
			break;
		}

		return true;
	}

	public int contarBugs(Jogador jogador) {
		contador = 0;
		for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
			if (jogador.getTabuleiro().getMesas()[i].getAjudas().size() > 0)
				contador = contador + jogador.getTabuleiro().getMesas()[i]
						.somarArtefatosInspecionadosBug(jogador.getTabuleiro().getMesas()[i].getAjudas());

			if (jogador.getTabuleiro().getMesas()[i].getCodigos().size() > 0)
				contador = contador + jogador.getTabuleiro().getMesas()[i]
						.somarArtefatosInspecionadosBug(jogador.getTabuleiro().getMesas()[i].getCodigos());

			if (jogador.getTabuleiro().getMesas()[i].getDesenhos().size() > 0)
				contador = contador + jogador.getTabuleiro().getMesas()[i]
						.somarArtefatosInspecionadosBug(jogador.getTabuleiro().getMesas()[i].getDesenhos());

			if (jogador.getTabuleiro().getMesas()[i].getRastros().size() > 0)
				contador = contador + jogador.getTabuleiro().getMesas()[i]
						.somarArtefatosInspecionadosBug(jogador.getTabuleiro().getMesas()[i].getRastros());

			if (jogador.getTabuleiro().getMesas()[i].getRequisitos().size() > 0)
				contador = contador + jogador.getTabuleiro().getMesas()[i]
						.somarArtefatosInspecionadosBug(jogador.getTabuleiro().getMesas()[i].getRequisitos());
		}
		return contador;
	}

	public int contarBugs(Jogador jogador, int tipoArtefato) {
		contador = 0;
		for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
			if (tipoArtefato == Mesa.ARTEFATOS_AJUDA)
				if (jogador.getTabuleiro().getMesas()[i].getAjudas().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i]
							.somarArtefatosInspecionadosBug(jogador.getTabuleiro().getMesas()[i].getAjudas());
			if (tipoArtefato == Mesa.ARTEFATOS_CODIGO)
				if (jogador.getTabuleiro().getMesas()[i].getCodigos().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i]
							.somarArtefatosInspecionadosBug(jogador.getTabuleiro().getMesas()[i].getCodigos());
			if (tipoArtefato == Mesa.ARTEFATOS_DESENHO)
				if (jogador.getTabuleiro().getMesas()[i].getDesenhos().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i]
							.somarArtefatosInspecionadosBug(jogador.getTabuleiro().getMesas()[i].getDesenhos());
			if (tipoArtefato == Mesa.ARTEFATOS_RASTROS)
				if (jogador.getTabuleiro().getMesas()[i].getRastros().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i]
							.somarArtefatosInspecionadosBug(jogador.getTabuleiro().getMesas()[i].getRastros());
			if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS)
				if (jogador.getTabuleiro().getMesas()[i].getRequisitos().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i]
							.somarArtefatosInspecionadosBug(jogador.getTabuleiro().getMesas()[i].getRequisitos());
		}
		return contador;
	}

	public int contarArtefatos(Jogador jogador, int tipoArtefato) {
		contador = 0;
		for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
			if (tipoArtefato == Mesa.ARTEFATOS_AJUDA)
				contador = contador + jogador.getTabuleiro().getMesas()[i].getAjudas().size();
			if (tipoArtefato == Mesa.ARTEFATOS_CODIGO)
				contador = contador + jogador.getTabuleiro().getMesas()[i].getCodigos().size();
			if (tipoArtefato == Mesa.ARTEFATOS_DESENHO)
				contador = contador + jogador.getTabuleiro().getMesas()[i].getDesenhos().size();
			if (tipoArtefato == Mesa.ARTEFATOS_RASTROS)
				contador = contador + jogador.getTabuleiro().getMesas()[i].getRastros().size();
			if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS)
				contador = contador + jogador.getTabuleiro().getMesas()[i].getRequisitos().size();
		}
		return contador;
	}

	public int contarArtefatosCinzas(Jogador jogador) {
		contador = 0;
		for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
			for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getAjudas().size(); j++) {
				if (jogador.getTabuleiro().getMesas()[i].getAjudas().get(j).isPoorQuality() == true)
					contador++;
			}
			for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getCodigos().size(); j++) {
				if (jogador.getTabuleiro().getMesas()[i].getCodigos().get(j).isPoorQuality() == true)
					contador++;
			}
			for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getDesenhos().size(); j++) {
				if (jogador.getTabuleiro().getMesas()[i].getDesenhos().get(j).isPoorQuality() == true)
					contador++;
			}
			for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getRastros().size(); j++) {
				if (jogador.getTabuleiro().getMesas()[i].getRastros().get(j).isPoorQuality() == true)
					contador++;
			}
			for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getRequisitos().size(); j++) {
				if (jogador.getTabuleiro().getMesas()[i].getRequisitos().get(j).isPoorQuality() == true)
					contador++;
			}
		}
		return contador;
	}

	public int contarArtefatosNaoInspecionados(Jogador jogador, int tipoArtefato) {
		contador = 0;
		for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
			if (tipoArtefato == Mesa.ARTEFATOS_AJUDA)
				if (jogador.getTabuleiro().getMesas()[i].getAjudas().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i]
							.somarArtefatosNotInspecionados(jogador.getTabuleiro().getMesas()[i].getAjudas());

			if (tipoArtefato == Mesa.ARTEFATOS_CODIGO)
				if (jogador.getTabuleiro().getMesas()[i].getCodigos().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i]
							.somarArtefatosNotInspecionados(jogador.getTabuleiro().getMesas()[i].getCodigos());

			if (tipoArtefato == Mesa.ARTEFATOS_DESENHO)
				if (jogador.getTabuleiro().getMesas()[i].getDesenhos().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i]
							.somarArtefatosNotInspecionados(jogador.getTabuleiro().getMesas()[i].getDesenhos());

			if (tipoArtefato == Mesa.ARTEFATOS_RASTROS)
				if (jogador.getTabuleiro().getMesas()[i].getRastros().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i]
							.somarArtefatosNotInspecionados(jogador.getTabuleiro().getMesas()[i].getRastros());

			if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS)
				if (jogador.getTabuleiro().getMesas()[i].getRequisitos().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i]
							.somarArtefatosNotInspecionados(jogador.getTabuleiro().getMesas()[i].getRequisitos());
		}
		return contador;
	}

	public int contarArtefatosMesa(Jogador jogador, int tipoArtefato) {
		contador = 0;
		int max = contador;
		for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
			if (tipoArtefato == Mesa.ARTEFATOS_AJUDA)
				if (jogador.getTabuleiro().getMesas()[i].getAjudas().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i].getAjudas().size();

			if (tipoArtefato == Mesa.ARTEFATOS_CODIGO)
				if (jogador.getTabuleiro().getMesas()[i].getCodigos().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i].getCodigos().size();

			if (tipoArtefato == Mesa.ARTEFATOS_DESENHO)
				if (jogador.getTabuleiro().getMesas()[i].getDesenhos().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i].getDesenhos().size();

			if (tipoArtefato == Mesa.ARTEFATOS_RASTROS)
				if (jogador.getTabuleiro().getMesas()[i].getRastros().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i].getRastros().size();

			if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS)
				if (jogador.getTabuleiro().getMesas()[i].getRequisitos().size() > 0)
					contador = contador + jogador.getTabuleiro().getMesas()[i].getRequisitos().size();

			if (contador > max)
				max = contador;
			contador = 0;
		}
		return max;
	}

	// Todos os engenheiros perdem artefatos
	public void allEngineerLoseArtifacts(Jogador jogador, int quantidadeArtefato, int tipoArtefato) {
		Random sorteio = new Random();
    
		// percorrendo mesas dos engenheiros
		for (int i = 0; i < jogador.getTabuleiro().getMesas().length; i++) {
			if (jogador.getTabuleiro().getMesas()[i].getCartaMesa() == null)
                            
				/** se mesa nao tem engenheiro, pula iteracao */
				continue;

			// se deve-se retirar todos os artefatos
			if (quantidadeArtefato == ALL_ARTIFACTS) {
				this.removeAllArtifacts(jogador, tipoArtefato, i);
			}
			/** se nao for todos os artefatos */
			else {
				this.removeSomeArtifacts(jogador, quantidadeArtefato, tipoArtefato, sorteio, i);
			}
		}
	}

	private void removeSomeArtifacts(Jogador jogador, int quantidadeArtefato, int tipoArtefato, Random sorteio, int i) {
    
		// ira retirar a quantidade de artefato de cada engenheiro
		for (int j = 0; j < quantidadeArtefato; j++) {
      
			 // se for qualquer tipo de de artefato a ser retirado
			if (tipoArtefato == ANY_ARTIFACTS) {
				boolean retirou = false;
        
				// enquanto nao se retirar artefato e existir artefato no tabuleiro, repete-se loop
				while (retirou == false) {
					int tipoArtefatoSorteado = sorteio.nextInt(5);
          
					/**
					 * sorteando qual tipo de artefato ira ser retirado
					 */
					if ((tipoArtefatoSorteado == Mesa.ARTEFATOS_AJUDA)
							&& (jogador.getTabuleiro().getMesas()[i].getAjudas().size() > 0)) {
						retirarArtefato(jogador, i, Mesa.ARTEFATOS_AJUDA);
						retirou = true;
					}
					if ((tipoArtefatoSorteado == Mesa.ARTEFATOS_CODIGO)
							&& (jogador.getTabuleiro().getMesas()[i].getCodigos().size() > 0)) {
						retirarArtefato(jogador, i, Mesa.ARTEFATOS_CODIGO);
						retirou = true;
					}
					if ((tipoArtefatoSorteado == Mesa.ARTEFATOS_DESENHO)
							&& (jogador.getTabuleiro().getMesas()[i].getDesenhos().size() > 0)) {
						retirarArtefato(jogador, i, Mesa.ARTEFATOS_DESENHO);
						retirou = true;
					}
					if ((tipoArtefatoSorteado == Mesa.ARTEFATOS_RASTROS)
							&& (jogador.getTabuleiro().getMesas()[i].getRastros().size() > 0)) {
						retirarArtefato(jogador, i, Mesa.ARTEFATOS_RASTROS);
						retirou = true;
					}
					if ((tipoArtefatoSorteado == Mesa.ARTEFATOS_REQUISITOS)
							&& (jogador.getTabuleiro().getMesas()[i].getRequisitos().size() > 0)) {
						retirarArtefato(jogador, i, Mesa.ARTEFATOS_REQUISITOS);
						retirou = true;
					}
					if ((jogador.getTabuleiro().getMesas()[i].getAjudas().size() == 0)
							&& (jogador.getTabuleiro().getMesas()[i].getCodigos().size() == 0)
							&& (jogador.getTabuleiro().getMesas()[i].getDesenhos().size() == 0)
							&& (jogador.getTabuleiro().getMesas()[i].getRastros().size() == 0)
							&& (jogador.getTabuleiro().getMesas()[i].getRequisitos().size() == 0))
            
						/**
						 * se chegou ate aqui, nao ha mais o que se
						 * retirar no tabuleiro do jogador
						 */
						break;
          
					/** sai do while */
				}
			}

			if ((tipoArtefato == Mesa.ARTEFATOS_AJUDA)
					&& (jogador.getTabuleiro().getMesas()[i].getAjudas().size() > 0)) {
				retirarArtefato(jogador, i, Mesa.ARTEFATOS_AJUDA);
			}
			if ((tipoArtefato == Mesa.ARTEFATOS_CODIGO)
					&& (jogador.getTabuleiro().getMesas()[i].getCodigos().size() > 0)) {
				retirarArtefato(jogador, i, Mesa.ARTEFATOS_CODIGO);
			}
			if ((tipoArtefato == Mesa.ARTEFATOS_DESENHO)
					&& (jogador.getTabuleiro().getMesas()[i].getDesenhos().size() > 0)) {
				retirarArtefato(jogador, i, Mesa.ARTEFATOS_DESENHO);
			}
			if ((tipoArtefato == Mesa.ARTEFATOS_RASTROS)
					&& (jogador.getTabuleiro().getMesas()[i].getRastros().size() > 0)) {
				retirarArtefato(jogador, i, Mesa.ARTEFATOS_RASTROS);
			}
			if ((tipoArtefato == Mesa.ARTEFATOS_REQUISITOS)
					&& (jogador.getTabuleiro().getMesas()[i].getRequisitos().size() > 0)) {
				retirarArtefato(jogador, i, Mesa.ARTEFATOS_REQUISITOS);
			}
		}
	}

	private void removeAllArtifacts(Jogador jogador, int tipoArtefato, int i) {
    
		// caso, seja todos os tipos de artefatos a serem retirados
		if (tipoArtefato == ANY_ARTIFACTS) {
      
			/** retira todos os artefatos */
			retirarTodosArtefatos(jogador, i, Mesa.ARTEFATOS_AJUDA);
			retirarTodosArtefatos(jogador, i, Mesa.ARTEFATOS_CODIGO);
			retirarTodosArtefatos(jogador, i, Mesa.ARTEFATOS_DESENHO);
			retirarTodosArtefatos(jogador, i, Mesa.ARTEFATOS_RASTROS);
			retirarTodosArtefatos(jogador, i, Mesa.ARTEFATOS_REQUISITOS);
		}

		if (tipoArtefato == Mesa.ARTEFATOS_AJUDA)
      
			/** caso, seja todos os tipos de artefatos de ajuda */
			retirarTodosArtefatos(jogador, i, Mesa.ARTEFATOS_AJUDA);
    
		/** retira todos os artefatos de ajuda */
		if (tipoArtefato == Mesa.ARTEFATOS_CODIGO)
			retirarTodosArtefatos(jogador, i, Mesa.ARTEFATOS_CODIGO);
		if (tipoArtefato == Mesa.ARTEFATOS_DESENHO)
			retirarTodosArtefatos(jogador, i, Mesa.ARTEFATOS_DESENHO);
		if (tipoArtefato == Mesa.ARTEFATOS_RASTROS)
			retirarTodosArtefatos(jogador, i, Mesa.ARTEFATOS_RASTROS);
		if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS)
			retirarTodosArtefatos(jogador, i, Mesa.ARTEFATOS_REQUISITOS);
	}

	public void retirarTodosArtefatos(Jogador jogador, int mesa, int tipoArtefato) {
		int auxiliar = 0;
                
		/**
		 * sempre, quando se remove um objeto do arrayList, todos os elementos
		 * acima desse indice sao deslocados para baixo por 1
		 */
		if (tipoArtefato == Mesa.ARTEFATOS_AJUDA) {
                        
                        // retirando todos os artefatos de ajuda
			while (jogador.getTabuleiro().getMesas()[mesa].getAjudas()
					.size() != 0) /**
									 * 
									 * 
									 *
									 */
			{
				if (jogador.getTabuleiro().getMesas()[mesa].getAjudas().get(auxiliar).isPoorQuality() == true) {
					baralhoArtefatosRuins[BARALHO_AUXILIAR]
							.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getAjudas().get(auxiliar));
                                        
					/** recolhendo artefato para baralho */
					jogador.getTabuleiro().getMesas()[mesa].getAjudas().remove(auxiliar);
                                        
					/** removendo 1 artefato na posicao auxiliar */
				} else {
					baralhoArtefatosBons[BARALHO_AUXILIAR]
							.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getAjudas().get(auxiliar));
                                        
					/** recolhendo artefato para baralho */
					jogador.getTabuleiro().getMesas()[mesa].getAjudas().remove(auxiliar);
                                        
					/** removendo 1 artefato na posicao auxiliar */
				}

			}
		}
		if (tipoArtefato == Mesa.ARTEFATOS_CODIGO) {
      
                        //retirando todos os artefatos de codigo
			while (jogador.getTabuleiro().getMesas()[mesa].getCodigos()
					.size() != 0)
			{
				if (jogador.getTabuleiro().getMesas()[mesa].getCodigos().get(auxiliar).isPoorQuality() == true) {
					baralhoArtefatosRuins[BARALHO_AUXILIAR]
							.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getCodigos().get(auxiliar));
                                        
					/** recolhendo artefato para baralho */
					jogador.getTabuleiro().getMesas()[mesa].getCodigos().remove(auxiliar);
                                        
					/** removendo 1 artefato na posicao auxiliar */
				} else {
					baralhoArtefatosBons[BARALHO_AUXILIAR]
							.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getCodigos().get(auxiliar));
                                        
					/** recolhendo artefato para baralho */
					jogador.getTabuleiro().getMesas()[mesa].getCodigos().remove(auxiliar);
                                        
					/** removendo 1 artefato na posicao auxiliar */
				}

			}
		}
		if (tipoArtefato == Mesa.ARTEFATOS_DESENHO) {
                    
                        //retirando todos os artefatos de desenhos
			while (jogador.getTabuleiro().getMesas()[mesa].getDesenhos()
					.size() != 0)
			{
				if (jogador.getTabuleiro().getMesas()[mesa].getDesenhos().get(auxiliar).isPoorQuality() == true) {
					baralhoArtefatosRuins[BARALHO_AUXILIAR]
							.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getDesenhos().get(auxiliar));
                                        
					/** recolhendo artefato para baralho */
					jogador.getTabuleiro().getMesas()[mesa].getDesenhos().remove(auxiliar);
                                        
					/** removendo 1 artefato na posicao auxiliar */
				} else {
					baralhoArtefatosBons[BARALHO_AUXILIAR]
							.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getDesenhos().get(auxiliar));
                                        
					/** recolhendo artefato para baralho */
					jogador.getTabuleiro().getMesas()[mesa].getDesenhos().remove(auxiliar);
                                        
					/** removendo 1 artefato na posicao auxiliar */
				}

			}
		}
		if (tipoArtefato == Mesa.ARTEFATOS_RASTROS) {
                    
                        //retirando todos os artefatos de rastros
			while (jogador.getTabuleiro().getMesas()[mesa].getRastros()
					.size() != 0) 
			{
				if (jogador.getTabuleiro().getMesas()[mesa].getRastros().get(auxiliar).isPoorQuality() == true) {
					baralhoArtefatosRuins[BARALHO_AUXILIAR]
							.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getRastros().get(auxiliar));
                                        
					/** recolhendo artefato para baralho */
					jogador.getTabuleiro().getMesas()[mesa].getRastros().remove(auxiliar);
                                        
					/** removendo 1 artefato na posicao auxiliar */
				} else {
					baralhoArtefatosBons[BARALHO_AUXILIAR]
							.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getRastros().get(auxiliar));
                                        
					/** recolhendo artefato para baralho */
					jogador.getTabuleiro().getMesas()[mesa].getRastros().remove(auxiliar);
                                        
					/** removendo 1 artefato na posicao auxiliar */
				}

			}
		}
		if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS) {
      
                        //retirando todos os artefatos de requisitos
			while (jogador.getTabuleiro().getMesas()[mesa].getRequisitos()
					.size() != 0) 
			{
				if (jogador.getTabuleiro().getMesas()[mesa].getRequisitos().get(auxiliar).isPoorQuality() == true) {
					baralhoArtefatosRuins[BARALHO_AUXILIAR]
							.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getRequisitos().get(auxiliar));
                                        
					/** recolhendo artefato para baralho */
					jogador.getTabuleiro().getMesas()[mesa].getRequisitos().remove(auxiliar);
                                        
					/** removendo 1 artefato na posicao auxiliar */
				} else {
					baralhoArtefatosBons[BARALHO_AUXILIAR]
							.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getRequisitos().get(auxiliar));
                                        
					/** recolhendo artefato para baralho */
					jogador.getTabuleiro().getMesas()[mesa].getRequisitos().remove(auxiliar);
                                        
					/** removendo 1 artefato na posicao auxiliar */
				}

			}
		}

	}

	public void retirarArtefato(Jogador jogador, int mesa, int tipoArtefato) {
		Random sorteio = new Random();
                
		/** sorteara qual artefato retirar de um arrayList */
		if (tipoArtefato == Mesa.ARTEFATOS_AJUDA) {
			int sorteado = sorteio.nextInt(jogador.getTabuleiro().getMesas()[mesa].getAjudas().size());
                        
			/** sorteou o artefato a ser retirado */
			if (jogador.getTabuleiro().getMesas()[mesa].getAjudas().get(sorteado).isPoorQuality() == true) {
				baralhoArtefatosRuins[BARALHO_AUXILIAR]
						.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getAjudas().get(sorteado));
                                
				/** recolhendo artefato para baralho */
				jogador.getTabuleiro().getMesas()[mesa].getAjudas().remove(sorteado);
                                
				/** removendo 1 artefato na posicao do sorteio */
			} else {
				baralhoArtefatosBons[BARALHO_AUXILIAR]
						.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getAjudas().get(sorteado));
                                
				/** recolhendo artefato para baralho */
				jogador.getTabuleiro().getMesas()[mesa].getAjudas().remove(sorteado);
                                
				/** removendo 1 artefato na posicao do sorteio */
			}

		}
		if (tipoArtefato == Mesa.ARTEFATOS_CODIGO) {
			int sorteado = sorteio.nextInt(jogador.getTabuleiro().getMesas()[mesa].getCodigos().size());
                        
			/** sorteou o artefato a ser retirado */
			if (jogador.getTabuleiro().getMesas()[mesa].getCodigos().get(sorteado).isPoorQuality() == true) {
				baralhoArtefatosRuins[BARALHO_AUXILIAR]
						.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getCodigos().get(sorteado));
                                
				/** recolhendo artefato para baralho */
				jogador.getTabuleiro().getMesas()[mesa].getCodigos().remove(sorteado);
                                
				/** removendo 1 artefato na posicao do sorteio */
			} else {
				baralhoArtefatosBons[BARALHO_AUXILIAR]
						.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getCodigos().get(sorteado));
                                
				/** recolhendo artefato para baralho */
				jogador.getTabuleiro().getMesas()[mesa].getCodigos().remove(sorteado);
                                
				/** removendo 1 artefato na posicao do sorteio */
			}

		}
		if (tipoArtefato == Mesa.ARTEFATOS_DESENHO) {
			int sorteado = sorteio.nextInt(jogador.getTabuleiro().getMesas()[mesa].getDesenhos().size());
                        
			/** sorteou o artefato a ser retirado */
			if (jogador.getTabuleiro().getMesas()[mesa].getDesenhos().get(sorteado).isPoorQuality() == true) {
				baralhoArtefatosRuins[BARALHO_AUXILIAR]
						.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getDesenhos().get(sorteado));
                                
				/** recolhendo artefato para baralho */
				jogador.getTabuleiro().getMesas()[mesa].getDesenhos().remove(sorteado);
                                
				/** removendo 1 artefato na posicao do sorteio */
			} else {
				baralhoArtefatosBons[BARALHO_AUXILIAR]
						.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getDesenhos().get(sorteado));
        
				/** recolhendo artefato para baralho */
				jogador.getTabuleiro().getMesas()[mesa].getDesenhos().remove(sorteado);
                                
				/** removendo 1 artefato na posicao do sorteio */
			}

		}
		if (tipoArtefato == Mesa.ARTEFATOS_RASTROS) {
                    
                        /** sorteou o artefato a ser retirado */
			int sorteado = sorteio.nextInt(jogador.getTabuleiro().getMesas()[mesa].getRastros().size());
			
			if (jogador.getTabuleiro().getMesas()[mesa].getRastros().get(sorteado).isPoorQuality() == true) {
				baralhoArtefatosRuins[BARALHO_AUXILIAR]
                                        
                                                /** recolhendo artefato para baralho */
						.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getRastros().get(sorteado));
				
				jogador.getTabuleiro().getMesas()[mesa].getRastros().remove(sorteado);
				/** removendo 1 artefato na posicao do sorteio */
			} else {
				baralhoArtefatosBons[BARALHO_AUXILIAR]
                                        
                                                /** recolhendo artefato para baralho */
						.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getRastros().get(sorteado));
				
                                
                                /** removendo 1 artefato na posicao do sorteio */
				jogador.getTabuleiro().getMesas()[mesa].getRastros().remove(sorteado);
				
			}

		}
		if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS) {
                    
                        /** sorteou o artefato a ser retirado */
			int sorteado = sorteio.nextInt(jogador.getTabuleiro().getMesas()[mesa].getRequisitos().size());
			
			if (jogador.getTabuleiro().getMesas()[mesa].getRequisitos().get(sorteado).isPoorQuality() == true) {
				baralhoArtefatosRuins[BARALHO_AUXILIAR]
                                                
                                                /** recolhendo artefato para baralho */
						.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getRequisitos().get(sorteado));
				
                                /** removendo 1 artefato na posicao do sorteio */
				jogador.getTabuleiro().getMesas()[mesa].getRequisitos().remove(sorteado);
				
			} else {
				baralhoArtefatosBons[BARALHO_AUXILIAR]
                                        
                                                /** recolhendo artefato para baralho */
						.recolherArtefato(jogador.getTabuleiro().getMesas()[mesa].getRequisitos().get(sorteado));
				
                                /** removendo 1 artefato na posicao do sorteio */
				jogador.getTabuleiro().getMesas()[mesa].getRequisitos().remove(sorteado);
				
			}

		}
	}

	public void changeWhiteArtifactsByGrayArtifacts(Jogador jogador, int tipoArtefato) {
            
                //percorrendo mesas do jogador
		for (int i = 0; i < jogador.getTabuleiro()
				.getMesas().length; i++)
		{
			if (tipoArtefato == Mesa.ARTEFATOS_AJUDA) {
				boolean percorreuTudo = false;
                                
                                //percorrendo vetor de artefatos de ajuda na mesa
				while (percorreuTudo == false) {
					for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getAjudas()
							.size(); j++) 
					{
						if (jogador.getTabuleiro().getMesas()[i].getAjudas().get(j).isPoorQuality() == false) {
							baralhoArtefatosBons[BARALHO_AUXILIAR]
									.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getAjudas().remove(j));
							jogador.getTabuleiro().getMesas()[i].getAjudas()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
							break;
                                                        
							/**
							 * nao pode-se percorrer um arrayList por for ja que
							 * ha reorganizacao do array,logo deve-se retirar
							 * somente um elemento por vez
							 */
						}
					}
					percorreuTudo = true;
                                        
					/**
					 * se percorreu todo o array, nao ha artefatos brancos nele
					 */
				}
			}

			if (tipoArtefato == Mesa.ARTEFATOS_CODIGO) {
				boolean percorreuTudo = false;
                                
                                //percorrendo vetor de artefatos de codigos na mesa
				while (percorreuTudo == false) {
					for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getCodigos()
							.size(); j++)
					{
						if (jogador.getTabuleiro().getMesas()[i].getCodigos().get(j).isPoorQuality() == false) {
							baralhoArtefatosBons[BARALHO_AUXILIAR]
									.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getCodigos().remove(j));
							jogador.getTabuleiro().getMesas()[i].getCodigos()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
							break;
                                                        
							/**
							 * nao pode-se percorrer um arrayList por for ja que
							 * ha reorganizacao do array,logo deve-se retirar
							 * somente um elemento por vez
							 */
						}
					}
					percorreuTudo = true;
                                        
					/**
					 * se percorreu todo o array, nao ha artefatos brancos nele
					 */
				}
			}

			if (tipoArtefato == Mesa.ARTEFATOS_DESENHO) {
				boolean percorreuTudo = false;
                                
                                //percorrendo vetor de artefatos de desenho na mesa
				while (percorreuTudo == false) {
					for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getDesenhos()
							.size(); j++) 
					{
						if (jogador.getTabuleiro().getMesas()[i].getDesenhos().get(j).isPoorQuality() == false) {
							baralhoArtefatosBons[BARALHO_AUXILIAR]
									.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getDesenhos().remove(j));
							jogador.getTabuleiro().getMesas()[i].getDesenhos()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
							break;
                                                        
							/**
							 * nao pode-se percorrer um arrayList por for ja que
							 * ha reorganizacao do array,logo deve-se retirar
							 * somente um elemento por vez
							 */
						}
					}
					percorreuTudo = true;
                                        
					/**
					 * se percorreu todo o array, nao ha artefatos brancos nele
					 */
				}
			}

			if (tipoArtefato == Mesa.ARTEFATOS_RASTROS) {
				boolean percorreuTudo = false;
                                
                                //percorrendo vetor de artefatos de rastros na mesa
				while (percorreuTudo == false) {
					for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getRastros()
							.size(); j++)
					{
						if (jogador.getTabuleiro().getMesas()[i].getRastros().get(j).isPoorQuality() == false) {
							baralhoArtefatosBons[BARALHO_AUXILIAR]
									.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getRastros().remove(j));
							jogador.getTabuleiro().getMesas()[i].getRastros()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
							break;
                                                        
							/**
							 * nao pode-se percorrer um arrayList por for ja que
							 * ha reorganizacao do array,logo deve-se retirar
							 * somente um elemento por vez
							 */
						}
					}
					percorreuTudo = true;
                                        
					/**
					 * se percorreu todo o array, nao ha artefatos brancos nele
					 */
				}
			}

			if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS) {
				boolean percorreuTudo = false;
                                
                                //percorrendo vetor de artefatos de requisitos na mesa
				while (percorreuTudo == false) {
					for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getRequisitos()
							.size(); j++) 
					{
						if (jogador.getTabuleiro().getMesas()[i].getRequisitos().get(j).isPoorQuality() == false) {
							baralhoArtefatosBons[BARALHO_AUXILIAR]
									.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getRequisitos().remove(j));
							jogador.getTabuleiro().getMesas()[i].getRequisitos()
									.add(baralhoArtefatosRuins[BARALHO_PRINCIPAL].darArtefato());
							break;
                                                        
							/**
							 * nao pode-se percorrer um arrayList por for ja que
							 * ha reorganizacao do array,logo deve-se retirar
							 * somente um elemento por vez
							 */
						}
					}
					percorreuTudo = true;
                                        
					/**
					 * se percorreu todo o array, nao ha artefatos brancos nele
					 */
				}

			}

		}
	}

	public void retirarTodosArtefatosCinzas(Jogador jogador, int tipoArtefato) {
		if (tipoArtefato == Mesa.ARTEFATOS_AJUDA) {
                    
                        //percorrendo mesas
			for (int i = 0; i < jogador.getTabuleiro()
					.getMesas().length; i++)
			{
				if (jogador.getTabuleiro().getMesas()[i].getAjudas().size() > 0) {
					boolean percorreuTudo = false;
                                        
                                        //percorrendo vetor de artefatos de ajuda na mesa
					while (percorreuTudo == false) {
						for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getAjudas()
								.size(); j++) 
						{
							if (jogador.getTabuleiro().getMesas()[i].getAjudas().get(j).isPoorQuality() == true) {
								baralhoArtefatosRuins[BARALHO_AUXILIAR]
										.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getAjudas().remove(j));
								break;
                                                                
								/**
								 * nao pode-se percorrer um arrayList por for ja
								 * que ha reorganizacao do array,logo deve-se
								 * retirar somente um elemento por vez
								 */
							}
						}
						percorreuTudo = true;
                                                
						/**
						 * se percorreu todo o array, nao ha artefatos cinzas
						 * nele
						 */
					}
				}
			}
		}
		if (tipoArtefato == Mesa.ARTEFATOS_CODIGO) {
                        
                         //percorrendo mesas
			for (int i = 0; i < jogador.getTabuleiro()
					.getMesas().length; i++) 
			{
				if (jogador.getTabuleiro().getMesas()[i].getCodigos().size() > 0) {
					boolean percorreuTudo = false;
                                        
                                         //percorrendo vetor de artefatos de ajuda na mesa
					while (percorreuTudo == false) {
						for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getCodigos()
								.size(); j++) 
						{
							if (jogador.getTabuleiro().getMesas()[i].getCodigos().get(j).isPoorQuality() == true) {
								baralhoArtefatosRuins[BARALHO_AUXILIAR]
										.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getCodigos().remove(j));
								break;
                                                                
								/**
								 * nao pode-se percorrer um arrayList por for ja
								 * que ha reorganizacao do array,logo deve-se
								 * retirar somente um elemento por vez
								 */
							}
						}
						percorreuTudo = true;
                                                
						/**
						 * se percorreu todo o array, nao ha artefatos cinzas
						 * nele
						 */
					}
				}
			}
		}
		if (tipoArtefato == Mesa.ARTEFATOS_DESENHO) {
                    
                        //percorrendo mesas
			for (int i = 0; i < jogador.getTabuleiro()
					.getMesas().length; i++) 
			{
				if (jogador.getTabuleiro().getMesas()[i].getDesenhos().size() > 0) {
					boolean percorreuTudo = false;
                                        
                                         //percorrendo vetor de artefatos de ajuda na mesa
					while (percorreuTudo == false) {
						for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getDesenhos()
								.size(); j++)
						{
							if (jogador.getTabuleiro().getMesas()[i].getDesenhos().get(j).isPoorQuality() == true) {
								baralhoArtefatosRuins[BARALHO_AUXILIAR]
										.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getDesenhos().remove(j));
								break;
                                                                
								/**
								 * nao pode-se percorrer um arrayList por for ja
								 * que ha reorganizacao do array,logo deve-se
								 * retirar somente um elemento por vez
								 */
							}
						}
						percorreuTudo = true;
						/**
						 * se percorreu todo o array, nao ha artefatos cinzas
						 * nele
						 */
					}
				}
			}
		}
		if (tipoArtefato == Mesa.ARTEFATOS_RASTROS) {
                    
                        //percorrendo mesas
			for (int i = 0; i < jogador.getTabuleiro()
					.getMesas().length; i++)
			{
				if (jogador.getTabuleiro().getMesas()[i].getRastros().size() > 0) {
					boolean percorreuTudo = false;
					while (percorreuTudo == false) {
                                                //percorrendo vetor de artefatos de ajuda na mesa
						for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getRastros()
								.size(); j++) 
						{
							if (jogador.getTabuleiro().getMesas()[i].getRastros().get(j).isPoorQuality() == true) {
								baralhoArtefatosRuins[BARALHO_AUXILIAR]
										.recolherArtefato(jogador.getTabuleiro().getMesas()[i].getRastros().remove(j));
								break;
                                                                
								/**
								 * nao pode-se percorrer um arrayList por for ja
								 * que ha reorganizacao do array,logo deve-se
								 * retirar somente um elemento por vez
								 */
							}
						}
						percorreuTudo = true;
                                                
						/**
						 * se percorreu todo o array, nao ha artefatos cinzas
						 * nele
						 */
					}
				}
			}
		}
		if (tipoArtefato == Mesa.ARTEFATOS_REQUISITOS) {
                    
                        //percorrendo mesas
			for (int i = 0; i < jogador.getTabuleiro()
					.getMesas().length; i++) 
			{
				if (jogador.getTabuleiro().getMesas()[i].getRequisitos().size() > 0) {
					boolean percorreuTudo = false;
					while (percorreuTudo == false) {
						for (int j = 0; j < jogador.getTabuleiro().getMesas()[i].getRequisitos()
                                                        
                                                                
								.size(); j++) 
						{
							if (jogador.getTabuleiro().getMesas()[i].getRequisitos().get(j).isPoorQuality() == true) {
								baralhoArtefatosRuins[BARALHO_AUXILIAR].recolherArtefato(
										jogador.getTabuleiro().getMesas()[i].getRequisitos().remove(j));
								break;
                                                                
								/**
								 * nao pode-se percorrer um arrayList por for ja
								 * que ha reorganizacao do array,logo deve-se
								 * retirar somente um elemento por vez
								 */
							}
						}
						percorreuTudo = true;
                                                
						/**
						 * se percorreu todo o array, nao ha artefatos cinzas
						 * nele
						 */
					}
				}
			}
		}
	}

	public void adicionarEfeitosFimTurno() {
		for (int j = 0; j < getJogadores().length; j++) {
			while (getJogadores()[j].getTabuleiro().getEfeitoAumentarHabilidadeEngenheiroLater()
                                        // enquanto houver efeito de aumentar habilidade ao final da rodada
					.size() > 0) 
			{
                            
                                //percorrendo mesas do jogador com direito ao beneficio
				for (int i = 0; i < getJogadores()[j].getTabuleiro().getMesas().length; i++)
				{
					if (getJogadores()[j].getTabuleiro().getMesas()[i].getCartaMesa() == null)
						continue;

					if (getJogadores()[j].getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(getJogadores()[j].getTabuleiro().getEfeitoAumentarHabilidadeEngenheiroLater()
                                                                
                                                                        /** encontra engenheiro que recebera efeito */
									.get(0)[0])) 
					{
						getJogadores()[j].getTabuleiro().getMesas()[i]
								.setEfeitoAumentarHabilidadeEngenheiro(Integer.parseInt(getJogadores()[j].getTabuleiro()
										.getEfeitoAumentarHabilidadeEngenheiroLater().get(0)[1]));
                                                
						/** inserindo efeito */
						getJogadores()[j].getTabuleiro().getEfeitoAumentarHabilidadeEngenheiroLater().remove(0);
                                                
						/**
						 * se inseriu remove engenheiro da lista de inserir
						 * efeitos na proxima rodada
						 */
					}
                                        
					/**
					 * se nao encontrar engenheiro para inserir efeito,
					 * significa que engenheiro foi demitido, nao tendo
					 * portanto, efeito para inserir
					 */
				}

			}
			while (getJogadores()[j].getTabuleiro().getEfeitoDemitirEngenheiroLater().size() > 0) {
				for (int i = 0; i < getJogadores()[j].getTabuleiro()
                                        
                                                // percorrendo mesas do jogador com direito ao beneficio
						.getMesas().length; i++) 
				{
					if (getJogadores()[j].getTabuleiro().getMesas()[i].getCartaMesa() == null)
						continue;

					if (getJogadores()[j].getTabuleiro().getMesas()[i].getCartaMesa().getEngenheiro().getNomeEngenheiro()
							.equals(getJogadores()[j].getTabuleiro().getEfeitoDemitirEngenheiroLater()
									.get(0))) 
                                            
					{                       /* TODO ver *//** encontra engenheiro que recebera efeito  */				
                                                                despedirEngenheiro(getJogadores()[j],
								getJogadores()[j].getTabuleiro().getMesas()[i].getCartaMesa());
						getJogadores()[j].getTabuleiro().getEfeitoDemitirEngenheiroLater().remove(0);
					}
				}
			}
		}
	}

	public void diminuirDuracaoEfeitosTemporario(int jogador) {
		for (int i = 0; i < getJogadores()[jogador].getTabuleiro().getMesas().length; i++) {
			if (getJogadores()[jogador].getTabuleiro().getMesas()[i]
					.getDuracaoEfeito_TEMPORARIO_EnginnersNotCorrectArtifacts() > 0)
				getJogadores()[jogador].getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_EnginnersNotCorrectArtifacts(
								getJogadores()[jogador].getTabuleiro().getMesas()[i]
										.getDuracaoEfeito_TEMPORARIO_EnginnersNotCorrectArtifacts() - 1);

			if (getJogadores()[jogador].getTabuleiro().getMesas()[i]
					.getDuracaoEfeito_TEMPORARIO_EnginnersNotInspectArtifacts() > 0)
				getJogadores()[jogador].getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_EnginnersNotInspectArtifacts(
								getJogadores()[jogador].getTabuleiro().getMesas()[i]
										.getDuracaoEfeito_TEMPORARIO_EnginnersNotInspectArtifacts() - 1);

			if (getJogadores()[jogador].getTabuleiro().getMesas()[i]
					.getDuracaoEfeito_TEMPORARIO_EnginnersNotProduceArtifacts() > 0)
				getJogadores()[jogador].getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_EnginnersNotProduceArtifacts(
								getJogadores()[jogador].getTabuleiro().getMesas()[i]
										.getDuracaoEfeito_TEMPORARIO_EnginnersNotProduceArtifacts() - 1);

			if (getJogadores()[jogador].getTabuleiro().getMesas()[i]
					.getDuracaoEfeito_TEMPORARIO_ProduceOnlyGrayArtifacts() > 0)
				getJogadores()[jogador].getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_ProduceOnlyGrayArtifacts(
								getJogadores()[jogador].getTabuleiro().getMesas()[i]
										.getDuracaoEfeito_TEMPORARIO_ProduceOnlyGrayArtifacts() - 1);

			if (getJogadores()[jogador].getTabuleiro().getMesas()[i]
					.getDuracaoEfeito_TEMPORARIO_ProduceOnlyWhiteArtifacts() > 0)
				getJogadores()[jogador].getTabuleiro().getMesas()[i]
						.setDuracaoEfeito_TEMPORARIO_ProduceOnlyWhiteArtifacts(-1);
		}
	}

	public void mockinit() {
		String dificuldade = DIFICIL;
		//#ifdef ConceptCard
		int[] cartasConceito;
		//#endif
		int[] cartasProblema;

		//#ifdef ConceptCard
		cartasConceito = new int[1];
		cartasConceito[0] = ModeGameConstants.ALL_CARDS_CONCEITO;
		//#endif
		cartasProblema = new int[1];
		cartasProblema[0] = ModeGameConstants.ALL_CARDS_PROBLEMA;
                
                //insere o nome dos jogadores no vetor de string
		String[] nomeJogadores = { "ziraldo", "zezin", "mariazinha" }; 

		// configurarJogo(DIFICIL,nomeJogadores,cartasConceito,cartasProblema);

		this.baralhoCartas = new BaralhoCartas[2];
		this.baralhoArtefatosBons = new BaralhoArtefatosBons[2];
		this.baralhoArtefatosRuins = new BaralhoArtefatosRuins[2];

		// sortearProjeto(facilidade);
		projeto = new CartaoProjeto(dificuldade);

		// formarBaralhoCarta(facilidade,cartasConceito,cartasProblema);
		this.baralhoCartas[BARALHO_PRINCIPAL] = new BaralhoCartas(dificuldade, 
				//#ifdef ConceptCard
				cartasConceito, 
				//#endif
				cartasProblema);
		this.baralhoCartas[BARALHO_AUXILIAR] = new BaralhoCartas(baralhoCartas[BARALHO_PRINCIPAL]);

		// formarBaralhoArtefato();
		this.baralhoArtefatosBons[BARALHO_PRINCIPAL] = new BaralhoArtefatosBons();
		this.baralhoArtefatosBons[BARALHO_AUXILIAR] = new BaralhoArtefatosBons(0);
		this.baralhoArtefatosRuins[BARALHO_PRINCIPAL] = new BaralhoArtefatosRuins();
		this.baralhoArtefatosRuins[BARALHO_AUXILIAR] = new BaralhoArtefatosRuins(0);

		cadastrarJogadores(nomeJogadores);
                
		// ordenarJogadores();
		embaralharCartaseArtefatos();

		gameStatus = Status.CONTINUE;
	}

}
