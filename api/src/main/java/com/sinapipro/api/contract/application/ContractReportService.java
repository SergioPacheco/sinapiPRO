package com.sinapipro.api.contract.application;

import module java.base;

import com.sinapipro.api.contract.domain.ChangeOrder;
import com.sinapipro.api.contract.domain.ChangeOrderStatus;
import com.sinapipro.api.contract.domain.Contract;
import com.sinapipro.api.contract.domain.ContractRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ContractReportService {

    private final ContractRepository contractRepository;

    public ContractReportService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public byte[] generateContractPdf(UUID contractId) {
        var contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new DomainNotFoundException("Contract not found: " + contractId));

        List<String> lines = new ArrayList<>();
        lines.add("RELATORIO DE CONTRATO");
        lines.add("");
        lines.add("Contrato: " + contract.getNumber());
        lines.add("Descricao: " + abbreviate(contract.getDescription(), 80));
        lines.add("Fornecedor: " + contract.getSupplier().getName());
        lines.add("Status: " + contract.getStatus().name());
        lines.add("Vigencia: " + contract.getStartDate() + " a " + (contract.getEndDate() != null ? contract.getEndDate() : "Indeterminado"));
        lines.add("");
        lines.add("--- VALORES ---");
        lines.add("Valor Original: " + money(contract.getOriginalValue()));
        lines.add("Valor Atualizado: " + money(contract.getUpdatedValue()));
        lines.add("Retencao: " + percent(contract.getRetentionPct()));
        var retentionAmount = contract.getUpdatedValue().multiply(contract.getRetentionPct()).setScale(2, RoundingMode.HALF_UP);
        lines.add("Valor Retencao: " + money(retentionAmount));
        lines.add("Valor Liquido: " + money(contract.getUpdatedValue().subtract(retentionAmount)));
        lines.add("");

        var changeOrders = contract.getChangeOrders();
        if (!changeOrders.isEmpty()) {
            lines.add("--- ADITIVOS ---");
            lines.add(String.format("%-5s %-40s %12s %12s", "NUM", "DESCRICAO", "VALOR", "STATUS"));
            lines.add("-------------------------------------------------------------------------");
            for (var co : changeOrders) {
                lines.add(String.format("%-5d %-40s %12s %12s",
                        co.getNumber(), abbreviate(co.getDescription(), 40),
                        money(co.getAmount()), co.getStatus().name()));
            }
            lines.add("-------------------------------------------------------------------------");
            var approvedTotal = changeOrders.stream()
                    .filter(co -> co.getStatus() == ChangeOrderStatus.APPROVED)
                    .map(ChangeOrder::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            lines.add("Total Aditivos Aprovados: " + money(approvedTotal));
        }

        return SimplePdf.write(lines);
    }

    private String abbreviate(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }

    private String money(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String percent(BigDecimal value) {
        return value == null ? "0.00%" : value.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%";
    }

    private static final class SimplePdf {
        private SimplePdf() {}

        static byte[] write(List<String> lines) {
            StringBuilder content = new StringBuilder("BT\n/F1 9 Tf\n40 800 Td\n12 TL\n");
            lines.stream().limit(58).forEach(line -> content
                    .append("(").append(escape(line)).append(") Tj\nT*\n"));
            content.append("ET\n");

            byte[] contentBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);
            List<String> objects = List.of(
                    "<< /Type /Catalog /Pages 2 0 R >>",
                    "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                    "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                    "<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>",
                    "<< /Length " + contentBytes.length + " >>\nstream\n" + content + "endstream");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            write(out, "%PDF-1.4\n");
            List<Integer> offsets = new ArrayList<>();
            for (int i = 0; i < objects.size(); i++) {
                offsets.add(out.size());
                write(out, (i + 1) + " 0 obj\n" + objects.get(i) + "\nendobj\n");
            }
            int xref = out.size();
            write(out, "xref\n0 " + (objects.size() + 1) + "\n");
            write(out, "0000000000 65535 f \n");
            offsets.forEach(offset -> write(out, String.format("%010d 00000 n \n", offset)));
            write(out, "trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");
            return out.toByteArray();
        }

        private static String escape(String value) {
            return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
        }

        private static void write(ByteArrayOutputStream out, String value) {
            try { out.write(value.getBytes(StandardCharsets.ISO_8859_1)); }
            catch (IOException e) { throw new UncheckedIOException(e); }
        }
    }
}
