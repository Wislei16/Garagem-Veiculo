package br.edu.unirv.garagem.repository;

import br.edu.unirv.garagem.model.Pessoa;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PessoaRepository implements IPessoaRepository {
    private final String FILE_PATH = "data/pessoas.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PessoaRepository() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            salvarArquivo(new ArrayList<>());
        }
    }

    private List<Pessoa> lerArquivo() {
        try {
            return objectMapper.readValue(new File(FILE_PATH), new TypeReference<List<Pessoa>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void salvarArquivo(List<Pessoa> pessoas) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), pessoas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Pessoa> obterTodas() {
        return lerArquivo();
    }

    @Override
    public Optional<Pessoa> obterPorId(int id) {
        return lerArquivo().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @Override
    public void adicionar(Pessoa pessoa) {
        List<Pessoa> pessoas = lerArquivo();
        // O Id é gerado pelo repositório (maior Id + 1) conforme exigido
        int novoId = pessoas.stream().mapToInt(Pessoa::getId).max().orElse(0) + 1;
        pessoa.setId(novoId);
        pessoas.add(pessoa);
        salvarArquivo(pessoas);
    }

    @Override
    public void atualizar(Pessoa pessoa) {
        List<Pessoa> pessoas = lerArquivo();
        for (int i = 0; i < pessoas.size(); i++) {
            if (pessoas.get(i).getId().equals(pessoa.getId())) {
                pessoas.set(i, pessoa);
                salvarArquivo(pessoas);
                return;
            }
        }
    }

    @Override
    public void remover(int id) {
        List<Pessoa> pessoas = lerArquivo();
        pessoas.removeIf(p -> p.getId().equals(id));
        salvarArquivo(pessoas);
    }
}
