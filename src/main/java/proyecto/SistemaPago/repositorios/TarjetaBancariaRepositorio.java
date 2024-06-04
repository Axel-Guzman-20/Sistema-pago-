package proyecto.SistemaPago.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.SistemaPago.entidades.TarjetaBancaria;
@Repository
public interface TarjetaBancariaRepositorio extends JpaRepository<TarjetaBancaria, String> {
}
