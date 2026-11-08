package br.fiap.territorio.persistencia;

import br.fiap.territorio.dominio.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    /** Todos os alertas, do mais recente para o mais antigo. */
    List<Alerta> findAllByOrderByIdDesc();
}
