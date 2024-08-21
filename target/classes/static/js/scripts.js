let currentPage = 0; // Numer bieżącej strony
const pageSize = 10; // Rozmiar strony

// Funkcja pobierająca zadania i aktualizująca listę na stronie
function fetchTasks(page = 0) {
    const taskList = document.getElementById('taskList');
    taskList.innerHTML = ''; // Usunięcie poprzednich zadań

    fetch(`/api/tasks?page=${page}&size=${pageSize}`)
        .then(response => response.json())
        .then(pageData => {
            const tasks = pageData.content;

            tasks.forEach(task => {
                const li = document.createElement('li');
                li.className = 'list-group-item d-flex justify-content-between align-items-center';
                li.dataset.taskId = task.id;  // Dodanie ID zadania

                const taskDesc = document.createElement('span');
                taskDesc.className = 'task-desc';
                taskDesc.textContent = task.description;

                const buttonsDiv = document.createElement('div');
                buttonsDiv.className = 'buttons-container d-flex align-items-center';

                const editButton = document.createElement('button');
                editButton.className = 'btn btn-link text-primary edit-btn';
                editButton.innerHTML = '<i class="fas fa-edit"></i>';

                const deleteButton = document.createElement('button');
                deleteButton.className = 'btn btn-link text-danger delete-btn';
                deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i>';
                deleteButton.onclick = () => deleteTask(task.id);

                buttonsDiv.appendChild(editButton);
                buttonsDiv.appendChild(deleteButton);

                const priority = document.createElement('span');
                priority.className = `priority-badge priority-${task.priority.toLowerCase()}`;
                priority.textContent = `Priorytet: ${task.priority === 'LOW' ? 'Niski' : task.priority === 'MEDIUM' ? 'Średni' : 'Wysoki'}`;

                li.appendChild(taskDesc);     // Opis zadania po lewej
                li.appendChild(buttonsDiv);   // Przyciski na prawo od opisu
                li.appendChild(priority);     // Priorytet po prawej stronie

                taskList.appendChild(li);
            });

            // Aktualizacja widoczności przycisków paginacji
            document.getElementById('prevPage').style.display = pageData.pageable.pageNumber === 0 ? 'none' : 'block';
            document.getElementById('nextPage').style.display = pageData.pageable.pageNumber >= pageData.totalPages - 1 ? 'none' : 'block';
        })
        .catch(error => console.error('Błąd podczas pobierania zadań:', error));
}


document.getElementById('filterForm').addEventListener('submit', function(event) {
    event.preventDefault();
    fetchTasks(0); // Przekazanie numeru strony jako 0, aby zacząć od pierwszej strony
});


document.getElementById('taskForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const taskInput = document.getElementById('taskDescription');
    const priorityInput = document.getElementById('taskPriority');

    const description = taskInput.value;
    const priority = priorityInput.value;

    fetch('/api/tasks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json' // Ważne: Ustawienie Content-Type na application/json
        },
        body: JSON.stringify({ description: description, priority: priority })
    })
    .then(response => {
        if (response.ok) {
            taskInput.value = '';
            priorityInput.value = 'LOW'; // Reset priorytetu po dodaniu zadania
            fetchTasks(currentPage); // Pobierz aktualną stronę po dodaniu zadania
        } else {
            response.text().then(text => {
                console.error('Błąd podczas dodawania zadania:', text);
            });
        }
    })
    .catch(error => console.error('Błąd podczas dodawania zadania:', error));
});


// Funkcja usuwająca zadanie
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

// Funkcja aktualizująca zadanie
function updateTask(id, description, priority) {
    return fetch(`/api/tasks/${id}`, {
        method: 'PUT',
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

// Inicjalizacja zmiennych dla edycji zadań
let currentTaskDescElem, currentTaskId;

// Obsługa kliknięcia przycisku edycji
document.querySelector('.task-list').addEventListener('click', function (e) {
    if (e.target.closest('.edit-btn')) {
        const taskItem = e.target.closest('.list-group-item');
        currentTaskId = taskItem.dataset.taskId;
        currentTaskDescElem = taskItem;  // Ustawienie elementu do aktualizacji

        // Zmiana sposobu pobierania tekstu zadania i priorytetu
        const taskDesc = taskItem.querySelector('.task-desc').textContent.trim();
        const taskPriority = taskItem.querySelector('.priority-badge').textContent.trim();

        document.getElementById('editTaskDescription').value = taskDesc;
        document.getElementById('editTaskPriority').value = taskPriority.toUpperCase();  // Dopasowanie tekstu do opcji w polu wyboru
        $('#editTaskModal').modal('show');
    }
});


// Obsługa zapisu zmian w formularzu edycji
document.getElementById('editTaskForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const newDesc = document.getElementById('editTaskDescription').value.trim();
    const newPriority = document.getElementById('editTaskPriority').value;

    if (newDesc) {
        updateTask(currentTaskId, newDesc, newPriority)
            .then(updatedTask => {
                if (currentTaskDescElem) {
                    const taskDescElem = currentTaskDescElem.querySelector('.task-desc');
                    const taskPriorityElem = currentTaskDescElem.querySelector('.task-priority');

                    if (taskDescElem) {
                        // Usuń stary priorytet z opisu zadania, jeśli istnieje
                        const descWithoutPriority = taskDescElem.textContent.replace(/\[\w+ Priorytet\]/g, '').trim();
                        taskDescElem.textContent = descWithoutPriority;
                    }

                    if (taskPriorityElem) {
                        if (updatedTask.priority) {
                            taskPriorityElem.textContent = `[${updatedTask.priority} Priorytet]`;
                        } else {
                            taskPriorityElem.textContent = `[Brak Priorytetu]`;
                        }
                    } else {
                        // Jeśli element taskPriorityElem nie istnieje, dodaj go
                        const newPriorityElem = document.createElement('strong');
                        newPriorityElem.className = 'task-priority';
                        newPriorityElem.style.marginLeft = '10px';
                        newPriorityElem.textContent = `[${updatedTask.priority || 'Brak'} Priorytet]`;
                        taskDescElem.appendChild(newPriorityElem);
                    }

                } else {
                    console.error('Element currentTaskDescElem nie istnieje.');
                }
                $('#editTaskModal').modal('hide');
            })
            .catch(error => {
                console.error('Błąd podczas aktualizacji zadania:', error);
                alert('Nie udało się zaktualizować zadania.');
            });
    }
});



// Obsługa przycisków nawigacji
document.getElementById('prevPage').onclick = () => {
    if (currentPage > 0) {
        currentPage -= 1;
        fetchTasks(currentPage);
    }
};

document.getElementById('nextPage').onclick = () => {
    currentPage += 1;
    fetchTasks(currentPage);
};

// Inicjalizacja zadań przy ładowaniu strony
document.addEventListener('DOMContentLoaded', function () {
    fetchTasks(); // Pobierz pierwszą stronę zadań po załadowaniu strony
});
