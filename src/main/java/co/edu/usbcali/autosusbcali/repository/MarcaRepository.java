package co.edu.usbcali.autosusbcali.repository;

import co.edu.usbcali.autosusbcali.domain.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    // Busca una marca por su nombre exacto
    Optional<Marca> findByNombre(String nombre);

    // Busca las marcas por estado (activas o inactivas)
    List<Marca> findByActivo(boolean activo);
}