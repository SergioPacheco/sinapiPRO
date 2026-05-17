import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

export interface NextActionSuggestion {
  message: string;
  actionLabel: string;
  route: string;
}

const ACTION_MAP: Record<string, NextActionSuggestion> = {
  'budget.created': {
    message: 'Orçamento criado! Próximo passo: criar o cronograma.',
    actionLabel: 'Criar Cronograma',
    route: '../schedule',
  },
  'budget.approved': {
    message: 'Orçamento aprovado! Gere os pedidos de compra da Curva ABC.',
    actionLabel: 'Ir para Suprimentos',
    route: '../procurement',
  },
  'contract.created': {
    message: 'Contrato criado! Defina as parcelas financeiras.',
    actionLabel: 'Ir para Financeiro',
    route: '../finance',
  },
  'contract.activated': {
    message: 'Contrato ativado! A obra está pronta para execução.',
    actionLabel: 'Ver Dashboard',
    route: '../summary',
  },
  'measurement.submitted': {
    message: 'Medição enviada para aprovação.',
    actionLabel: 'Ver Medições',
    route: '../measurements',
  },
  'measurement.approved': {
    message: 'Medição aprovada! Gere a fatura no financeiro.',
    actionLabel: 'Ir para Financeiro',
    route: '../finance',
  },
  'order.received': {
    message: 'Pedido recebido! Verifique o estoque atualizado.',
    actionLabel: 'Ver Estoque',
    route: '../procurement',
  },
  'daily_log.created': {
    message: 'Diário registrado! Continue com o apontamento de horas.',
    actionLabel: 'Apontar Horas',
    route: '../time-tracking',
  },
  'project.created': {
    message: 'Obra criada! Comece pelo orçamento.',
    actionLabel: 'Criar Orçamento',
    route: 'budgets',
  },
  'schedule.created': {
    message: 'Cronograma criado! Defina os contratos com fornecedores.',
    actionLabel: 'Criar Contrato',
    route: '../contracts',
  },
};

@Injectable({ providedIn: 'root' })
export class NextActionService {
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  suggest(actionKey: string, baseRoute?: string) {
    const suggestion = ACTION_MAP[actionKey];
    if (!suggestion) return;

    const ref = this.snackBar.open(suggestion.message, suggestion.actionLabel, {
      duration: 8000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: 'next-action-snackbar',
    });

    ref.onAction().subscribe(() => {
      if (baseRoute) {
        this.router.navigate([baseRoute, suggestion.route]);
      } else {
        this.router.navigateByUrl(suggestion.route);
      }
    });
  }
}
