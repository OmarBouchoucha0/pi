import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { AvatarModule } from 'primeng/avatar';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { FloatLabelModule } from 'primeng/floatlabel';
import { SelectModule } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TenantStatus, Tenant } from '../../../shared/types/user';

@Component({
  selector: 'app-tenants',
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
  templateUrl: './tenants.component.html',
  styleUrl: './tenants.component.scss',
})
export class TenantsComponent implements OnInit {
  view = 'table' as const;
  tenants: Tenant[] = [];
  loading = true;

  showAddDialog = false;
  showEditDialog = false;
  submitted = false;
  loadingSubmit = false;
  errorMessage = '';

  isEditMode = false;
  selectedTenant: Tenant | null = null;
  selectedTenantId: number | null = null;

  name = '';
  status: TenantStatus = 'ACTIVE';

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

  get tenantDialogVisible(): boolean {
    return this.showAddDialog || this.showEditDialog;
  }

  loadTenants(): void {
    this.loading = true;
    this.userService.getTenants().subscribe({
      next: (tenants) => {
        this.tenants = tenants;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  openAddDialog(): void {
    this.submitted = false;
    this.errorMessage = '';
    this.name = '';
    this.showAddDialog = true;
  }

  closeAddDialog(): void {
    this.showAddDialog = false;
  }

  openEditDialog(tenant: Tenant): void {
    this.submitted = false;
    this.errorMessage = '';
    this.isEditMode = true;
    this.selectedTenant = tenant;
    this.selectedTenantId = tenant.id ?? null;
    this.name = tenant.name ?? '';
    this.status = tenant.status ?? 'ACTIVE';
    this.showEditDialog = true;
  }

  closeEditDialog(): void {
    this.showEditDialog = false;
    this.isEditMode = false;
    this.selectedTenant = null;
    this.selectedTenantId = null;
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';

    const nameInvalid = !this.name.trim();

    if (nameInvalid) {
      return;
    }

    this.loadingSubmit = true;

    if (this.isEditMode && this.selectedTenantId) {
      this.userService
        .updateTenant(this.selectedTenantId, { name: this.name, status: this.status })
        .subscribe({
          next: () => {
            this.loadingSubmit = false;
            this.showEditDialog = false;
            this.messageService.add({
              severity: 'success',
              summary: 'Updated',
              detail: 'Tenant has been updated',
            });
            this.loadTenants();
          },
          error: (error) => {
            this.loadingSubmit = false;
            this.errorMessage =
              error.error?.message || 'Failed to update tenant. Please try again.';
          },
        });
    } else {
      this.userService.createTenant(this.name).subscribe({
        next: () => {
          this.loadingSubmit = false;
          this.showAddDialog = false;
          this.messageService.add({
            severity: 'success',
            summary: 'Created',
            detail: 'Tenant has been created',
          });
          this.loadTenants();
        },
        error: (error) => {
          this.loadingSubmit = false;
          this.errorMessage = error.error?.message || 'Failed to create tenant. Please try again.';
        },
      });
    }
  }

  openDeleteDialog(tenant: Tenant, event: Event): void {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: `Are you sure you want to delete ${tenant.name}?`,
      header: 'Delete Tenant',
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
        this.userService.deleteTenant(tenant.id).subscribe({
          next: () => {
            this.loading = false;
            this.messageService.add({
              severity: 'success',
              summary: 'Deleted',
              detail: `${tenant.name} has been deleted`,
            });
            this.loadTenants();
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
