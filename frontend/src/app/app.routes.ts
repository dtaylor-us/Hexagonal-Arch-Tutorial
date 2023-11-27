import { Routes } from '@angular/router';
import {TodoComponent} from "./todo/todo.component";
import {UserComponent} from "./user/user.component";

export const routes: Routes = [
    {
      path: '',
      title: 'App Home Page',
      component: TodoComponent,
    },
    {
      path: 'user',
      title: 'App User Page',
      component: UserComponent,
    },
  ];
