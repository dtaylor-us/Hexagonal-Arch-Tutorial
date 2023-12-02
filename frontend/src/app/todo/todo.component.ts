import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TodoService} from "../todo.service";
import {CardModule} from "primeng/card";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {ButtonModule} from "primeng/button";
import {TableModule} from "primeng/table";
import {CheckboxChangeEvent, CheckboxModule} from "primeng/checkbox";
import {Todo} from "../domain/todo";
import {InputTextModule} from "primeng/inputtext";
import {InputTextareaModule} from "primeng/inputtextarea";
import {DividerModule} from "primeng/divider";

@Component({
  selector: 'app-todo',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    FormsModule,
    ButtonModule,
    TableModule,
    CheckboxModule,
    InputTextModule,
    ReactiveFormsModule,
    InputTextareaModule,
    DividerModule
  ],
  templateUrl: './todo.component.html',
  styleUrl: './todo.component.css'
})
export class TodoComponent {
  todoService = inject(TodoService);
  todos: Todo[] = [];
  todoForm: FormGroup;

  async ngOnInit() {
    this.getTodos();
  }

  private getTodos() {
    this.todoService.getTodos().subscribe(data => {
      this.todos = data;
    });
  }

  constructor(private formBuilder: FormBuilder) {
    this.todoForm = this.formBuilder.group({
      title: ['', Validators.required],
      description: [''],
      complete: [false],
      userId: [localStorage.getItem("DEFAULT_USER_ID")]
    });
  }

  addTodo() {
    this.todoService.addTodo(this.todoForm.value).subscribe(
      () => {
        this.getTodos();
        this.todoForm.reset();
      }
    )
  }

  getTodoById(id: number) {
    this.todoService.getById(id);
  }

  updateTodo($event: CheckboxChangeEvent, todo: any) {
    todo.complete = $event.checked;
    this.todoService.updateTodo(todo).subscribe(() => {
        this.getTodos();
      }
    )
  }

  deleteTodo($event: MouseEvent, id: any) {
    this.todoService.delete(id).subscribe(() => {
        this.getTodos();
      }
    )
  }


}
