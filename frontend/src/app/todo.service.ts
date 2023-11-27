import {Injectable} from '@angular/core';
import {Todo} from "./domain/todo";
import {environment} from "../environments/environments";

@Injectable({
  providedIn: 'root'
})
export class TodoService {

  async getTodos(): Promise<Todo[]> {
    const data = await fetch(environment.api);
    return (await data.json()) ?? [];
  }

}
