package br.fiap.territorio.persistencia;

import br.fiap.territorio.dominio.Medicao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository: o Spring Data cria a implementacao sozinho.
 * O NOME do metodo vira SQL:
 *  findBySensorIdOrderByIdDesc = "WHERE sensor_id = ? ORDER BY id DESC".
 * O Pageable limita quantas linhas voltam (as N mais recentes).
 */
public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    List<Medicao> findBySensorIdOrderByIdDesc(String sensorId, Pageable limite);
}
