// Pobieranie zadań z serwera i wyświetlanie na stronie
function fetchTasks() {
    fetch('/api/tasks')
        .then(response => response.json())
        .then(tasks => {
            const taskList = document.getElementById('taskList');
            taskList.innerHTML = '';
            tasks.forEach(task => {
                const li = document.createElement('li');
                li.textContent = task.description;
                const button = document.createElement('button');
                button.textContent = 'Usuń';
                button.className = 'delete-button';
                button.setAttribute('data-task-id', task.id); // Ustawienie atrybutu data-task-id z id zadania
                button.onclick = () => deleteTask(task.id); // Wywołanie funkcji deleteTask z id zadania
                li.appendChild(button);
                taskList.appendChild(li);
            });
        })
        .catch(error => console.error('Błąd podczas pobierania zadań:', error));
}

// Dodawanie nowego zadania
function addTask(event) {
    event.preventDefault();
    const taskInput = document.getElementById('taskDescription');
    const description = taskInput.value;
    if (description.trim() === '') {
        alert('Wprowadź zadanie');
        return;
    }
    fetch('/api/tasks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ description })
    })
    .then(response => {
        if (response.ok) {
            taskInput.value = '';
            fetchTasks();
        } else {
            response.text().then(text => {
                console.error('Błąd podczas dodawania zadania:', text);
            });
        }
    })
    .catch(error => console.error('Błąd podczas dodawania zadania:', error));
}

// Usuwanie zadania
function deleteTask(id) {
    fetch(`/api/tasks/${id}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            fetchTasks();
        } else {
            response.text().then(text => {
                console.error('Błąd podczas usuwania zadania:', text);
            });
        }
    })
    .catch(error => console.error('Błąd podczas usuwania zadania:', error));
}

// Inicjalizacja strony
document.addEventListener('DOMContentLoaded', () => {
    fetchTasks();
    const taskForm = document.getElementById('taskForm');
    taskForm.onsubmit = addTask;
});
