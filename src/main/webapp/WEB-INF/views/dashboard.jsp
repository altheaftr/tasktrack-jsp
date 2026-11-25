<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %><%@ include file="_head.jspf" %><div class="container pb-5 fade-up"><%@ include file="_flash.jspf" %>
<h2 class="mb-3"><span>Halo,</span> <c:out value="${sessionScope.uname}"/> 👋</h2>
<div class="row g-4"><div class="col-lg-8">
<div class="row g-3 mb-3"><c:forEach var="e" items="${stats}"><div class="col-4"><div class="card card-body lift text-center"><div class="fs-2 fw-bold" style="color:var(--acc)">${e.value}</div><small>${e.key}</small></div></div></c:forEach></div>
<div class="card card-body mb-3"><div class="d-flex justify-content-between"><small>Progres selesai</small><b>${pct}%</b></div><div class="progress mt-1" style="height:10px"><div class="progress-bar" style="width:${pct}%;background:var(--acc)"></div></div></div>
<div class="card card-body mb-4"><h6>Tambah Cepat</h6>
<c:choose><c:when test="${not empty courses}"><form method="post" action="${ctx}/app/tasks/save" class="row g-2"><input type="hidden" name="_t" value="${csrf}"><input type="hidden" name="priority" value="medium"><input type="hidden" name="status" value="todo">
<div class="col-md-4"><input name="title" class="form-control" placeholder="Judul tugas" required></div>
<div class="col-md-3"><select name="course_id" class="form-select"><c:forEach var="c" items="${courses}"><option value="${c.id}"><c:out value="${c.name}"/></option></c:forEach></select></div>
<div class="col-md-3"><input type="datetime-local" name="deadline" class="form-control" required></div><div class="col-md-2"><button class="btn btn-primary w-100">Tambah</button></div></form></c:when>
<c:otherwise><a href="${ctx}/app/courses">Tambahkan mata kuliah dulu →</a></c:otherwise></c:choose></div>
<h5 class="text-danger">Terlambat</h5><c:set var="rows" value="${late}"/><%@ include file="_tasklist.jspf" %>
<h5 class="mt-4">Deadline 3 hari ke depan</h5><c:set var="rows" value="${soon}"/><%@ include file="_tasklist.jspf" %></div>
<div class="col-lg-4"><div class="card card-body mb-3"><h6>Deadline Terdekat</h6>
<c:choose><c:when test="${not empty next}"><div class="fw-bold"><c:out value="${next.title}"/></div><div class="fs-2 fw-bold" style="color:var(--acc)" id="cd" data-t="${next.iso}">--</div></c:when>
<c:otherwise><p class="mb-0">Tidak ada deadline mendatang 🎉</p></c:otherwise></c:choose></div>
<div class="card card-body text-center"><h6>Fokus Pomodoro</h6><div class="display-4 fw-bold" id="pt">25:00</div>
<div class="d-flex justify-content-center gap-2 my-2"><button class="btn btn-sm btn-soft" data-m="25">25</button><button class="btn btn-sm btn-soft" data-m="5">5</button><button class="btn btn-sm btn-soft" data-m="15">15</button></div>
<div class="d-flex justify-content-center gap-2"><button class="btn btn-ink" id="ps">Mulai</button><button class="btn btn-soft" id="pr">Reset</button></div></div></div></div></div>
<%@ include file="_foot.jspf" %>
