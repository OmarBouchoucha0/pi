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
import { Department, Tenant } from '../../../shared/types/user';

@Component({
  selector: 'app-departments',
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
  templateUrl: './departments.component.html',
  styleUrl: './departments.component.scss',
})
export class DepartmentsComponent implements OnInit {
  view = 'table' as const;
  departments: Department[] = [];
  tenants: Tenant[] = [];
  loading = true;

  showAddDialog = false;
  showEditDialog = false;
  submitted = false;
  loadingSubmit = false;
  errorMessage = '';

  isEditMode = false;
  selectedDepartment: Department | null = null;
  selectedDepartmentId: number | null = null;

  name = '';
  description = '';
  selectedTenantId: number | null = 1;
  selectedHospitalId: number | null = 1;

  tenantOptions: { label: string; value: number }[] = [];

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
        this.loadDepartments();
      },
    });
  }

  loadDepartments(): void {
    this.loading = true;
    this.userService.getDepartments().subscribe({
      next: (departments) => {
        this.departments = departments;
        this.mapTenantNames(departments);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  mapTenantNames(departments: Department[]): void {
    departments.forEach((dept) => {
      const tenant = this.tenants.find((t) => t.id === (dept.tenantId ?? dept.tenant?.id));
      if (tenant) {
        dept.tenant = { ...tenant };
      }
    });
  }

  onTenantChange(tenantId: number): void {
    this.selectedTenantId = tenantId;
    if (tenantId) {
      this.loading = true;
      this.userService.getDepartmentsByTenant(tenantId).subscribe({
        next: (departments) => {
          this.departments = departments;
          this.mapTenantNames(departments);
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        },
      });
    } else {
      this.loadDepartments();
    }
  }

  openAddDialog(): void {
    this.submitted = false;
    this.errorMessage = '';
    this.name = '';
    this.description = '';
    this.selectedTenantId = 1;
    this.selectedHospitalId = 1;
    this.showAddDialog = true;
  }

  closeAddDialog(): void {
    this.showAddDialog = false;
  }

  openEditDialog(department: Department): void {
    this.submitted = false;
    this.errorMessage = '';
    this.isEditMode = true;
    this.selectedDepartment = department;
    this.selectedDepartmentId = department.id ?? null;
    this.name = department.name ?? '';
    this.description = department.description ?? '';
    this.selectedTenantId = department.tenant?.id ?? 1;
    this.selectedHospitalId = department.hospital?.id ?? 1;
    this.showEditDialog = true;
  }

  closeEditDialog(): void {
    this.showEditDialog = false;
    this.isEditMode = false;
    this.selectedDepartment = null;
    this.selectedDepartmentId = null;
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

    const deptData = {
      name: this.name,
      description: this.description,
      tenant: { id: this.selectedTenantId! },
      hospital: { id: this.selectedHospitalId! },
    };

    if (this.isEditMode && this.selectedDepartmentId) {
      this.userService
        .updateDepartment(this.selectedDepartmentId, {
          name: this.name,
          description: this.description,
          tenantId: this.selectedTenantId!,
        })
        .subscribe({
          next: () => {
            this.loadingSubmit = false;
            this.showEditDialog = false;
            this.messageService.add({
              severity: 'success',
              summary: 'Updated',
              detail: 'Department has been updated',
            });
            this.loadDepartments();
          },
          error: (error) => {
            this.loadingSubmit = false;
            this.errorMessage =
              error.error?.message || 'Failed to update department. Please try again.';
          },
        });
    } else {
      this.userService.createDepartment(deptData).subscribe({
        next: () => {
          this.loadingSubmit = false;
          this.showAddDialog = false;
          this.messageService.add({
            severity: 'success',
            summary: 'Created',
            detail: 'Department has been created',
          });
          this.loadDepartments();
        },
        error: (error) => {
          this.loadingSubmit = false;
          this.errorMessage =
            error.error?.message || 'Failed to create department. Please try again.';
        },
      });
    }
  }

  openDeleteDialog(department: Department, event: Event): void {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: `Are you sure you want to delete ${department.name}?`,
      header: 'Delete Department',
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
        this.userService.deleteDepartment(department.id).subscribe({
          next: () => {
            this.loading = false;
            this.messageService.add({
              severity: 'success',
              summary: 'Deleted',
              detail: `${department.name} has been deleted`,
            });
            this.loadDepartments();
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
