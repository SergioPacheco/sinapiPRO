package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Contrato;
import br.edu.ifrn.sinapiPRO.model.ParcelaVenda;
import br.edu.ifrn.sinapiPRO.model.UnidadeVenda;
import br.edu.ifrn.sinapiPRO.model.Venda;
import br.edu.ifrn.sinapiPRO.repository.ContratosRepository;
import br.edu.ifrn.sinapiPRO.repository.MedicoesRepository;
import br.edu.ifrn.sinapiPRO.repository.VendasRepository;

/**
 * Validações de negócio centralizadas.
 *
 * Regras implementadas:
 * 1. Unidade já vendida — não pode ser vendida novamente
 * 2. Parcela duplicada — mesmo número na mesma venda
 * 3. Contrato encerrado — não pode receber nova medição
 * 4. Medição duplicada — mesmo número no mesmo contrato
 * 5. Venda distratada — não pode gerar novas parcelas
 */
@Service
public class ValidacaoNegocioService {

    private final VendasRepository vendaRepository;
    private final ContratosRepository contratoRepository;
    private final MedicoesRepository medicaoRepository;

    public ValidacaoNegocioService(
            VendasRepository vendaRepository,
            ContratosRepository contratoRepository,
            MedicoesRepository medicaoRepository) {
        this.vendaRepository = vendaRepository;
        this.contratoRepository = contratoRepository;
        this.medicaoRepository = medicaoRepository;
    }

    /**
     * Valida se uma unidade pode ser vendida.
     * Regra: unidade com situação diferente de DISPONIVEL não pode ser vendida.
     *
     * @throws RuntimeException se a unidade já estiver vendida/reservada
     */
    @Transactional(readOnly = true)
    public void validarUnidadeDisponivel(UnidadeVenda unidade) {
        // Verifica se já existe venda ATIVA para esta unidade
        boolean jaVendida = vendaRepository.findAll().stream()
                .anyMatch(v -> v.getUnidade().getCodigo().equals(unidade.getCodigo())
                        && "ATIVA".equals(v.getSituacao()));

        if (jaVendida) {
            throw new RuntimeException(String.format(
                    "Unidade '%s' já está vendida. Não é possível registrar nova venda.",
                    unidade.getIdentificacao()));
        }

        // Verifica situação da unidade
        if (unidade.getSituacao() != null) {
            String nomeSituacao = unidade.getSituacao().getNome();
            if ("VENDIDA".equalsIgnoreCase(nomeSituacao)
                    || "RESERVADA".equalsIgnoreCase(nomeSituacao)
                    || "BLOQUEADA".equalsIgnoreCase(nomeSituacao)) {
                throw new RuntimeException(String.format(
                        "Unidade '%s' está com situação '%s'. Não pode ser vendida.",
                        unidade.getIdentificacao(), nomeSituacao));
            }
        }
    }

    /**
     * Valida se as parcelas de uma venda não têm números duplicados.
     *
     * @throws RuntimeException se houver parcelas com número duplicado
     */
    public void validarParcelasSemDuplicatas(List<ParcelaVenda> parcelas) {
        long numerosDistintos = parcelas.stream()
                .filter(p -> p.getNumero() != null)
                .map(ParcelaVenda::getNumero)
                .distinct()
                .count();

        long totalComNumero = parcelas.stream()
                .filter(p -> p.getNumero() != null)
                .count();

        if (numerosDistintos < totalComNumero) {
            throw new RuntimeException(
                    "Existem parcelas com números duplicados. Cada parcela deve ter um número único.");
        }
    }

    /**
     * Valida se o valor total das parcelas corresponde ao valor da venda.
     * Tolerância: R$ 0,02 (diferença de centavos por arredondamento).
     *
     * @throws RuntimeException se a diferença for maior que a tolerância
     */
    public void validarTotalParcelas(Venda venda) {
        if (venda.getParcelas().isEmpty()) return;

        BigDecimal totalParcelas = venda.getParcelas().stream()
                .map(ParcelaVenda::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal diferenca = venda.getValorVenda().subtract(totalParcelas).abs();
        BigDecimal tolerancia = new BigDecimal("0.02");

        if (diferenca.compareTo(tolerancia) > 0) {
            throw new RuntimeException(String.format(
                    "Soma das parcelas (R$ %.2f) difere do valor da venda (R$ %.2f). Diferença: R$ %.2f",
                    totalParcelas, venda.getValorVenda(), diferenca));
        }
    }

    /**
     * Valida se um contrato pode receber nova medição.
     * Regras:
     * - Contrato ENCERRADO não aceita medições
     * - Não pode haver medição ABERTA para o mesmo contrato
     *
     * @throws RuntimeException se o contrato não puder receber medição
     */
    @Transactional(readOnly = true)
    public void validarContratoParaMedicao(Long codigoContrato) {
        Contrato contrato = contratoRepository.findById(codigoContrato)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        if ("ENCERRADO".equals(contrato.getSituacao())) {
            throw new RuntimeException(String.format(
                    "Contrato '%s' está ENCERRADO. Não é possível registrar nova medição.",
                    contrato.getDescricao()));
        }

        if ("SUSPENSO".equals(contrato.getSituacao())) {
            throw new RuntimeException(String.format(
                    "Contrato '%s' está SUSPENSO. Aguarde a reativação para registrar medições.",
                    contrato.getDescricao()));
        }

        // Verifica se já existe medição ABERTA
        boolean temMedicaoAberta = medicaoRepository
                .findByContratoCodigoOrderByNumeroAsc(codigoContrato)
                .stream()
                .anyMatch(m -> "ABERTA".equals(m.getSituacao()));

        if (temMedicaoAberta) {
            throw new RuntimeException(
                    "Já existe uma medição ABERTA para este contrato. "
                    + "Aprove ou cancele a medição existente antes de criar uma nova.");
        }
    }

    /**
     * Valida se o número da medição não está duplicado no contrato.
     *
     * @throws RuntimeException se o número já existir
     */
    @Transactional(readOnly = true)
    public void validarNumeromedicaoUnico(Long codigoContrato, Integer numero, Long codigoMedicaoAtual) {
        if (numero == null) return;

        boolean duplicado = medicaoRepository
                .findByContratoCodigoOrderByNumeroAsc(codigoContrato)
                .stream()
                .anyMatch(m -> numero.equals(m.getNumero())
                        && !m.getCodigo().equals(codigoMedicaoAtual));

        if (duplicado) {
            throw new RuntimeException(String.format(
                    "Já existe uma medição com o número %d neste contrato.", numero));
        }
    }

    /**
     * Valida se uma venda pode ter parcelas geradas.
     * Regra: venda DISTRATADA não pode gerar parcelas.
     *
     * @throws RuntimeException se a venda não puder ter parcelas
     */
    public void validarVendaParaParcelas(Venda venda) {
        if ("DISTRATADA".equals(venda.getSituacao())) {
            throw new RuntimeException(
                    "Venda DISTRATADA não pode ter parcelas geradas.");
        }
        if ("QUITADA".equals(venda.getSituacao())) {
            throw new RuntimeException(
                    "Venda já QUITADA. Não é necessário gerar novas parcelas.");
        }
    }
}
