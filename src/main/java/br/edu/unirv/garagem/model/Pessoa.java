package br.edu.unirv.garagem.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class Pessoa {
    private Integer id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    private String telefone;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
