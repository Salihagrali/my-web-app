<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>TODO List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; }
        .task-item { display: flex; align-items: center; justify-content: space-between; padding: .5rem; border-bottom: 1px solid #dee2e6; }
        .task-title { flex: 1; margin: 0 .5rem; }
    </style>
</head>
<body>
<div class="container py-4">
    <h1 class="mb-4">My TODOs</h1>
    <div class="input-group mb-3">
        <input id="new-task" type="text" class="form-control" placeholder="Add new task">
        <button id="add-btn" class="btn btn-primary">Add</button>
    </div>
    <ul id="task-list" class="list-group"></ul>
</div>
<script>
const API = '<%= request.getContextPath() %>/api/tasks';

function fetchTasks(){
    fetch(API).then(r=>r.json()).then(render);
}

function render(tasks){
    const list = document.getElementById('task-list');
    list.innerHTML = '';
    tasks.forEach(t=>{
        const li = document.createElement('li');
        li.className = 'list-group-item task-item';
        const cb = document.createElement('input');
        cb.type='checkbox'; cb.checked=t.completed;
        cb.addEventListener('change',()=>{
            updateTask(t.id,{completed:cb.checked});
        });
        const span = document.createElement('span');
        span.className='task-title';
        span.textContent=t.title;
        const edit = document.createElement('i');
        edit.className='bi bi-pencil mx-2 text-secondary';
        edit.style.cursor='pointer';
        edit.addEventListener('click',()=>{
            const nv=prompt('Edit task',t.title);
            if(nv!==null && nv.trim()!==''){
                updateTask(t.id,{title:nv});
            }
        });
        const trash = document.createElement('i');
        trash.className='bi bi-trash text-danger';
        trash.style.cursor='pointer';
        trash.addEventListener('click',()=>{
            deleteTask(t.id);
        });
        li.append(cb,span,edit,trash);
        list.append(li);
    });
}

function addTask(){
    const input=document.getElementById('new-task');
    const title=input.value.trim();
    if(!title) return;
    fetch(API,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({title})})
        .then(r=>r.json()).then(_=>{ input.value=''; fetchTasks(); });
}

function updateTask(id,changes){
    fetch(API+'/'+id,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify(changes)})
        .then(r=>{ if(r.ok) fetchTasks(); });
}

function deleteTask(id){
    fetch(API+'/'+id,{method:'DELETE'}).then(r=>{ if(r.status===204) fetchTasks(); });
}

document.getElementById('add-btn').addEventListener('click',addTask);
document.getElementById('new-task').addEventListener('keypress',e=>{ if(e.key==='Enter') addTask();});
fetchTasks();
</script>
</body>
</html>
