import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Forms, PaginatedForms } from '../Models/form.model';
import { FormRepository } from '../Repositories/form.repository';

@Injectable({
    providedIn: 'root'
})
export class FormService {
    constructor(private repository: FormRepository) { }

    getAllForms(campaignId?: number): Observable<Forms[]> {
        return this.repository.getAll(campaignId!).pipe(
            map((response: PaginatedForms) => response.items)
        );
    }

    createForm(campaignId: number, form: Forms): Observable<Forms> {
        return this.repository.create(campaignId, form);
    }

    getFormById(id: number): Observable<Forms> {
        return this.repository.getById(id);
    }


    updateTitle(id: number, title: string): Observable<Forms> {
        return this.repository.updateTitle(id, title);
    }

    updateDescription(id: number, description: string): Observable<Forms> {
        return this.repository.updateDescription(id, description);
    }

    updateSchedule(id: number, openAt: string, closeAt: string): Observable<Forms> {
        return this.repository.updateSchedule(id, openAt, closeAt);
    }

    updateTheme(id: number, mode: string, primaryColor: string): Observable<Forms> {
        return this.repository.updateTheme(id, mode, primaryColor);
    }

    toggleAnonymous(id: number, enabled: boolean): Observable<Forms> {
        return this.repository.toggleAnonymous(id, enabled);
    }

    toggleAutoSave(id: number, enabled: boolean): Observable<Forms> {
        return this.repository.toggleAutoSave(id, enabled);
    }

    toggleAllowEdit(id: number, enabled: boolean): Observable<Forms> {
        return this.repository.toggleAllowEdit(id, enabled);
    }

    updatePresentation(id: number, form: Forms): Observable<Forms> {
        return this.repository.updatePresentation(id, form);
    }

    deleteForm(id: number): Observable<void> {
        return this.repository.delete(id);
    }

}
