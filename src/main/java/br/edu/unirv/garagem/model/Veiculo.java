package br.edu.unirv.garagem.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Veiculo {
    private Integer id;

    @NotBlank(message = "A placa é obrigatória")
    private String placa;

    @NotBlank(message = "A marca é obrigatória")
    private String marca;

    @NotBlank(message = "O modelo é obrigatório")
    private String modelo;

    @NotNull(message = "O ano é obrigatório")
    private Integer ano;

    private String cor; // Opcional

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }
    
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
}
