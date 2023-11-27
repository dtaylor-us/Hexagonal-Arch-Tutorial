import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterOutlet, RouterLink} from '@angular/router';
import {MenuItem} from 'primeng/api';
import {MenubarModule} from "primeng/menubar";


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, MenubarModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';
  items: MenuItem[] | undefined;

  ngOnInit() {
    this.items = [
      {
        label: 'Todo',
        icon: 'pi pi-fw pi-check-square',
        routerLink: ['/']
      },

      {
        label: 'User',
        icon: 'pi pi-fw pi-user',
        href: '/users',
        routerLink: ['/user'],
        style: {'margin-left': 'auto'},
        items: [
          {
            label: 'New',
            icon: 'pi pi-fw pi-user-plus',
            routerLink: ['/users']
          },
          {
            label: 'Delete',
            icon: 'pi pi-fw pi-user-minus'
          },
          {
            icon: 'pi pi-fw pi-bars',
            label: 'List'
          }
        ]
      },
    ];
  }
}
