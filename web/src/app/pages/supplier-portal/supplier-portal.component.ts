import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe, DatePipe } from '@angular/common';

interface SupplierQuotation { id: string; requisitionNumber: string; projectName: string; items: QuotationItem[]; dueDate: string; status: string; }
interface QuotationItem { id: string; description: string; unit: string; quantity: number; unitPrice: number | null; }
interface SupplierOrder { id: string; orderNumber: string; projectName: string; totalAmount: number; status: string; deliveryDate: string; }

@Component({
  selector: 'app-supplier-portal',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  template: `
    <div class="portal">
      <div class="portal-header">
        <h2>Portal do Fornecedor</h2>
        <p>Bem-vindo! Aqui você pode responder cotações e acompanhar pedidos.</p>
      </div>

      <section>
        <h3>Cotações Pendentes</h3>
        @for (q of quotations(); track q.id) {
          <div class="quote-card">
            <div class="quote-header">
              <strong>Requisição #{{ q.requisitionNumber }}</strong>
              <span>{{ q.projectName }} — Vence: {{ q.dueDate | date:'dd/MM/yyyy' }}</span>
            </div>
            <table>
              <thead><tr><th>Item</th><th>Un</th><th>Qtd</th><th>Preço Unit.</th></tr></thead>
              <tbody>
                @for (item of q.items; track item.id) {
                  <tr>
                    <td>{{ item.description }}</td>
                    <td>{{ item.unit }}</td>
                    <td class="num">{{ item.quantity }}</td>
                    <td class="num">
                      @if (q.status === 'PENDING') {
                        <input type="number" [value]="item.unitPrice" (change)="updatePrice(q.id, item.id, $event)" min="0" step="0.01" />
                      } @else {
                        R$ {{ item.unitPrice | number:'1.2-2' }}
                      }
                    </td>
                  </tr>
                }
              </tbody>
            </table>
            @if (q.status === 'PENDING') {
              <button class="btn-primary" (click)="submitQuote(q.id)">Enviar Cotação</button>
            }
          </div>
        }
      </section>

      <section>
        <h3>Meus Pedidos</h3>
        <table class="orders-table">
          <thead><tr><th>Pedido</th><th>Obra</th><th>Valor</th><th>Entrega</th><th>Status</th></tr></thead>
          <tbody>
            @for (o of orders(); track o.id) {
              <tr>
                <td>{{ o.orderNumber }}</td>
                <td>{{ o.projectName }}</td>
                <td class="num">R$ {{ o.totalAmount | number:'1.2-2' }}</td>
                <td>{{ o.deliveryDate | date:'dd/MM/yyyy' }}</td>
                <td><span class="badge" [attr.data-status]="o.status">{{ o.status }}</span></td>
              </tr>
            }
          </tbody>
        </table>
      </section>
    </div>
  `,
  styles: [`
    .portal-header { margin-bottom: 2rem; h2 { margin: 0; color: #e0e0e0; } p { color: #8a8aaa; margin-top: 0.25rem; } }
    h3 { color: #8a8aaa; font-size: 0.85rem; text-transform: uppercase; margin-bottom: 1rem; }
    section { margin-bottom: 2.5rem; }
    .quote-card { background: #16213e; border-radius: 10px; border: 1px solid #2a2a4a; padding: 1.25rem; margin-bottom: 1rem; }
    .quote-header { display: flex; justify-content: space-between; margin-bottom: 1rem; strong { color: #4fc3f7; } span { color: #8a8aaa; font-size: 0.85rem; } }
    table { width: 100%; border-collapse: collapse; }
    th { text-align: left; padding: 0.5rem; color: #8a8aaa; font-size: 0.75rem; border-bottom: 1px solid #2a2a4a; }
    td { padding: 0.5rem; border-bottom: 1px solid #1a1a2e; }
    .num { text-align: right; font-family: monospace; }
    input[type="number"] { width: 100px; padding: 0.3rem; border-radius: 4px; border: 1px solid #3a3a5a; background: #1a1a2e; color: #e0e0e0; text-align: right; }
    .btn-primary { margin-top: 1rem; padding: 0.5rem 1rem; border-radius: 6px; border: none; background: #4fc3f7; color: #1a1a2e; font-weight: 600; cursor: pointer; }
    .orders-table { background: #16213e; border-radius: 10px; overflow: hidden; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem;
      &[data-status="PENDING"] { background: #e65100; color: #ffcc80; }
      &[data-status="CONFIRMED"] { background: #0f3460; color: #4fc3f7; }
      &[data-status="DELIVERED"] { background: #1b5e20; color: #a5d6a7; }
    }
  `]
})
export class SupplierPortalComponent implements OnInit {
  private http = inject(HttpClient);
  quotations = signal<SupplierQuotation[]>([]);
  orders = signal<SupplierOrder[]>([]);

  ngOnInit() {
    this.http.get<SupplierQuotation[]>('/supplier-portal/quotations').subscribe(q => this.quotations.set(q));
    this.http.get<SupplierOrder[]>('/supplier-portal/orders').subscribe(o => this.orders.set(o));
  }

  updatePrice(quoteId: string, itemId: string, event: Event) {
    const value = (event.target as HTMLInputElement).valueAsNumber;
    this.http.patch(`/supplier-portal/quotations/${quoteId}/items/${itemId}`, { unitPrice: value }).subscribe();
  }

  submitQuote(id: string) {
    this.http.post(`/supplier-portal/quotations/${id}/submit`, {}).subscribe(() => {
      this.http.get<SupplierQuotation[]>('/supplier-portal/quotations').subscribe(q => this.quotations.set(q));
    });
  }
}
