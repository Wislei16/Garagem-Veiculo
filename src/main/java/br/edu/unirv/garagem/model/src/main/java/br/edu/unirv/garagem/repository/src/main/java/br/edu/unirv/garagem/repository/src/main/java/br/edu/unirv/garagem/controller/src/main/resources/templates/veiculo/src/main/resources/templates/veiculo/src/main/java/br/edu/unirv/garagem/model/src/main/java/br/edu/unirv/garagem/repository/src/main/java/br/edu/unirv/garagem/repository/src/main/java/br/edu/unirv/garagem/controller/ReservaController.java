package br.edu.unirv.garagem.controller;

import br.edu.unirv.garagem.model.Reserva;
import br.edu.unirv.garagem.repository.IPessoaRepository;
import br.edu.unirv.garagem.repository.IReservaRepository;
import br.edu.unirv.garagem.repository.IVeiculoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ReservaController {

    private final IReservaRepository reservaRepository;
    private final IVeiculoRepository veiculoRepository;
    private final IPessoaRepository pessoaRepository;

    // Injeção de dependência pelo construtor das três interfaces
    public ReservaController(IReservaRepository reservaRepository, 
                             IVeiculoRepository veiculoRepository, 
                             IPessoaRepository pessoaRepository) {
        this.reservaRepository = reservaRepository;
        this.veiculoRepository = veiculoRepository;
        this.pessoaRepository = pessoaRepository;
    }

    // Rota padrão exigida para a página inicial
    @GetMapping({"/", "/reservas"})
    public String listar(Model model) {
        model.addAttribute("reservas", reservaRepository.obterTodas());
        // Enviamos veículos e pessoas para cruzar os dados na View (exibir nomes em vez de IDs)
        model.addAttribute("veiculos", veiculoRepository.obterTodos());
        model.addAttribute("pessoas", pessoaRepository.obterTodas());
        return "reserva/index";
    }

    @GetMapping("/reservas/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("veiculos", veiculoRepository.obterTodos());
        model.addAttribute("pessoas", pessoaRepository.obterTodas());
        return "reserva/ReservaForm";
    }

    @PostMapping("/reservas/novo")
    public String cadastrar(@Valid @ModelAttribute("reserva") Reserva reserva, BindingResult result, Model model) {
        validarRegras(reserva, result, null);

        if (result.hasErrors()) {
            model.addAttribute("veiculos", veiculoRepository.obterTodos());
            model.addAttribute("pessoas", pessoaRepository.obterTodas());
            return "reserva/ReservaForm";
        }

        reservaRepository.adicionar(reserva);
        return "redirect:/reservas";
    }

    @GetMapping("/reservas/{id}/editar")
    public String mostrarFormularioEdicao(@PathVariable int id, Model model) {
        Optional<Reserva> reserva = reservaRepository.obterPorId(id);
        if (reserva.isEmpty()) {
            return "redirect:/reservas";
        }
        model.addAttribute("reserva", reserva.get());
        model.addAttribute("veiculos", veiculoRepository.obterTodos());
        model.addAttribute("pessoas", pessoaRepository.obterTodas());
        return "reserva/ReservaForm";
    }

    @PostMapping("/reservas/{id}/editar")
    public String editar(@PathVariable int id, @Valid @ModelAttribute("reserva") Reserva reserva, BindingResult result, Model model) {
        validarRegras(reserva, result, id);

        if (result.hasErrors()) {
            reserva.setId(id);
            model.addAttribute("veiculos", veiculoRepository.obterTodos());
            model.addAttribute("pessoas", pessoaRepository.obterTodas());
            return "reserva/ReservaForm";
        }

        reserva.setId(id);
        reservaRepository.atualizar(reserva);
        return "redirect:/reservas";
    }

    @PostMapping("/reservas/{id}/excluir")
    public String excluir(@PathVariable int id) {
        reservaRepository.remover(id);
        return "redirect:/reservas";
    }

    // Método privado que processa as regras de negócio antes de gravar
    private void validarRegras(Reserva reserva, BindingResult result, Integer idIgnorado) {
        if (reserva.getDataInicio() != null && reserva.getDataFim() != null) {
            if (reserva.getDataFim().isBefore(reserva.getDataInicio())) {
                result.rejectValue("dataFim", "error.reserva", "A data de fim não pode ser anterior à data de início.");
            } else if (reservaRepository.existeConflito(reserva.getVeiculoId(), reserva.getDataInicio(), reserva.getDataFim(), idIgnorado)) {
                // Se houver conflito, bloqueia a reserva e mostra erro na tela
                result.rejectValue("dataInicio", "error.reserva", "O veículo já se encontra reservado para este período.");
            }
        }
    }
}
