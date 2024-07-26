package med.voll.api.domain.medico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Page<Medico> findAllByAtivoTrue(Pageable paginacao);
    //Se for seguido o padrão de nomenclatura "AtivoTrue" o JPA ja cria a query automaticamente
                        // Nesse caso é o nome do Atributo e a Condição "Ativo" e "True"
}