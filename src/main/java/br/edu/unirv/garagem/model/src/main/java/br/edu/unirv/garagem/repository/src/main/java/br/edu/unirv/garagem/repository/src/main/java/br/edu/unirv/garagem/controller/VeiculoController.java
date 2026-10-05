package br.edu.unirv.garagem.controller;

import br.edu.unirv.garagem.model.Veiculo;
import br.edu.unirv.garagem.repository.IVeiculoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    private final IVeiculoRepository repository;

    // Injeção de dependência pelo construtor
    public VeiculoController(IVeiculoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("veiculos", repository.obterTodos());
        return "veiculo/index";
    }

    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("veiculo", new Veiculo());
        return "veiculo/VeiculoForm";
    }

    @PostMapping("/novo")
    public String cadastrar(@Valid @ModelAttribute Veiculo veiculo, BindingResult result) {
        // Valida se a placa já existe
        if (repository.obterTodos().stream().anyMatch(v -> v.getPlaca().equalsIgnoreCase(veiculo.getPlaca()))) {
            result.rejectValue("placa", "error.veiculo", "Esta placa já está registada.");
        }
        
        if (result.hasErrors()) {
            return "veiculo/VeiculoForm";
        }
        
        repository.adicionar(veiculo);
        return "redirect:/veiculos";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicao(@PathVariable int id, Model model) {
        Optional<Veiculo> veiculo = repository.obterPorId(id);
        if (veiculo.isEmpty()) {
            return "redirect:/veiculos";
        }
        model.addAttribute("veiculo", veiculo.get());
        return "veiculo/VeiculoForm";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable int id, @Valid @ModelAttribute Veiculo veiculo, BindingResult result) {
        // Valida se a placa já existe noutro veículo (ignora o próprio id)
        if (repository.obterTodos().stream().anyMatch(v -> v.getPlaca().equalsIgnoreCase(veiculo.getPlaca()) && !v.getId().equals(id))) {
            result.rejectValue("placa", "error.veiculo", "Esta placa já está registada noutro veículo.");
        }

        if (result.hasErrors()) {
            veiculo.setId(id);
            return "veiculo/VeiculoForm";
        }

        veiculo.setId(id);
        repository.atualizar(veiculo);
        return "redirect:/veiculos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable int id) {
        repository.remover(id);
        return "redirect:/veiculos";
    }
}
