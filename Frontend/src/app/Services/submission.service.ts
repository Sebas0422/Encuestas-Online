import { SubmissionRepository } from '../Repositories/submission.repository';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SubmissionsRequest } from '../Models/submissions.model';



@Injectable({
    providedIn: 'root'
})
export class SubmissionService {
    constructor(private repository: SubmissionRepository) { }

    startSubmissionUser(form_id: number, payload: SubmissionsRequest): Observable<any> {
        return this.repository.startSubmissionUser(form_id, payload);
    }
    


}