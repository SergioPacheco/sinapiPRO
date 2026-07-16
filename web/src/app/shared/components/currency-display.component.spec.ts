import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CurrencyDisplayComponent } from './index';

describe('CurrencyDisplayComponent', () => {
  let component: CurrencyDisplayComponent;
  let fixture: ComponentFixture<CurrencyDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CurrencyDisplayComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CurrencyDisplayComponent);
    component = fixture.componentInstance;
  });

  it('should format 1000 as R$ 1.000,00', () => {
    // Arrange
    fixture.componentRef.setInput('value', 1000);

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.formatted()).toBe('R$\u00a01.000,00');
  });

  it('should format 0 as R$ 0,00', () => {
    // Arrange
    fixture.componentRef.setInput('value', 0);

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.formatted()).toBe('R$\u00a00,00');
  });

  it('should format decimal values with 2 places', () => {
    // Arrange
    fixture.componentRef.setInput('value', 1234.5);

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.formatted()).toBe('R$\u00a01.234,50');
  });

  it('should format large values with thousands separator', () => {
    // Arrange
    fixture.componentRef.setInput('value', 1500000.99);

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.formatted()).toBe('R$\u00a01.500.000,99');
  });

  it('should return dash for null value', () => {
    // Arrange
    fixture.componentRef.setInput('value', null as any);

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.formatted()).toBe('—');
  });

  it('should format negative values', () => {
    // Arrange
    fixture.componentRef.setInput('value', -500.3);

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.formatted()).toContain('500,30');
    expect(component.formatted()).toContain('-');
  });
});
