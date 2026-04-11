import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { By } from '@angular/platform-browser';
import { TiltDirective } from './tilt.directive';

@Component({
  template: '<div appTilt id="host"></div>',
  imports: [TiltDirective],
})
class TestHostComponent {}

describe('TiltDirective', () => {
  let fixture: ComponentFixture<TestHostComponent>;
  let hostElement: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestHostComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();
    hostElement = fixture.nativeElement.querySelector('#host');
  });

  it('should create with directive', () => {
    const directiveEl = fixture.debugElement.query(By.directive(TiltDirective));
    expect(directiveEl).toBeTruthy();
  });

  it('should set willChange to transform', () => {
    expect(hostElement.style.willChange).toBe('transform');
  });

  it('should set backfaceVisibility to hidden', () => {
    expect(hostElement.style.backfaceVisibility).toBe('hidden');
  });

  it('should set perspective on parent element', () => {
    const parent = hostElement.parentElement;
    expect(parent?.style.perspective).toBe('900px');
  });

  it('should accept tiltStrength input', () => {
    const fixture2 = TestBed.createComponent(TestHostComponent);
    fixture2.detectChanges();
    const directive = fixture2.debugElement.children[0].injector.get(TiltDirective);
    expect(directive.tiltStrength).toBe(4);
  });

  it('should accept tiltLerp input', () => {
    const fixture2 = TestBed.createComponent(TestHostComponent);
    fixture2.detectChanges();
    const directive = fixture2.debugElement.children[0].injector.get(TiltDirective);
    expect(directive.tiltLerp).toBe(0.05);
  });

  it('should clean up on destroy', () => {
    const removeListenerSpy = spyOn(document, 'removeEventListener').and.callThrough();
    fixture.destroy();
    expect(removeListenerSpy).toHaveBeenCalledWith('mousemove', jasmine.any(Function));
    expect(removeListenerSpy).toHaveBeenCalledWith('mouseleave', jasmine.any(Function));
  });
});
