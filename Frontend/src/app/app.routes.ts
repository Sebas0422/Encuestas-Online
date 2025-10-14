import { Routes } from '@angular/router';
import {LoginComponent} from "./Pages/Login/login.component";
import {RegisterComponent} from "./Pages/registro/registro.component"

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {path: 'login',component:LoginComponent},
  {path: 'register',component:RegisterComponent}

];
