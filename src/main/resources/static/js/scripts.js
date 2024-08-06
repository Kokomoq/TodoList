let currentPage = 0; // Numer bieżącej strony
const pageSize = 10; // Rozmiar strony

function fetchTasks(page = 0) {
    currentPage = page; // Zaktualizuj bieżącą stronę
    const filterDescription = document.getElementById('filterDescription').value;
    const sortOrder = document.getElementById('sortOrder').value;

    const queryParams = new URLSearchParams({
        description: filterDescription,
        page: currentPage,
        size: pageSize,
        sort: sortOrder,
    });

    fetch(`/api/tasks?${queryParams.toString()}`)
        .then(response => response.json())
        .then(pageData => {
            const tasks = pageData.content; // Pobranie właściwej tablicy zadań
            const totalPages = pageData.totalPages; // Pobranie liczby stron

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

            // Zaktualizuj widoczność przycisków nawigacyjnych
            document.getElementById('prevPage').style.display = currentPage > 0 ? 'block' : 'none';
            document.getElementById('nextPage').style.display = currentPage < totalPages - 1 ? 'block' : 'none';
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
            fetchTasks(currentPage); // Pobierz aktualną stronę po dodaniu zadania
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
            fetchTasks(currentPage); // Pobierz aktualną stronę po usunięciu zadania
        } else {
            response.text().then(text => {
                console.error('Błąd podczas usuwania zadania:', text);
            });
        }
    })
    .catch(error => console.error('Błąd podczas usuwania zadania:', error));
}

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

// Inicjalizacja strony
document.addEventListener('DOMContentLoaded', () => {
    fetchTasks(); // Pobierz pierwszą stronę zadań

    const taskForm = document.getElementById('taskForm');
    taskForm.onsubmit = addTask;

    const filterForm = document.getElementById('filterForm');
    filterForm.onsubmit = (event) => {
        event.preventDefault();
        fetchTasks(); // Przeładuj z filtrem
    };

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

    // Obsługuje przyciski nawigacji
    document.getElementById('prevPage').onclick = () => fetchTasks(currentPage - 1);
    document.getElementById('nextPage').onclick = () => fetchTasks(currentPage + 1);
});
