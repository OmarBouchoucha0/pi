import { Directive, ElementRef, Input, NgZone, OnInit, OnDestroy, inject } from '@angular/core';

@Directive({
  selector: '[appTilt]',
  standalone: true,
})
export class TiltDirective implements OnInit, OnDestroy {
  @Input() tiltStrength = 2; // degrees of rotation at screen edge
  @Input() tiltLerp = 0.06; // smoothing — lower is slower

  private el = inject(ElementRef<HTMLElement>);
  private ngZone = inject(NgZone);

  private targetX = 0;
  private targetY = 0;
  private currentX = 0;
  private currentY = 0;
  private rafId = 0;
  private lastMoveTime = 0;

  private onMouseMove = (e: MouseEvent) => {
    const now = Date.now();
    if (now - this.lastMoveTime < 16) return;
    this.lastMoveTime = now;

    const cx = window.innerWidth / 2;
    const cy = window.innerHeight / 2;
    this.targetX = ((e.clientY - cy) / cy) * -this.tiltStrength;
    this.targetY = ((e.clientX - cx) / cx) * this.tiltStrength;
  };

  private onMouseLeave = () => {
    this.targetX = 0;
    this.targetY = 0;
  };

  ngOnInit(): void {
    const el = this.el.nativeElement;
    el.style.willChange = 'transform';
    el.style.backfaceVisibility = 'hidden';

    // perspective must be on the parent
    const parent = el.parentElement;
    if (parent) parent.style.perspective = '900px';

    document.addEventListener('mousemove', this.onMouseMove);
    document.addEventListener('mouseleave', this.onMouseLeave);

    this.ngZone.runOutsideAngular(() => this.loop());
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.rafId);
    document.removeEventListener('mousemove', this.onMouseMove);
    document.removeEventListener('mouseleave', this.onMouseLeave);
  }

  private loop(): void {
    this.currentX += (this.targetX - this.currentX) * this.tiltLerp;
    this.currentY += (this.targetY - this.currentY) * this.tiltLerp;

    const dx = Math.abs(this.targetX - this.currentX);
    const dy = Math.abs(this.targetY - this.currentY);

    if (dx > 0.001 || dy > 0.001) {
      this.el.nativeElement.style.transform = `rotateX(${this.currentX}deg) rotateY(${this.currentY}deg)`;
    }

    this.rafId = requestAnimationFrame(() => this.loop());
  }
}
