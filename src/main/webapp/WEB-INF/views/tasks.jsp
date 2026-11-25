<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %><%@ include file="_head.jspf" %><div class="container pb-5 fade-up"><%@ include file="_flash.jspf" %>
<div class="d-flex justify-content-between"><h4>${hist?'Riwayat Tugas Selesai':'Daftar Tugas'}</h4><c:if test="${!hist}"><a class="btn btn-primary" href="${ctx}/app/tasks/form">+ Tugas</a></c:if></div>
<form class="row g-2 my-3"><div class="col"><input name="q" value="<c:out value='${param.q}'/>" class="form-control" placeholder="Cari judul"></div>
<div class="col"><select name="course" class="form-select"><option value="">Semua MK</option><c:forEach var="c" items="${courses}"><option value="${c.id}" ${param.course==c.id?'selected':''}><c:out value="${c.name}"/></option></c:forEach></select></div>
<div class="col"><select name="tag" class="form-select"><option value="">Semua tag</option><c:forEach var="g" items="${tags}"><option value="${g.id}" ${param.tag==g.id?'selected':''}><c:out value="${g.name}"/></option></c:forEach></select></div>
<div class="col"><select name="priority" class="form-select"><option value="">Semua prioritas</option><c:forEach var="p" items="low,medium,high"><option ${param.priority==p?'selected':''}>${p}</option></c:forEach></select></div>
<div class="col-auto"><button class="btn btn-secondary">Filter</button></div></form>
<%@ include file="_tasklist.jspf" %></div>
<%@ include file="_foot.jspf" %>
