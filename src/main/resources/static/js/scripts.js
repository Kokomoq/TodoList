// scripts.js

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
                button.onclick = () => deleteTask(task.id);
                li.appendChild(button);
                taskList.appendChild(li);
            });
        });
}

function addTask() {
    const taskInput = document.getElementById('taskInput');
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
    .then(() => {
        taskInput.value = '';
        fetchTasks();
    });
}

function deleteTask(id) {
    fetch(`/api/tasks/${id}`, {
        method: 'DELETE'
    })
    .then(fetchTasks);
}

fetchTasks();
