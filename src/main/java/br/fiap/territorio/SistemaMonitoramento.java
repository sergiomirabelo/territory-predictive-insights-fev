package br.fiap.territorio;

import br.fiap.territorio.dominio.Alerta;
import br.fiap.territorio.dominio.DadosTerritorio;
import br.fiap.territorio.dominio.Limiar;
import br.fiap.territorio.dominio.Medicao;
import br.fiap.territorio.dominio.Sensor;
import br.fiap.territorio.excecao.DadosInvalidosException;
import br.fiap.territorio.excecao.FonteIndisponivelException;
import br.fiap.territorio.servico.ResultadoPrevisao;
import br.fiap.territorio.servico.ServicoMonitoramento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Menu de console. O Spring chama run() depois de subir.
 * Aqui so tem entrada/saida: a logica fica no ServicoMonitoramento.
 * (Textos sem acento de proposito: o console do Windows pode embaralhar acentos.)
 */
@Component
public class SistemaMonitoramento implements CommandLineRunner {

    private final ServicoMonitoramento servico;
    private final Scanner scanner = new Scanner(System.in);

    public SistemaMonitoramento(ServicoMonitoramento servico) {
        this.servico = servico;
    }

    @Override
    public void run(String... args) {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();
            try {
                opcao = Integer.parseInt(lerLinha("Escolha: "));
                executar(opcao);
            } catch (NumberFormatException e) {
                System.out.println(">> Entrada invalida: digite apenas numeros.");
                opcao = -1;
            } catch (DadosInvalidosException e) {
                System.out.println(">> Dado invalido: " + e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.out.println(">> Fonte indisponivel: " + e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println(">> Entrada encerrada.");
                opcao = 0;
            }
        }
        System.out.println("Ate logo!");
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("===== TERRITORY PREDICTIVE INSIGHTS =====");
        System.out.println("1 - Verificar sensores (le e grava 1 medicao de cada)");
        System.out.println("2 - Gerar varias leituras (simulacao)");
        System.out.println("3 - Registrar medicao manual");
        System.out.println("4 - Rodar previsoes e alertas");
        System.out.println("5 - Ver historico de medicoes por sensor");
        System.out.println("6 - Ver alertas ja emitidos");
        System.out.println("7 - Gerenciar dados do territorio");
        System.out.println("8 - Status completo");
        System.out.println("0 - Sair");
    }

    private void executar(int opcao) throws DadosInvalidosException, FonteIndisponivelException {
        switch (opcao) {
            case 1 -> verificarSensores();
            case 2 -> gerarVariasLeituras();
            case 3 -> registrarManual();
            case 4 -> rodarPrevisoes();
            case 5 -> verHistorico();
            case 6 -> verAlertas();
            case 7 -> gerenciarDados();
            case 8 -> System.out.println(servico.statusCompleto());
            case 0 -> { /* sai do loop */ }
            default -> System.out.println(">> Opcao inexistente: " + opcao);
        }
    }

    // ------------------------------------------------------------------ opcoes

    private void verificarSensores() {
        for (Sensor sensor : servico.getSensores()) {
            try {
                Medicao m = servico.lerERegistrar(sensor);
                System.out.println("OK    " + m);
            } catch (FonteIndisponivelException e) {
                System.out.println("FALHA " + e.getMessage());
            } catch (DadosInvalidosException e) {
                System.out.println("INVALIDO " + e.getMessage());
            }
        }
    }

    private void gerarVariasLeituras() throws DadosInvalidosException {
        int quantidade = Integer.parseInt(lerLinha("Quantas rodadas de leitura (1 a 50)? "));
        if (quantidade < 1 || quantidade > 50) {
            throw new DadosInvalidosException("Quantidade deve estar entre 1 e 50: " + quantidade);
        }
        int gravadas = 0;
        int falhas = 0;
        for (int i = 0; i < quantidade; i++) {
            for (Sensor sensor : servico.getSensores()) {
                try {
                    servico.lerERegistrar(sensor);
                    gravadas++;
                } catch (FonteIndisponivelException | DadosInvalidosException e) {
                    falhas++;
                }
            }
        }
        System.out.println("Medicoes gravadas: " + gravadas + " | falhas: " + falhas);
    }

    private void registrarManual() throws DadosInvalidosException {
        Sensor sensor = escolherSensor();
        double valor = Double.parseDouble(lerLinha("Valor (" + sensor.getUnidade() + "): ").replace(',', '.'));
        Medicao m = servico.registrar(sensor, valor); // pode lancar DadosInvalidosException
        System.out.println("Gravado: " + m);
    }

    private void rodarPrevisoes() {
        for (Sensor sensor : servico.getSensores()) {
            System.out.println("--- " + sensor.getId() + " (" + sensor.getTipo() + ") ---");
            try {
                List<ResultadoPrevisao> resultados = servico.preverSensor(sensor);
                for (ResultadoPrevisao r : resultados) {
                    String nivel = r.nivel() == null ? "SEM ALERTA" : r.nivel().name();
                    System.out.printf("[%s] previsto: %.1f %s -> %s%n", r.estrategia(), r.valorPrevisto(), r.unidade(), nivel);
                    System.out.println("   " + r.explicacao());
                }
            } catch (DadosInvalidosException e) {
                // O sistema se cala quando nao tem base para prever (ver README)
                System.out.println("Sem previsao: " + e.getMessage());
            }
        }
    }

    private void verHistorico() throws DadosInvalidosException {
        Sensor sensor = escolherSensor();
        List<Medicao> medicoes = servico.ultimasMedicoes(sensor.getId(), 20);
        if (medicoes.isEmpty()) {
            System.out.println("Nenhuma medicao gravada para " + sensor.getId() + " ainda.");
            return;
        }
        System.out.println("Ultimas " + medicoes.size() + " medicoes de " + sensor.getId() + ":");
        medicoes.forEach(m -> System.out.println("  " + m));
    }

    private void verAlertas() {
        List<Alerta> alertas = servico.alertasEmitidos();
        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta emitido ainda.");
            return;
        }
        System.out.println("Alertas emitidos (mais recentes primeiro): " + alertas.size());
        alertas.forEach(a -> System.out.println("  " + a));
    }

