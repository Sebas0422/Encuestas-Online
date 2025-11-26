// ============================================
// ENUMS
// ============================================

export enum MemberRole {
  ADMIN = 'ADMIN',
  CREATOR = 'CREATOR',
  READER = 'READER'
}

// ============================================
// INTERFACES
// ============================================

export interface Member {
  id: number;
  campaignId: number;
  userId: number;
  userEmail: string;
  userFullName: string;
  role: MemberRole;
  addedAt: string; // ISO 8601
}

// ============================================
// REQUEST DTOs
// ============================================

export interface AddMemberRequest {
  userId: number;
  role: MemberRole;
}

export interface UpdateRoleRequest {
  role: MemberRole;
}

// ============================================
// RESPONSE DTOs
// ============================================

export interface MemberResponse {
  campaignId: number;
  userId: number;
  role: MemberRole;
  createdAt: string;
}

export interface PaginatedMembers {
  items: MemberResponse[];
  total: number;
  page: number;
  size: number;
}

// ============================================
// EXTENDED RESPONSE (con información del usuario)
// ============================================

export interface MemberWithUserInfo extends MemberResponse {
  userEmail?: string;
  userFullName?: string;
}
