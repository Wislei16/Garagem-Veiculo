package br.edu.unirv.garagem.controller;

import br.edu.unirv.garagem.model.Pessoa;
import br.edu.unirv.garagem.repository.IPessoaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/pessoas")
public class PessoaController {

    private final IPessoaRepository repository;

    // Injeção de dependência pelo construtor exigida pela arquitetura
    public PessoaController(IPessoaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pessoas", repository.obterTodas());
        return "pessoa/index";
    }

    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("pessoa", new Pessoa());
        return "pessoa/PessoaForm";
    }

    @PostMapping("/novo")
    public String cadastrar(@Valid @ModelAttribute Pessoa pessoa, BindingResult result) {
        if (repository.obterTodas().stream().anyMatch(p -> p.getCpf().equals(pessoa.getCpf()))) {
            result.rejectValue("cpf", "error.pessoa", "Este CPF já está cadastrado.");
        }
        
        if (result.hasErrors()) {
            return "pessoa/PessoaForm";
        }
        
        repository.adicionar(pessoa);
        return "redirect:/pessoas";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicao(@PathVariable int id, Model model) {
        Optional<Pessoa> pessoa = repository.obterPorId(id);
        if (pessoa.isEmpty()) {
            return "redirect:/pessoas";
        }
        model.addAttribute("pessoa", pessoa.get());
        return "pessoa/PessoaForm";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable int id, @Valid @ModelAttribute Pessoa pessoa, BindingResult result) {
        if (repository.obterTodas().stream().anyMatch(p -> p.getCpf().equals(pessoa.getCpf()) && !p.getId().equals(id))) {
            result.rejectValue("cpf", "error.pessoa", "Este CPF já está cadastrado em outro registro.");
        }

        if (result.hasErrors()) {
            pessoa.setId(id);
            return "pessoa/PessoaForm";
        }

        pessoa.setId(id);
        repository.atualizar(pessoa);
        return "redirect:/pessoas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable int id) {
        repository.remover(id);
        return "redirect:/pessoas";
    }
}
