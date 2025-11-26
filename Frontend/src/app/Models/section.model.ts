// ============================================
// SECTION MODEL
// ============================================

export interface Section {
  id?: number;
  formId: number;
  title: string;
  position: number;
}

export interface SectionResponse {
  id: number;
  formId: number;
  title: string;
  position: number;
}

// ============================================
// DTOs DE REQUEST
// ============================================

export interface AddSectionRequest {
  title: string;
}

export interface RenameSectionRequest {
  title: string;
}

// ============================================
// PAGINACIÓN
// ============================================

export interface PaginatedSections {
  items: SectionResponse[];
  total: number;
  page: number;
  size: number;
}
