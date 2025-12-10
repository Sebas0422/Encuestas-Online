import { Routes } from '@angular/router';
import { LoginComponent } from "./Pages/Login/login.component";
import { RegisterComponent } from "./Pages/registro/registro.component"
import { CampaignListComponent } from './Pages/campaigns/campaign-list/campaign-list.component';
import { CampaignFormComponent } from './Pages/campaigns/campaign-form/campaign-form.component';
import { CampaignMembersComponent } from './Pages/campaigns/campaign-members/campaign-members.component';
import { authGuard } from './Guards/auth.guard';
import { guestGuard } from './Guards/guest.guard';
import { FormsListComponent } from './Pages/forms/forms-list/forms-list.component';
import { FormFormComponent } from './Pages/forms/form-form/form-form.component';
import { FormPreviewComponent } from './Pages/forms/form-preview/form-preview.component';
import { QuestionBuilderComponent } from './Pages/questions/question-builder/question-builder.component';
import { ResponsesListComponent } from './Pages/responses/reponses-list/responses-list.component'
export const routes: Routes = [
  { path: '', redirectTo: '/campaigns', pathMatch: 'full' },
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },
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
  },
  {
    path: 'campaigns/:id/members',
    component: CampaignMembersComponent,
    canActivate: [authGuard]
  },
  {
    path: 'campaigns/:id/forms',
    component: FormsListComponent,
    canActivate: [authGuard]
  },
  {
    path: 'forms/create',
    component: FormFormComponent,
    canActivate: [authGuard]
  },
  {
    path: 'forms/edit/:id',
    component: FormFormComponent,
    canActivate: [authGuard]
  },
  {
    path: 'forms/:formId/questions',
    component: QuestionBuilderComponent,
    canActivate: [authGuard]
  },
  {
    path: 'forms/:formId/preview',
    component: FormPreviewComponent,
    canActivate: [authGuard]
  }
  ,
  {
    path: 'public/forms/:token',
    component: FormPreviewComponent
  },
  {
    path: 'forms/:formId/responses',
    component: ResponsesListComponent,
    canActivate: [authGuard]
  }
];
