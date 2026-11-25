<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %><%@ include file="_head.jspf" %><div class="container pb-5 fade-up"><%@ include file="_flash.jspf" %>
<c:set var="pr" value="${empty task?'medium':task.priority}"/><c:set var="stt" value="${empty task?'todo':task.status}"/>
<h4>${empty task?'Tambah Tugas':'Edit Tugas'}</h4>
<form method="post" action="${ctx}/app/tasks/save" class="card card-body"><input type="hidden" name="_t" value="${csrf}"><c:if test="${not empty task}"><input type="hidden" name="id" value="${task.id}"></c:if>
<label>Judul</label><input name="title" class="form-control mb-2" required value="<c:out value='${task.title}'/>">
<label>Mata kuliah</label><select name="course_id" class="form-select mb-2"><c:forEach var="c" items="${courses}"><option value="${c.id}" ${task.course_id==c.id?'selected':''}><c:out value="${c.name}"/></option></c:forEach></select>
<label>Deadline</label><input type="datetime-local" name="deadline" class="form-control mb-2" required value="${task.dlin}">
<div class="row mb-2"><div class="col"><label>Prioritas</label><select name="priority" class="form-select"><c:forEach var="p" items="low,medium,high"><option ${pr==p?'selected':''}>${p}</option></c:forEach></select></div>
<div class="col"><label>Status</label><select name="status" class="form-select"><c:forEach var="p" items="todo,doing,done"><option ${stt==p?'selected':''}>${p}</option></c:forEach></select></div></div>
<label>Tag</label><div class="mb-2"><c:forEach var="g" items="${tags}"><label class="me-3"><input type="checkbox" name="tags" value="${g.id}" ${sel.contains(g.id)?'checked':''}> <c:out value="${g.name}"/></label></c:forEach></div>
<label>Deskripsi</label><textarea name="description" class="form-control mb-3"><c:out value="${task.description}"/></textarea>
<button class="btn btn-primary">Simpan</button></form></div>
<%@ include file="_foot.jspf" %>
