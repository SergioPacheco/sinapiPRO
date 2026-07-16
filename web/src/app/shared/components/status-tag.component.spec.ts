import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StatusTagComponent } from './index';

describe('StatusTagComponent', () => {
  let component: StatusTagComponent;
  let fixture: ComponentFixture<StatusTagComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusTagComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(StatusTagComponent);
    component = fixture.componentInstance;
  });

  const cases: Array<[string, string, string]> = [
    ['DRAFT', 'Rascunho', 'secondary'],
    ['IN_PROGRESS', 'Em Execução', 'success'],
    ['SUBMITTED', 'Submetida', 'info'],
    ['APPROVED', 'Aprovada', 'success'],
    ['REJECTED', 'Rejeitada', 'danger'],
    ['PAID', 'Paga', 'success'],
    ['PENDING', 'Pendente', 'warn'],
    ['CANCELLED', 'Cancelada', 'danger'],
    ['OVERDUE', 'Vencida', 'danger'],
  ];

  describe.each(cases)('status %s', (status, expectedLabel, expectedSeverity) => {
    it(`should map to label "${expectedLabel}" and severity "${expectedSeverity}"`, () => {
      // Arrange
      fixture.componentRef.setInput('status', status);

      // Act
      fixture.detectChanges();

      // Assert
      expect(component.label()).toBe(expectedLabel);
      expect(component.severity()).toBe(expectedSeverity);
    });
  });

  it('should return the raw status as label for unknown statuses', () => {
    // Arrange
    fixture.componentRef.setInput('status', 'UNKNOWN_STATUS');

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.label()).toBe('UNKNOWN_STATUS');
    expect(component.severity()).toBe('secondary');
  });
});
