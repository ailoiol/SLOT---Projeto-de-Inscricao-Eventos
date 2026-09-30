package com.yasmin.inscricao_eventos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "resposta_criterio")
public class RespostaCriterio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "inscricao_id")
    private Inscricao inscricao;

    @ManyToOne
    @JoinColumn(name = "criterio_personalizado_id")
    private CriterioPersonalizado criterioPersonalizado;

    @Column(name = "valor")
    private String valor;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Inscricao getInscricao() { return inscricao; }
    public void setInscricao(Inscricao inscricao) { this.inscricao = inscricao; }
    public CriterioPersonalizado getCriterioPersonalizado() { return criterioPersonalizado; }
    public void setCriterioPersonalizado(CriterioPersonalizado criterioPersonalizado) { this.criterioPersonalizado = criterioPersonalizado; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
}