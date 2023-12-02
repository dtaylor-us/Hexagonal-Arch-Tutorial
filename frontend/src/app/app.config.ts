import {APP_INITIALIZER, ApplicationConfig} from '@angular/core';
import {provideRouter} from '@angular/router';
import {environment} from "../environments/environments";
import {User} from "./domain/user"
import {routes} from './app.routes';
import {HttpClient, provideHttpClient} from "@angular/common/http";
import {firstValueFrom, tap} from "rxjs";


export function initializeApp(http: HttpClient) {
  return (): Promise<any> =>
    firstValueFrom(
      http
        .get<User>(`${environment.user_api}/username/admin`)
        .pipe(tap(user => {
          // store userId from user in local Storage
          localStorage.setItem("DEFAULT_USER_ID", user.id);
        }))
    );
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [HttpClient],
      multi: true
    }
  ]
};
