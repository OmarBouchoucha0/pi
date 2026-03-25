import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { provideRouter, Router } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render Welcome back heading', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent).toContain('Welcome back');
  });

  it('should render email input', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('input[name="email"]')).toBeTruthy();
  });

  it('should render password input', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('p-password')).toBeTruthy();
  });

  it('should render Sign In button', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const button = compiled.querySelector('p-button');
    expect(button).toBeTruthy();
    expect(button?.getAttribute('label')).toBe('Sign In');
  });

  it('should render Register link', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const register = compiled.querySelector('span[role="button"]');
    expect(register?.textContent).toContain('Register');
  });

  it('should navigate to signup when Register is clicked', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.router.navigate(['/signup']);
    expect(navigateSpy).toHaveBeenCalledWith(['/signup']);
  });
});
