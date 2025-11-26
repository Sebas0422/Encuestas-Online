// ============================================
// ENUMS
// ============================================

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED'
}

// ============================================
// INTERFACES
// ============================================

export interface User {
  id: number;
  email: string;
  fullName: string;
  systemAdmin: boolean;
  status: UserStatus;
  createdAt: string;
}

// ============================================
// RESPONSE DTOs
// ============================================

export interface UserResponse {
  id: number;
  email: string;
  fullName: string;
  systemAdmin: boolean;
  status: UserStatus;
  createdAt: string;
}

export interface PaginatedUsers {
  items: UserResponse[];
  total: number;
  page: number;
  size: number;
}
