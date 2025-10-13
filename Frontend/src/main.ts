import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config'; // Este archivo contiene provideRouter
import { AppComponent } from './app/app.component';

// Quitamos el array extra de 'providers', ya que está en appConfig.
bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));
