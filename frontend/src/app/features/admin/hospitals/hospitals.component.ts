import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { AvatarModule } from 'primeng/avatar';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { FloatLabelModule } from 'primeng/floatlabel';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TenantStatus, Tenant, Hospital } from '../../../shared/types/user';

@Component({
  selector: 'app-hospitals',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    AvatarModule,
    DialogModule,
    ConfirmDialogModule,
    ToastModule,
    ButtonModule,
    InputTextModule,
    SelectModule,
    FloatLabelModule,
    FormsModule,
  ],
  providers: [ConfirmationService, MessageService],
  templateUrl: './hospitals.component.html',
  styleUrl: './hospitals.component.scss',
})
export class HospitalsComponent implements OnInit {
  view = 'table' as const;
  hospitals: Hospital[] = [];
  tenants: Tenant[] = [];
  loading = true;

  showAddDialog = false;
  showEditDialog = false;
  submitted = false;
  loadingSubmit = false;
  errorMessage = '';

  isEditMode = false;
  selectedHospital: Hospital | null = null;
  selectedHospitalId: number | null = null;

  name = '';
  selectedTenantId: number | null = 1;
  status: TenantStatus = 'ACTIVE';

  tenantOptions: { label: string; value: number }[] = [];

  statusOptions: { label: string; value: string }[] = [
    { label: 'Active', value: 'ACTIVE' },
    { label: 'Inactive', value: 'INACTIVE' },
    { label: 'Suspended', value: 'SUSPENDED' },
  ];

  private userService = inject(UserService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  ngOnInit(): void {
    this.loadTenants();
  }

  loadTenants(): void {
    this.userService.getTenants().subscribe({
      next: (tenants) => {
        this.tenants = tenants;
        this.tenantOptions = tenants.map((t) => ({ label: t.name, value: t.id }));
        this.loadHospitals();
      },
    });
  }

  loadHospitals(): void {
    this.loading = true;
    this.userService.getHospitals().subscribe({
      next: (hospitals) => {
        this.hospitals = hospitals;
        this.mapTenantNames(hospitals);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  mapTenantNames(hospitals: Hospital[]): void {
    hospitals.forEach((hosp) => {
      const tenant = this.tenants.find((t) => t.id === (hosp.tenantId ?? hosp.tenant?.id));
      if (tenant) {
        hosp.tenant = { ...tenant };
      }
    });
  }

  onTenantChange(tenantId: number): void {
    this.selectedTenantId = tenantId;
    if (tenantId) {
      this.loading = true;
      this.userService.getHospitalsByTenant(tenantId).subscribe({
        next: (hospitals) => {
          this.hospitals = hospitals;
          this.mapTenantNames(hospitals);
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        },
      });
    } else {
      this.loadHospitals();
    }
  }

  get hospitalDialogVisible(): boolean {
    return this.showAddDialog || this.showEditDialog;
  }

  openAddDialog(): void {
    this.submitted = false;
    this.errorMessage = '';
    this.name = '';
    this.selectedTenantId = 1;
    this.status = 'ACTIVE';
    this.showAddDialog = true;
  }

  closeAddDialog(): void {
    this.showAddDialog = false;
  }

  openEditDialog(hospital: Hospital): void {
    this.submitted = false;
    this.errorMessage = '';
    this.isEditMode = true;
    this.selectedHospital = hospital;
    this.selectedHospitalId = hospital.id ?? null;
    this.name = hospital.name ?? '';
    this.selectedTenantId = hospital.tenant?.id ?? 1;
    this.status = hospital.status ?? 'ACTIVE';
    this.showEditDialog = true;
  }

  closeEditDialog(): void {
    this.showEditDialog = false;
    this.isEditMode = false;
    this.selectedHospital = null;
    this.selectedHospitalId = null;
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';

    const nameInvalid = !this.name.trim();
    const tenantInvalid = !this.selectedTenantId;

    if (nameInvalid || tenantInvalid) {
      return;
    }

    this.loadingSubmit = true;

    const hospitalData = {
      name: this.name,
      tenant: { id: this.selectedTenantId! },
      status: this.status,
    };

    if (this.isEditMode && this.selectedHospitalId) {
      this.userService
        .updateHospital(this.selectedHospitalId, {
          name: this.name,
          status: this.status,
        })
        .subscribe({
          next: () => {
            this.loadingSubmit = false;
            this.showEditDialog = false;
            this.messageService.add({
              severity: 'success',
              summary: 'Updated',
              detail: 'Hospital has been updated',
            });
            this.loadHospitals();
          },
          error: (error) => {
            this.loadingSubmit = false;
            this.errorMessage =
              error.error?.message || 'Failed to update hospital. Please try again.';
          },
        });
    } else {
      this.userService.createHospital(hospitalData).subscribe({
        next: () => {
          this.loadingSubmit = false;
          this.showAddDialog = false;
          this.messageService.add({
            severity: 'success',
            summary: 'Created',
            detail: 'Hospital has been created',
          });
          this.loadHospitals();
        },
        error: (error) => {
          this.loadingSubmit = false;
          this.errorMessage =
            error.error?.message || 'Failed to create hospital. Please try again.';
        },
      });
    }
  }

  openDeleteDialog(hospital: Hospital, event: Event): void {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: `Are you sure you want to delete ${hospital.name}?`,
      header: 'Delete Hospital',
      icon: 'pi pi-exclamation-triangle',
      rejectLabel: 'Cancel',
      rejectButtonProps: {
        label: 'Cancel',
        severity: 'secondary',
        outlined: true,
      },
      acceptButtonProps: {
        label: 'Delete',
        severity: 'danger',
      },
      accept: () => {
        this.loading = true;
        this.userService.deleteHospital(hospital.id).subscribe({
          next: () => {
            this.loading = false;
            this.messageService.add({
              severity: 'success',
              summary: 'Deleted',
              detail: `${hospital.name} has been deleted`,
            });
            this.loadHospitals();
          },
          error: () => {
            this.loading = false;
          },
        });
      },
    });
  }

  getInitials(name: string): string {
    return name ? name.substring(0, 2).toUpperCase() : '??';
  }
}
