package us.dtaylor.todoservice.application.rest.request;


public record CreateTodoRequest(String title, String description, boolean completed, String userId) {
}
