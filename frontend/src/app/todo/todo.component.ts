import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TodoService} from "../todo.service";
import {CardModule} from "primeng/card";
import {FormsModule} from "@angular/forms";
import {ButtonModule} from "primeng/button";
import {TableModule} from "primeng/table";
import {CheckboxChangeEvent, CheckboxModule} from "primeng/checkbox";
import {Todo} from "../domain/todo";
import {InputTextModule} from "primeng/inputtext";

@Component({
  selector: 'app-todo',
  standalone: true,
  imports: [CommonModule, CardModule, FormsModule, ButtonModule, TableModule, CheckboxModule, InputTextModule],
  templateUrl: './todo.component.html',
  styleUrl: './todo.component.css'
})
export class TodoComponent {
  todoService = inject(TodoService);
  todos: Todo[] = [];
  task: string = '';

  async ngOnInit() {
    this.todos = await this.todoService.getTodos();
  }
  addTodo() {
    alert('not implemented yet')
  }

  updateTodo($event: CheckboxChangeEvent, todo: any) {
    alert('not implemented yet');
  }

  deleteTodo($event: MouseEvent, id: any) {
    alert('not implemented yet')
  }


}
