import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ResponsesRepository } from '../Repositories/responses.repository';


@Injectable({ providedIn: 'root' })
export class ResponsesService {
    constructor(private repository: ResponsesRepository) { }

    saveResponseTrueFalseAnswer(submission_id: number, questionId: number, answer: boolean): Observable<void> {
        return this.repository.saveResponseTrueFalseAnswer(submission_id, questionId, answer);
    }
    
    saveResponseChoiceAnswer(submission_id: number, questionId: number, optionIds: number[]): Observable<void> {
        return this.repository.saveResponseChoiceAnswer(submission_id, questionId, optionIds);
    }

    saveResponseTextAnswer(submission_id: number, questionId: number, text: string): Observable<void> {
        return this.repository.saveResponseTextAnswer(submission_id, questionId, text);
    }

    getSubmissionsByForm(formId: number): Observable<any> {
        return this.repository.getSubmissionsByForm(formId);
    }

    getResponsesByQuestion(formId: number, questionId: number): Observable<any> {
        return this.repository.getResponsesByQuestion(formId, questionId);
    }

    getFormStatistics(formId: number): Observable<any> {
        return this.repository.getFormStatistics(formId);
    }

    getFormReport(formId: number, includeDrafts: boolean = true): Observable<any> {
        return this.repository.getFormReport(formId, includeDrafts);
    }
}