package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired   // <-- Injeção de dependência
    private MedicoRepository repository;

    @PostMapping
    @Transactional  // <-- Essa notação é para dizer que haverá transação de dados com o banco de dados (Casos de put ou post)
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder) {  // <-- @Valid é necessário para verificar as validações feitas nos DTO'S
        var medico = new Medico(dados);
        repository.save(medico);

        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri(); // <- Aqui será gerada a URI com o id do médico criado

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }

    @GetMapping                        // Usa-se @PageableDefault para alterar o padrão de paginação
    public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {   // O retorno do tipo Page devolve a Lista mas também informações sobre a paginação
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new); // Aqui é convertido uma lista de Medicos para uma lista de DadosListagemMedico
        //OBS: Para mudar a qtd de elementos por página pode-se mudar direto na url utilizando $size=1 // Para ordenação usa-se o filtro ?sort=nomeDoAtributo
                                                          // O map() devolve uma Page
        return ResponseEntity.ok(page);
    }
    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados){
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    @DeleteMapping("/{id}")                   // OBS: Exceptions não tratadas, o spring interpreta como erro 500.
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id){
        var medico = repository.getReferenceById(id);
        medico.excluir();

        return ResponseEntity.noContent().build(); // <-- Usa-se o build() para construir um objeto do tipo ResponseEntity
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id){
        var medico = repository.getReferenceById(id);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico)); // <-- Usa-se o build() para construir um objeto do tipo ResponseEntity
    }
}
