export interface Client {
  id: string;
  name: string;
  document: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  state: string;
}

export interface Employee {
  id: string;
  employeeCode: string;
  name: string;
  document: string;
  role: string;
  specialty: string;
  type: 'EMPLOYEE' | 'CONTRACTOR';
  employmentStatus: 'ACTIVE' | 'ON_LEAVE' | 'INACTIVE';
  email: string;
  phone: string;
  mobilePhone: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  address: string;
  city: string;
  state: string;
  postalCode: string;
  costCenter: string;
  companyName: string;
  notes: string;
  hourlyRate: number | null;
  admissionDate: string | null;
  terminationDate: string | null;
}

export interface TeamMember {
  employeeId: string;
  name: string;
  role: string;
}

export interface Team {
  id: string;
  name: string;
  description: string;
  projectId: string | null;
  projectName: string | null;
  active: boolean;
  members: TeamMember[];
}

export interface PaymentMethod {
  id: string;
  name: string;
  installments: number;
}

export interface BankAccount {
  id: string;
  bankCode: string;
  bankName: string;
  agency: string;
  accountNumber: string;
  accountType: string;
  holderName: string;
}
