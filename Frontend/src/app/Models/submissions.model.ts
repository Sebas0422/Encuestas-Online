import { ResponsePayload } from './response.model';


export interface SubmissionsRequest {
    formId: number;
    respondentType: 'USER' | 'ANONYMOUS';
    userId?: number;
    email?: string | null;
    code?: string | null;
    sourceIp: string;
    responses: ResponsePayload[];
}