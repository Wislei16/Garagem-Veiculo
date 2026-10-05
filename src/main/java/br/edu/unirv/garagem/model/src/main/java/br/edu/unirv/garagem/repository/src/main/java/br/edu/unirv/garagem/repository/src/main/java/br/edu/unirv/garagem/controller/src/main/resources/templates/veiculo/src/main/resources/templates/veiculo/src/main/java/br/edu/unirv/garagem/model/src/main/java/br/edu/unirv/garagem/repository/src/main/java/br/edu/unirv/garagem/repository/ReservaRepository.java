package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Reserva;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservaRepository implements IReservaRepository {
    private final String FILE_PATH = "data/reservas.json";
    private final ObjectMapper objectMapper;

    public ReservaRepository() {
        objectMapper = new ObjectMapper();
        // Regista o módulo para suportar o LocalDate em Java
        objectMapper.registerModule(new JavaTimeModule());

        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            salvarArquivo(new ArrayList<>());
        }
    }

    private List<Reserva> lerArquivo() {
        try {
            return objectMapper.readValue(new File(FILE_PATH), new TypeReference<List<Reserva>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void salvarArquivo(List<Reserva> reservas) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), reservas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Reserva> obterTodas() {
        return lerArquivo();
    }

    @Override
    public Optional<Reserva> obterPorId(int id) {
        return lerArquivo().stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public void adicionar(Reserva reserva) {
        List<Reserva> reservas = lerArquivo();
        int novoId = reservas.stream().mapToInt(Reserva::getId).max().orElse(0) + 1;
        reserva.setId(novoId);
        reservas.add(reserva);
        salvarArquivo(reservas);
    }

    @Override
    public void atualizar(Reserva reserva) {
        List<Reserva> reservas = lerArquivo();
        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i).getId().equals(reserva.getId())) {
                reservas.set(i, reserva);
                salvarArquivo(reservas);
                return;
            }
        }
    }

    @Override
    public void remover(int id) {
        List<Reserva> reservas = lerArquivo();
        reservas.removeIf(r -> r.getId().equals(id));
        salvarArquivo(reservas);
    }

    @Override
    public boolean existeConflito(int veiculoId, LocalDate inicio, LocalDate fim, Integer idIgnorado) {
        List<Reserva> reservas = lerArquivo();
        return reservas.stream().anyMatch(r ->
                r.getVeiculoId().equals(veiculoId) &&
                (idIgnorado == null || !r.getId().equals(idIgnorado)) &&
                (!inicio.isAfter(r.getDataFim())) &&
                (!fim.isBefore(r.getDataInicio()))
        );
    }
}
