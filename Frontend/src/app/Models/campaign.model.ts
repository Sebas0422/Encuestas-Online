export interface Campaign {
  id?: number;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface PaginatedCampaigns {
  items: Campaign[];
  total: number;
  page: number;
  size: number;
}

export interface RenameCampaignRequest {
  name: string;
}

export interface ChangeDescriptionRequest {
  description: string;
}

export interface RescheduleCampaignRequest {
  startDate: string; 
  endDate: string;
}

export interface ChangeStatusRequest {
  status: string; 
}
