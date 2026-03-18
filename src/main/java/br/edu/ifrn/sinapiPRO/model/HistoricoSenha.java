package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "historico_senha")
public class HistoricoSenha implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;

	@ManyToOne
	@JoinColumn(name = "codigo_usuario", nullable = false)
	private Usuario usuario;

	@Column(name = "senha_hash", nullable = false)
	private String senhaHash;

	@Column(name = "data_criacao", nullable = false)
	private LocalDateTime dataCriacao;

	public Long getCodigo() { return codigo; }
	public void setCodigo(Long codigo) { this.codigo = codigo; }
	public Usuario getUsuario() { return usuario; }
	public void setUsuario(Usuario usuario) { this.usuario = usuario; }
	public String getSenhaHash() { return senhaHash; }
	public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
	public LocalDateTime getDataCriacao() { return dataCriacao; }
	public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
