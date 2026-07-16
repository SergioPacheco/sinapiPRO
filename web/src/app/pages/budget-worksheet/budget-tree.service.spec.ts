import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MessageService } from 'primeng/api';
import { BudgetTreeService, BudgetRow, ItemType } from './budget-tree.service';

describe('BudgetTreeService', () => {
  let service: BudgetTreeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BudgetTreeService, MessageService],
    });
    service = TestBed.inject(BudgetTreeService);
  });

  function makeRow(overrides: Partial<BudgetRow> = {}): BudgetRow {
    return {
      type: 'COMPOSITION', level: 1, expanded: false,
      code: '', refCode: '', description: 'Item teste',
      unit: 'm²', quantity: 10, unitCost: 50,
      leisSociais: 0, bdi: 0, total: 500,
      editable: false, dirty: false, hidden: false,
      ...overrides,
    };
  }

  describe('updateCell — cálculo de total da linha', () => {
    it('should calculate total as quantity * unitCost', () => {
      // Arrange
      const row = makeRow({ quantity: 5, unitCost: 120, total: 0 });
      service.rows.set([
        makeRow({ type: 'LEVEL', level: 0, stageId: 's1' }),
        { ...row, stageId: 's1' },
      ]);

      // Act
      service.updateCell(service.rows()[1]);

      // Assert
      expect(service.rows()[1].total).toBe(600); // 5 * 120
    });

    it('should treat null quantity as zero', () => {
      // Arrange
      const row = makeRow({ quantity: null, unitCost: 100, total: 0, stageId: 's1' });
      service.rows.set([
        makeRow({ type: 'LEVEL', level: 0, stageId: 's1' }),
        row,
      ]);

      // Act
      service.updateCell(service.rows()[1]);

      // Assert
      expect(service.rows()[1].total).toBe(0);
    });

    it('should treat null unitCost as zero', () => {
      // Arrange
      const row = makeRow({ quantity: 10, unitCost: null, total: 0, stageId: 's1' });
      service.rows.set([
        makeRow({ type: 'LEVEL', level: 0, stageId: 's1' }),
        row,
      ]);

      // Act
      service.updateCell(service.rows()[1]);

      // Assert
      expect(service.rows()[1].total).toBe(0);
    });

    it('should mark row as dirty', () => {
      // Arrange
      const row = makeRow({ quantity: 2, unitCost: 30, dirty: false, stageId: 's1' });
      service.rows.set([
        makeRow({ type: 'LEVEL', level: 0, stageId: 's1' }),
        row,
      ]);

      // Act
      service.updateCell(service.rows()[1]);

      // Assert
      expect(service.rows()[1].dirty).toBe(true);
    });
  });

  describe('visibleRows — filtra linhas ocultas', () => {
    it('should exclude hidden rows', () => {
      // Arrange
      service.rows.set([
        makeRow({ description: 'Visible', hidden: false }),
        makeRow({ description: 'Hidden', hidden: true }),
        makeRow({ description: 'Also visible', hidden: false }),
      ]);

      // Act
      const visible = service.visibleRows();

      // Assert
      expect(visible).toHaveLength(2);
      expect(visible.map(r => r.description)).toEqual(['Visible', 'Also visible']);
    });

    it('should return empty array when all rows are hidden', () => {
      // Arrange
      service.rows.set([
        makeRow({ hidden: true }),
        makeRow({ hidden: true }),
      ]);

      // Act & Assert
      expect(service.visibleRows()).toHaveLength(0);
    });
  });

  describe('dirtyCount — conta alterações pendentes', () => {
    it('should count dirty rows', () => {
      // Arrange
      service.rows.set([
        makeRow({ dirty: true }),
        makeRow({ dirty: false }),
        makeRow({ dirty: true }),
      ]);

      // Act & Assert
      expect(service.dirtyCount()).toBe(2);
    });

    it('should return 0 when no rows are dirty', () => {
      // Arrange
      service.rows.set([
        makeRow({ dirty: false }),
        makeRow({ dirty: false }),
      ]);

      // Act & Assert
      expect(service.dirtyCount()).toBe(0);
    });
  });

  describe('toggle — expand/collapse', () => {
    it('should hide child rows when collapsing a LEVEL', () => {
      // Arrange
      service.rows.set([
        makeRow({ type: 'LEVEL', level: 0, expanded: true }),
        makeRow({ type: 'COMPOSITION', level: 1, hidden: false }),
        makeRow({ type: 'COMPOSITION', level: 1, hidden: false }),
      ]);

      // Act
      service.toggle(service.rows()[0]);

      // Assert
      expect(service.rows()[0].expanded).toBe(false);
      expect(service.rows()[1].hidden).toBe(true);
      expect(service.rows()[2].hidden).toBe(true);
    });

    it('should show child rows when expanding a collapsed LEVEL', () => {
      // Arrange
      service.rows.set([
        makeRow({ type: 'LEVEL', level: 0, expanded: false }),
        makeRow({ type: 'COMPOSITION', level: 1, hidden: true }),
        makeRow({ type: 'COMPOSITION', level: 1, hidden: true }),
      ]);

      // Act
      service.toggle(service.rows()[0]);

      // Assert
      expect(service.rows()[0].expanded).toBe(true);
      expect(service.rows()[1].hidden).toBe(false);
      expect(service.rows()[2].hidden).toBe(false);
    });

    it('should not affect sibling rows at the same level', () => {
      // Arrange
      service.rows.set([
        makeRow({ type: 'LEVEL', level: 0, expanded: true, description: 'Stage 1' }),
        makeRow({ type: 'COMPOSITION', level: 1, hidden: false }),
        makeRow({ type: 'LEVEL', level: 0, expanded: true, description: 'Stage 2' }),
        makeRow({ type: 'COMPOSITION', level: 1, hidden: false }),
      ]);

      // Act — collapse Stage 1
      service.toggle(service.rows()[0]);

      // Assert — Stage 2's child unaffected
      expect(service.rows()[1].hidden).toBe(true);  // Stage 1's child hidden
      expect(service.rows()[3].hidden).toBe(false); // Stage 2's child still visible
    });
  });

  describe('insertEmpty — adiciona linha vazia', () => {
    it('should insert a new EMPTY row after the given index', () => {
      // Arrange
      service.rows.set([
        makeRow({ type: 'LEVEL', level: 0, expanded: true, stageId: 's1' }),
        makeRow({ type: 'COMPOSITION', level: 1, stageId: 's1' }),
      ]);
      service.budgetId.set('budget-1');

      // Act
      const newRow = service.insertEmpty(0);

      // Assert
      expect(newRow.type).toBe('EMPTY');
      expect(newRow.editable).toBe(true);
      expect(service.rows().length).toBe(3);
    });

    it('should inherit stageId from context', () => {
      // Arrange
      service.rows.set([
        makeRow({ type: 'LEVEL', level: 0, expanded: true, stageId: 's1' }),
      ]);
      service.budgetId.set('budget-1');

      // Act
      const newRow = service.insertEmpty(0);

      // Assert
      expect(newRow.stageId).toBe('s1');
    });
  });
});
