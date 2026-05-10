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
  name: string;
  document: string;
  role: string;
  type: 'EMPLOYEE' | 'CONTRACTOR';
  email: string;
  phone: string;
  hourlyRate: number | null;
  admissionDate: string | null;
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
