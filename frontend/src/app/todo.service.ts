import {Injectable} from '@angular/core';
import {Todo} from "./domain/todo";
import {environment} from "../environments/environments";
import {Observable, retry} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class TodoService {

  constructor(private httpClient: HttpClient) {
  }


  getTodos(): Observable<Todo[]> {
    return this.httpClient.get<Todo[]>(`${environment.api}`)
  }

   addTodo(todo: Todo): Observable<Todo> {
    return this.httpClient.post<Todo>(`${environment.api}`, todo)

  }
   getById(id: number): Observable<Todo> {
    return this.httpClient.get<Todo>(`${environment.api}/${id}`)
  }

  updateTodo(param: any) {
    return this.httpClient.put<Todo>(`${environment.api}/${param.id}`, param)
  }

  delete(id: any): Observable<Todo> {
    return this.httpClient.delete<Todo>(`${environment.api}/${id}`)
  }
}
