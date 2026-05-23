import { ActivatedRoute } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { InventoryListComponent } from './inventory-list';
import { InventoryService } from '../services/inventory.service';

describe('InventoryListComponent', () => {
  let fixture: ComponentFixture<InventoryListComponent>;
  let component: InventoryListComponent;
  let service: jasmine.SpyObj<InventoryService>;

  beforeEach(async () => {
    service = jasmine.createSpyObj<InventoryService>('InventoryService', ['listItems']);
    service.listItems.and.returnValue(of([
      {
        id: 'i1',
        description: 'Cimento CP-II',
        currentQuantity: 120,
        minQuantity: 40,
        unit: 'SC',
        location: 'Almoxarifado A',
        belowMinimum: false,
      },
    ]));

    await TestBed.configureTestingModule({
      imports: [InventoryListComponent],
      providers: [
        { provide: InventoryService, useValue: service },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: new Map() },
            parent: { snapshot: { paramMap: new Map([['projectId', 'p1']]) } },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(InventoryListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load inventory items on init', () => {
    expect(service.listItems).toHaveBeenCalledWith('p1');
    expect(component.list.length).toBe(1);
    expect(component.total).toBe(1);
  });
});
