import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { UserRepository } from '../Repositories/user.repository';
import { UserResponse, PaginatedUsers } from '../Models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private repository: UserRepository) { }

  getUserById(id: number): Observable<UserResponse> {
    return this.repository.getById(id);
  }

  getUserByEmail(email: string): Observable<UserResponse> {
    return this.repository.getByEmail(email);
  }

  getAllUsers(search?: string, page: number = 0, size: number = 20): Observable<UserResponse[]> {
    return this.repository.list(search, page, size).pipe(
      map((response: PaginatedUsers) => response.items)
    );
  }

  getUsersPaginated(search?: string, page: number = 0, size: number = 20): Observable<PaginatedUsers> {
    return this.repository.list(search, page, size);
  }
}
