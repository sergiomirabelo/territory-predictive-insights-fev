package br.fiap.territorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada. O Spring sobe, conecta no banco e depois chama
 * o menu (SistemaMonitoramento), que e um CommandLineRunner.
 */
@SpringBootApplication
public class TerritorioApplication {

    public static void main(String[] args) {
        SpringApplication.run(TerritorioApplication.class, args);
    }
}
