let currentPage = 0;
const pageSize = 10; // Rozmiar strony



function fetchTasks({ priority, sortBy, sortOrder, page } = { priority: '', sortBy: '', sortOrder: '', page: currentPage }) {
    const taskList = document.getElementById('task-list');
    if (!taskList) {
        console.error('Element o ID "task-list" nie istnieje.');
        return;
    }
    taskList.innerHTML = ''; // Usunięcie poprzednich zadań

    // Budowanie URL z parametrami
    let url = `/api/tasks?page=${encodeURIComponent(page)}&size=${pageSize}`;
    if (sortBy && sortOrder) {
        url += `&sort=${encodeURIComponent(sortBy)},${encodeURIComponent(sortOrder)}`;
    }
    if (priority) {
        url += `&priority=${encodeURIComponent(priority)}`;
    }

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Błąd w odpowiedzi z serwera.');
            }
            return response.json();
        })
        .then(pageData => {
            const tasks = pageData.content;

            if (tasks.length === 0) {
                taskList.innerHTML = '<li class="list-group-item">Brak zadań do wyświetlenia.</li>';
            } else {
                tasks.forEach(task => {
                    appendTaskToList(task); // Upewnij się, że ta funkcja jest zdefiniowana
                });
            }

            // Obsługa paginacji
            updatePagination(pageData);
        })
        .catch(error => console.error('Błąd podczas pobierania zadań:', error));
}

document.getElementById('addTaskForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const descriptionInput = document.getElementById('taskDescription');
    const prioritySelect = document.getElementById('taskPriority');

    const description = descriptionInput.value.trim();
    const priority = prioritySelect.value;

    if (description && priority) {
        addTask(description, priority)
            .then(newTask => {
                // Dodaj nowy element do listy zadań
                appendTaskToList(newTask);

                // Wyczyść formularz
                descriptionInput.value = '';
                prioritySelect.value = '';

                // Ponownie pobierz zadania, aby odświeżyć widok
                fetchTasks({ page: currentPage });
            })
            .catch(error => {
                console.error('Błąd podczas dodawania zadania:', error);
                alert('Nie udało się dodać zadania. Spróbuj ponownie.');
            });
    } else {
        alert('Proszę wprowadzić opis zadania i wybrać priorytet.');
    }
});

// Funkcja dodająca zadanie
function addTask(description, priority) {
    return fetch('/api/tasks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ description, priority })
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text); });
        }
        return response.json();
    });
}

// Funkcja usuwająca zadanie
function deleteTask(id) {
    fetch(`/api/tasks/${id}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            fetchTasks({ page: currentPage }); // Pobierz aktualną stronę po usunięciu zadania
        } else {
            response.text().then(text => {
                console.error('Błąd podczas usuwania zadania:', text);
            });
        }
    })
    .catch(error => console.error('Błąd podczas usuwania zadania:', error));
}

// Upewnij się, że ta funkcja jest zdefiniowana
function appendTaskToList(task) {
    const taskList = document.getElementById('task-list');
    const li = document.createElement('li');
    li.className = 'list-group-item d-flex justify-content-between align-items-center';
    li.dataset.taskId = task.id;

    const taskDesc = document.createElement('span');
    taskDesc.className = 'task-desc';
    taskDesc.textContent = task.description;

    const buttonsDiv = document.createElement('div');
    buttonsDiv.className = 'buttons-container d-flex align-items-center';

    const editButton = document.createElement('button');
    editButton.className = 'btn btn-link text-primary edit-btn';
    editButton.innerHTML = '<i class="fas fa-edit"></i>';
    editButton.setAttribute('data-toggle', 'modal');
    editButton.setAttribute('data-target', '#editTaskModal');

    const deleteButton = document.createElement('button');
    deleteButton.className = 'btn btn-link text-danger delete-btn';
    deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i>';
    deleteButton.onclick = () => deleteTask(task.id);

    buttonsDiv.appendChild(editButton);
    buttonsDiv.appendChild(deleteButton);

    const priority = document.createElement('span');
    priority.className = `priority-badge priority-${task.priority.toLowerCase()}`;
    priority.textContent = `Priorytet: ${task.priority === 'LOW' ? 'Niski' : task.priority === 'MEDIUM' ? 'Średni' : 'Wysoki'}`;

    li.appendChild(taskDesc);
    li.appendChild(buttonsDiv);
    li.appendChild(priority);

    taskList.appendChild(li);
}

// Obsługa formularza filtrowania i sortowania
document.getElementById('filterSortForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const filterPriority = document.getElementById('filterPriority').value;
    const sortBy = document.getElementById('sortBy').value;
    const sortOrder = document.getElementById('sortOrder').value;

    fetchTasks({
        priority: filterPriority,
        sortBy: sortBy,
        sortOrder: sortOrder,
        page: 0 // Resetuj stronę do pierwszej przy zmianie filtrów
    });
});

function updatePagination(pageData) {
    const prevPageBtn = document.getElementById('prevPage');
    const nextPageBtn = document.getElementById('nextPage');

    prevPageBtn.style.display = pageData.first ? 'none' : 'block';
    nextPageBtn.style.display = pageData.last ? 'none' : 'block';

    prevPageBtn.onclick = () => {
        if (currentPage > 0) {
            currentPage--;
            fetchTasks({ page: currentPage });
        }
    };

    nextPageBtn.onclick = () => {
        if (currentPage < pageData.totalPages - 1) {
            currentPage++;
            fetchTasks({ page: currentPage });
        }
    };
}

// Obsługa przycisków nawigacji
document.getElementById('prevPage').onclick = () => {
    if (currentPage > 0) {
        currentPage--;
        fetchTasks({ page: currentPage });
    }
};

document.getElementById('nextPage').onclick = () => {
    if (currentPage < pageData.totalPages - 1) {
        currentPage++;
        fetchTasks({ page: currentPage });
    }
};

// Inicjalizacja zadań przy ładowaniu strony
document.addEventListener('DOMContentLoaded', function () {
    fetchTasks({ page: currentPage }); // Pobierz pierwszą stronę zadań po załadowaniu strony
});
