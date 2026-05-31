package com.sinapipro.api.finance.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser CNAB 240 (FEBRABAN). Processa arquivo retorno bancário.
 * Layout: 240 posições por linha.
 * Tipos de registro: 0=Header Arquivo, 1=Header Lote, 3=Detalhe, 5=Trailer Lote, 9=Trailer Arquivo
 */
public class Cnab240Parser {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("ddMMyyyy");

    public Cnab240Result parse(String fileContent) {
        var lines = fileContent.split("\\r?\\n");
        var records = new ArrayList<Cnab240Record>();
        String bankCode = "";
        String companyName = "";

        for (var line : lines) {
            if (line.length() < 240) continue;
            char tipo = line.charAt(7);
            switch (tipo) {
                case '0' -> { // Header Arquivo
                    bankCode = line.substring(0, 3);
                    companyName = line.substring(72, 102).trim();
                }
                case '3' -> { // Detalhe
                    char segmento = line.charAt(13);
                    if (segmento == 'T') {
                        var record = parseSegmentoT(line);
                        records.add(record);
                    } else if (segmento == 'U' && !records.isEmpty()) {
                        enrichWithSegmentoU(records.getLast(), line);
                    }
                }
                // 1, 5, 9 = headers/trailers de lote — ignorados no parsing
            }
        }
        return new Cnab240Result(bankCode, companyName, records);
    }

    private Cnab240Record parseSegmentoT(String line) {
        return new Cnab240Record(
            line.substring(58, 73).trim(),                          // nossoNumero (pos 59-73)
            line.substring(105, 120).trim(),                        // seuNumero (pos 106-120)
            parseDate(line.substring(73, 81)),                      // vencimento (pos 74-81)
            parseAmount(line.substring(81, 96)),                    // valorTitulo (pos 82-96)
            line.substring(15, 17).trim(),                          // ocorrencia (pos 16-17)
            null, null                                               // preenchidos pelo segmento U
        );
    }

    private void enrichWithSegmentoU(Cnab240Record record, String line) {
        record.valorPago = parseAmount(line.substring(77, 92));     // valorPago (pos 78-92)
        record.dataPagamento = parseDate(line.substring(137, 145)); // dataPagamento (pos 138-145)
    }

    private BigDecimal parseAmount(String raw) {
        try { return new BigDecimal(raw.trim()).movePointLeft(2); }
        catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }

    private LocalDate parseDate(String raw) {
        try { return LocalDate.parse(raw.trim(), DATE_FMT); }
        catch (Exception e) { return null; }
    }

    // --- Result types ---

    public record Cnab240Result(String bankCode, String companyName, List<Cnab240Record> records) {
        public int totalRecords() { return records.size(); }
        public BigDecimal totalPaid() { return records.stream().map(r -> r.valorPago != null ? r.valorPago : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add); }
    }

    public static class Cnab240Record {
        public final String nossoNumero;
        public final String seuNumero;
        public final LocalDate vencimento;
        public final BigDecimal valorTitulo;
        public final String ocorrencia; // 02=Entrada, 06=Liquidação, 09=Baixa
        public BigDecimal valorPago;
        public LocalDate dataPagamento;

        public Cnab240Record(String nossoNumero, String seuNumero, LocalDate vencimento,
                             BigDecimal valorTitulo, String ocorrencia, BigDecimal valorPago, LocalDate dataPagamento) {
            this.nossoNumero = nossoNumero; this.seuNumero = seuNumero; this.vencimento = vencimento;
            this.valorTitulo = valorTitulo; this.ocorrencia = ocorrencia; this.valorPago = valorPago; this.dataPagamento = dataPagamento;
        }

        public boolean isLiquidacao() { return "06".equals(ocorrencia); }
        public boolean isBaixa() { return "09".equals(ocorrencia); }
    }
}
