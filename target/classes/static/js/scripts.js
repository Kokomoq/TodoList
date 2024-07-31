// Pobieranie zadań z serwera i wyświetlanie na stronie
function fetchTasks() {
    fetch('/api/tasks')
        .then(response => response.json())
        .then(tasks => {
            const taskList = document.getElementById('taskList');
            taskList.innerHTML = '';
            tasks.forEach(task => {
                const li = document.createElement('li');
                li.className = 'list-group-item d-flex justify-content-between align-items-center';
                li.setAttribute('data-task-id', task.id);

                const taskDesc = document.createElement('span');
                taskDesc.className = 'task-desc';
                taskDesc.textContent = task.description;

                const div = document.createElement('div');

                const editButton = document.createElement('button');
                editButton.className = 'btn btn-link text-primary edit-btn';
                editButton.innerHTML = '<i class="fas fa-edit"></i>';

                const deleteButton = document.createElement('button');
                deleteButton.className = 'btn btn-link text-danger delete-btn';
                deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i>';
                deleteButton.setAttribute('data-task-id', task.id);
                deleteButton.onclick = () => deleteTask(task.id);

                div.appendChild(editButton);
                div.appendChild(deleteButton);
                li.appendChild(taskDesc);
                li.appendChild(div);
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

// Aktualizacja zadania
function updateTask(id, description) {
    return fetch(`/api/tasks/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ description })
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text); });
        }
        return response.json();
    });
}

document.addEventListener('DOMContentLoaded', function () {
    fetchTasks();

    const taskForm = document.getElementById('taskForm');
    taskForm.onsubmit = addTask;

    const taskList = document.getElementById('taskList');
    const editTaskModal = $('#editTaskModal');
    const editTaskForm = document.getElementById('editTaskForm');
    let currentTaskDescElem = null;
    let currentTaskId = null;

    taskList.addEventListener('click', function (e) {
        if (e.target.classList.contains('edit-btn') || e.target.parentElement.classList.contains('edit-btn')) {
            const taskItem = e.target.closest('.list-group-item');
            const taskDescElem = taskItem.querySelector('.task-desc');
            const taskDesc = taskDescElem.textContent.trim();

            currentTaskDescElem = taskDescElem;
            currentTaskId = taskItem.dataset.taskId;
            editTaskForm.editTaskDescription.value = taskDesc;
            editTaskModal.modal('show');
        }
    });

    editTaskForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const newDesc = editTaskForm.editTaskDescription.value.trim();
        if (newDesc) {
            updateTask(currentTaskId, newDesc)
                .then(updatedTask => {
                    currentTaskDescElem.textContent = updatedTask.description;
                    editTaskModal.modal('hide');
                })
                .catch(error => {
                    console.error('Błąd podczas aktualizacji zadania:', error);
                    alert('Nie udało się zaktualizować zadania.');
                });
        }
    });
});
