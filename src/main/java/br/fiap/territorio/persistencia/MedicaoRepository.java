package br.fiap.territorio.persistencia;

import br.fiap.territorio.dominio.Medicao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    List<Medicao> findBySensorIdOrderByIdDesc(String sensorId, Pageable limite);
}
