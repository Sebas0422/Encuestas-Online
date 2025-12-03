import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
    PaginatedForms, Forms, UpdateTitleRequest, UpdateDescriptionRequest, RescheduleRequest,
    ToggleFlagRequest, SetPresentationRequest, UpdateThemeRequest, SetAccessModeRequest
} from "../Models/form.model";
import { PublishResponse } from "../Models/Publish.model";




@Injectable({
    providedIn: 'root'
})
export class FormRepository {
    private apiURL = 'http://localhost:8080/api/forms'


    constructor(private http: HttpClient) {

    }

    getAll(campaignId: number): Observable<PaginatedForms> {
        const url = `http://localhost:8080/api/campaigns/${campaignId}/forms`;
        let params = new HttpParams()
            .append('page', '0')
            .append('size', '20');

        return this.http.get<PaginatedForms>(url, { params });
    }

    create(campaignId: number, form: Forms): Observable<Forms> {
        const url = `http://localhost:8080/api/campaigns/${campaignId}/forms`;
        return this.http.post<Forms>(url, form);
    }

    getById(id: number): Observable<Forms> {
        return this.http.get<Forms>(`${this.apiURL}/${id}`);
    }

    // Public endpoint: obtiene un formulario por token público
    getPublicForm(token: string): Observable<Forms> {
        const url = `http://localhost:8080/api/public/forms/${token}`;
        return this.http.get<Forms>(url);
    }

    publish(id: number, force: boolean = false): Observable<PublishResponse> {
        const url = `${this.apiURL}/${id}/public-link`;

        if (force) {
            const params = new HttpParams().set('force', 'false');
            return this.http.post<PublishResponse>(url, {}, { params });
        }

        return this.http.post<PublishResponse>(url, {});
    }

    // --- MÉTODOS GRANULARES (PATCH) ---

    updateTitle(id: number, title: string): Observable<Forms> {
        const body: UpdateTitleRequest = { title };
        return this.http.patch<Forms>(`${this.apiURL}/${id}/title`, body);
    }

    updateDescription(id: number, description: string): Observable<Forms> {
        const body: UpdateDescriptionRequest = { description };
        return this.http.patch<Forms>(`${this.apiURL}/${id}/description`, body);
    }

    updateSchedule(id: number, openAt: string, closeAt: string): Observable<Forms> {
        const body: RescheduleRequest = { openAt, closeAt };
        return this.http.patch<Forms>(`${this.apiURL}/${id}/schedule`, body);
    }

    updateTheme(id: number, mode: string, primaryColor: string): Observable<Forms> {
        const body: UpdateThemeRequest = { mode, primaryColor };
        return this.http.patch<Forms>(`${this.apiURL}/${id}/theme`, body);
    }

    updateAccessMode(id: number, mode: string): Observable<Forms> {
        const body: SetAccessModeRequest = { mode };
        return this.http.patch<Forms>(`${this.apiURL}/${id}/access-mode`, body);
    }

    toggleAnonymous(id: number, enabled: boolean): Observable<Forms> {
        const body: ToggleFlagRequest = { enabled };
        return this.http.patch<Forms>(`${this.apiURL}/${id}/anonymous`, body);
    }

    toggleAutoSave(id: number, enabled: boolean): Observable<Forms> {
        const body: ToggleFlagRequest = { enabled };
        return this.http.patch<Forms>(`${this.apiURL}/${id}/autosave`, body);
    }

    toggleAllowEdit(id: number, enabled: boolean): Observable<Forms> {
        const body: ToggleFlagRequest = { enabled };
        return this.http.patch<Forms>(`${this.apiURL}/${id}/allow-edit`, body);
    }

    updatePresentation(id: number, form: Forms): Observable<Forms> {
        const payload: SetPresentationRequest = {
            shuffleQuestions: form.shuffleQuestions,
            shuffleOptions: form.shuffleOptions,
            progressBar: form.progressBar,
            paginated: form.paginated
        };
        return this.http.patch<Forms>(`${this.apiURL}/${id}/presentation`, payload);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiURL}/${id}`);
    }





} 
