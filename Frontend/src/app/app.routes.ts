import { Routes } from '@angular/router';
import { LoginComponent } from "./Pages/Login/login.component";
import { RegisterComponent } from "./Pages/registro/registro.component"
import { CampaignListComponent } from './Pages/campaigns/campaign-list/campaign-list.component';
import { CampaignFormComponent } from './Pages/campaigns/campaign-form/campaign-form.component';
import { authGuard } from './Guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'campaigns',
    component: CampaignListComponent,
    canActivate: [authGuard]
  },
  {
    path: 'campaigns/create',
    component: CampaignFormComponent,
    canActivate: [authGuard]
  },
  {
    path: 'campaigns/edit/:id',
    component: CampaignFormComponent,
    canActivate: [authGuard]
  }
];
