package com.sinapipro.api.budget.application;

import com.sinapipro.api.shared.error.DomainException;
import org.springframework.http.HttpStatus;

/**
 * Exception for the Budget module.
 * Each error scenario has a typed code with user-facing message.
 * Pattern: SGN3's CertificacaoBusinessException / ProdentBusinessException.
 */
public class BudgetException extends DomainException {

    public BudgetException(Error error) {
        super(error);
    }

    public BudgetException(Error error, Object... args) {
        super(error, args);
    }

    public enum Error implements ErrorCode {
        NOT_FOUND("budget.not-found", "Orçamento não encontrado: %s", HttpStatus.NOT_FOUND),
        ITEM_NOT_FOUND("budget.item-not-found", "Item não encontrado no orçamento: %s", HttpStatus.NOT_FOUND),
        STAGE_NOT_FOUND("budget.stage-not-found", "Etapa não encontrada no orçamento: %s", HttpStatus.NOT_FOUND),
        ALREADY_APPROVED("budget.already-approved", "Orçamento já aprovado, não pode ser alterado", HttpStatus.CONFLICT),
        DUPLICATE_CODE("budget.duplicate-code", "Já existe orçamento com o código: %s", HttpStatus.CONFLICT),
        TOTAL_MISMATCH("budget.total-mismatch", "Soma dos itens (%s) difere do total informado (%s)", HttpStatus.UNPROCESSABLE_ENTITY),
        EMPTY_ITEMS("budget.empty-items", "Orçamento deve ter pelo menos um item", HttpStatus.UNPROCESSABLE_ENTITY);

        private final String code;
        private final String message;
        private final HttpStatus status;

        Error(String code, String message, HttpStatus status) {
            this.code = code;
            this.message = message;
            this.status = status;
        }

        @Override public String code() { return code; }
        @Override public String message() { return message; }
        @Override public HttpStatus status() { return status; }
    }
}
