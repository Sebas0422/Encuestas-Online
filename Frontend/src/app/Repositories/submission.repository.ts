import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

import { SubmissionsRequest } from "../Models/submissions.model";

@Injectable({
    providedIn: 'root'
})

export class SubmissionRepository {
    private apiUrl = 'http://localhost:8080/api';

    constructor(private http: HttpClient) { }

    startSubmissionUser(form_id: number, payload: SubmissionsRequest): Observable<any> {
        const url = `${this.apiUrl}/forms/${form_id}/submissions`;
        return this.http.post<any>(url, payload);
    }

}