package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Veiculo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VeiculoRepository implements IVeiculoRepository {
    private final String FILE_PATH = "data/veiculos.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VeiculoRepository() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            salvarArquivo(new ArrayList<>());
        }
    }

    private List<Veiculo> lerArquivo() {
        try {
            return objectMapper.readValue(new File(FILE_PATH), new TypeReference<List<Veiculo>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void salvarArquivo(List<Veiculo> veiculos) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), veiculos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Veiculo> obterTodos() {
        return lerArquivo();
    }

    @Override
    public Optional<Veiculo> obterPorId(int id) {
        return lerArquivo().stream().filter(v -> v.getId().equals(id)).findFirst();
    }

    @Override
    public void adicionar(Veiculo veiculo) {
        List<Veiculo> veiculos = lerArquivo();
        int novoId = veiculos.stream().mapToInt(Veiculo::getId).max().orElse(0) + 1;
        veiculo.setId(novoId);
        veiculos.add(veiculo);
        salvarArquivo(veiculos);
    }

    @Override
    public void atualizar(Veiculo veiculo) {
        List<Veiculo> veiculos = lerArquivo();
        for (int i = 0; i < veiculos.size(); i++) {
            if (veiculos.get(i).getId().equals(veiculo.getId())) {
                veiculos.set(i, veiculo);
                salvarArquivo(veiculos);
                return;
            }
        }
    }

    @Override
    public void remover(int id) {
        List<Veiculo> veiculos = lerArquivo();
        veiculos.removeIf(v -> v.getId().equals(id));
        salvarArquivo(veiculos);
    }
}
