package com.sinapipro.api.procurement.application;

import module java.base;

import com.sinapipro.api.procurement.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProcurementReportService {

    private final QuotationRepository quotationRepository;
    private final PurchaseOrderRepository orderRepository;

    public ProcurementReportService(QuotationRepository quotationRepository, PurchaseOrderRepository orderRepository) {
        this.quotationRepository = quotationRepository;
        this.orderRepository = orderRepository;
    }

    public byte[] generateComparativeMapPdf(UUID quotationId) {
        var quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new DomainNotFoundException("Quotation not found: " + quotationId));
        var pr = quotation.getPurchaseRequest();
        var responses = quotation.getResponses();

        List<String> lines = new ArrayList<>();
        lines.add("MAPA COMPARATIVO DE COTACAO");
        lines.add("");
        lines.add("Material/Servico: " + pr.getDescription());
        lines.add("Quantidade: " + pr.getQuantity().toPlainString() + " " + pr.getUnit());
        lines.add("Prazo Cotacao: " + (quotation.getDeadline() != null ? quotation.getDeadline().toString() : "N/A"));
        lines.add("Status: " + quotation.getStatus());
        lines.add("");
        lines.add(String.format("%-4s %-30s %14s %8s %14s", "NUM", "FORNECEDOR", "PRECO UNIT.", "PRAZO", "TOTAL"));
        lines.add("--------------------------------------------------------------------------");

        var sorted = responses.stream()
                .sorted(Comparator.comparing(QuotationResponse::getUnitPrice))
                .toList();

        for (int i = 0; i < sorted.size(); i++) {
            var r = sorted.get(i);
            var total = r.getUnitPrice().multiply(pr.getQuantity());
            lines.add(String.format("%-4d %-30s %14s %8s %14s",
                    i + 1,
                    abbreviate(r.getSupplier().getName(), 30),
                    money(r.getUnitPrice()),
                    r.getDeliveryDays() != null ? r.getDeliveryDays() + "d" : "-",
                    money(total)));
        }

        lines.add("--------------------------------------------------------------------------");
        if (!sorted.isEmpty()) {
            var best = sorted.getFirst();
            lines.add("");
            lines.add("MELHOR PRECO: " + best.getSupplier().getName());
            lines.add("Valor Unitario: " + money(best.getUnitPrice()));
            lines.add("Valor Total: " + money(best.getUnitPrice().multiply(pr.getQuantity())));
            if (sorted.size() > 1) {
                var worst = sorted.getLast();
                var savings = worst.getUnitPrice().subtract(best.getUnitPrice()).multiply(pr.getQuantity());
                lines.add("Economia vs. maior preco: " + money(savings));
            }
        }

        return SimplePdf.write(lines);
    }

    public byte[] generatePurchaseOrderPdf(UUID orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainNotFoundException("Purchase order not found: " + orderId));

        List<String> lines = new ArrayList<>();
        lines.add("PEDIDO DE COMPRA");
        lines.add("");
        lines.add("Pedido N.: " + order.getNumber());
        lines.add("Status: " + order.getStatus());
        lines.add("Fornecedor: " + order.getSupplier().getName());
        lines.add("");
        lines.add("--- ITENS ---");
        lines.add(String.format("%-50s %10s %12s %12s", "DESCRICAO", "QTD", "UNIT.", "TOTAL"));
        lines.add("------------------------------------------------------------------------------------");
        lines.add(String.format("%-50s %10s %12s %12s",
                abbreviate(order.getDescription(), 50),
                order.getQuantity().toPlainString(),
                money(order.getUnitPrice()),
                money(order.getTotalAmount())));
        lines.add("------------------------------------------------------------------------------------");
        lines.add("TOTAL DO PEDIDO: " + money(order.getTotalAmount()));
        lines.add("");

        var receivings = order.getReceivings();
        if (!receivings.isEmpty()) {
            lines.add("--- RECEBIMENTOS ---");
            lines.add(String.format("%-12s %12s %s", "DATA", "QTD", "OBS"));
            receivings.forEach(r -> lines.add(String.format("%-12s %12s %s",
                    r.getReceivedAt(), r.getQuantityReceived().toPlainString(),
                    abbreviate(r.getNotes() != null ? r.getNotes() : "", 40))));
            lines.add("");
            lines.add("Total Recebido: " + order.getReceivedQuantity().toPlainString());
            var balance = order.getQuantity().subtract(order.getReceivedQuantity());
            lines.add("Saldo a Receber: " + balance.toPlainString());
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
