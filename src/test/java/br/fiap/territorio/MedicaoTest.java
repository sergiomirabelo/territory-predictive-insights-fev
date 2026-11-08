package br.fiap.territorio;

import br.fiap.territorio.dominio.Medicao;
import br.fiap.territorio.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedicaoTest {

    @Test
    void deveCriarMedicaoValida() throws DadosInvalidosException {
        // Arrange + Act
        Medicao medicao = new Medicao("PLUV-01", "CHUVA", 12.5, "mm/h");

        // Assert
        assertEquals("PLUV-01", medicao.getSensorId());
        assertEquals(12.5, medicao.getValor(), 0.0001);
    }

    @Test
    void deveLancarExcecaoQuandoMedicaoNegativa() {
        // Arrange (valor invalido) + Act + Assert
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("PLUV-01", "CHUVA", -3.0, "mm/h"));

        assertTrue(erro.getMessage().contains("negativa"));
    }

    @Test
    void deveLancarExcecaoQuandoSensorVazio() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao(" ", "CHUVA", 5.0, "mm/h"));
    }
}
