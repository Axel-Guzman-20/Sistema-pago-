package proyecto.SistemaPago.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.SistemaPago.entidades.Banco;

@Repository
public interface BancoRepositorio extends JpaRepository<Banco, Integer> {

}
