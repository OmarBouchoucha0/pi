import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DumpComponent } from './dump.component';

describe('DumpComponent', () => {
  let component: DumpComponent;
  let fixture: ComponentFixture<DumpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DumpComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(DumpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
