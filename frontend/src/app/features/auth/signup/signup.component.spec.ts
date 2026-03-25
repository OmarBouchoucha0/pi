import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SignupComponent } from './signup.component';
import { provideRouter, Router } from '@angular/router';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SignupComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render Create account heading', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent).toContain('Create account');
  });

  it('should render username input', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('input[name="username"]')).toBeTruthy();
  });

  it('should render email input', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('input[name="email"]')).toBeTruthy();
  });

  it('should render password input', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('p-password[name="password"]')).toBeTruthy();
  });

  it('should render confirm password input', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('p-password[name="confirmPassword"]')).toBeTruthy();
  });

  it('should render Create Account button', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const button = compiled.querySelector('p-button');
    expect(button).toBeTruthy();
    expect(button?.getAttribute('label')).toBe('Create Account');
  });

  it('should render Sign in link', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const signIn = compiled.querySelector('span[role="button"]');
    expect(signIn?.textContent).toContain('Sign in');
  });

  it('should navigate to login when goToLogin is called', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.goToLogin();
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });
});