    // ------------------------------------------------------------ dados territorio

    private void gerenciarDados() throws DadosInvalidosException {
        DadosTerritorio dados = servico.getConfiguracao().getDados();
        System.out.println("--- Dados do territorio ---");
        System.out.println("1 - Ver dados");
        System.out.println("2 - Alterar limiares de um tipo de sensor");
        System.out.println("3 - Alterar populacao");
        System.out.println("4 - Simular falha / conserto de um sensor");
        int opcao = Integer.parseInt(lerLinha("Escolha: "));
        switch (opcao) {
            case 1 -> {
                System.out.println(dados.getNome() + " | " + dados.getRegiao() + " | populacao " + dados.getPopulacao());
                for (Map.Entry<String, Limiar> e : dados.getLimiares().entrySet()) {
                    Limiar l = e.getValue();
                    System.out.println("  " + e.getKey() + ": " + l.atencao() + " / " + l.alerta() + " / " + l.critico());
                }
            }
            case 2 -> {
                String tipo = lerLinha("Tipo (CHUVA, NIVEL_RIO ou UMIDADE_SOLO): ").trim().toUpperCase();
                double a = lerNumero("Novo limiar de ATENCAO: ");
                double b = lerNumero("Novo limiar de ALERTA: ");
                double c = lerNumero("Novo limiar CRITICO: ");
                dados.definirLimiares(tipo, a, b, c); // valida e pode lancar DadosInvalidosException
                System.out.println("Limiares atualizados.");
            }
            case 3 -> {
                long pop = Long.parseLong(lerLinha("Nova populacao: ").trim());
                dados.setPopulacao(pop);
                System.out.println("Populacao atualizada.");
            }
            case 4 -> {
                Sensor sensor = escolherSensor();
                sensor.definirFuncionamento(!sensor.estaFuncionando());
                System.out.println(sensor.getId() + (sensor.estaFuncionando() ? " voltou a funcionar." : " agora esta com FALHA."));
            }
            default -> System.out.println(">> Opcao inexistente: " + opcao);
        }
    }

    // ---------------------------------------------------------------- auxiliares

    private Sensor escolherSensor() throws DadosInvalidosException {
        List<Sensor> sensores = servico.getSensores();
        for (int i = 0; i < sensores.size(); i++) {
            System.out.println((i + 1) + " - " + sensores.get(i).getId() + " (" + sensores.get(i).getTipo() + ")");
        }
        int indice = Integer.parseInt(lerLinha("Sensor: ")) - 1;
        if (indice < 0 || indice >= sensores.size()) {
            throw new DadosInvalidosException("Sensor inexistente.");
        }
        return sensores.get(indice);
    }

    private double lerNumero(String texto) {
        return Double.parseDouble(lerLinha(texto).replace(',', '.'));
    }

    private String lerLinha(String texto) {
        System.out.print(texto);
        return scanner.nextLine().trim();
    }
}
