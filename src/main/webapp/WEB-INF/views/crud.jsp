<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %><%@ include file="_head.jspf" %><div class="container pb-5 fade-up"><%@ include file="_flash.jspf" %>
<h4>${title}</h4>
<form method="post" action="${ctx}/app/${base}/save" class="row g-2 mb-3"><input type="hidden" name="_t" value="${csrf}">
<c:forEach var="f" items="${fields}"><div class="col"><input name="${f[0]}" type="${f[2]}" class="form-control" placeholder="${f[1]}" <c:if test="${f[2]=='color'}">value="#0d6efd"</c:if> <c:if test="${f[0]=='name'}">required</c:if>></div></c:forEach>
<div class="col-auto"><button class="btn btn-primary">Tambah</button></div></form>
<table class="table bg-white align-middle"><tr><c:forEach var="f" items="${fields}"><th>${f[1]}</th></c:forEach><th>Jml tugas</th><th></th></tr>
<c:forEach var="i" items="${items}"><tr><c:forEach var="f" items="${fields}"><td><input form="u${i.id}" name="${f[0]}" type="${f[2]}" value="<c:out value='${i[f[0]]}'/>" class="form-control form-control-sm"></td></c:forEach>
<td>${i.n}</td><td><button form="u${i.id}" class="btn btn-sm btn-success">Simpan</button> <button form="d${i.id}" class="btn btn-sm btn-danger" onclick="return confirm('Hapus?')">Hapus</button></td></tr></c:forEach></table>
<c:forEach var="i" items="${items}"><form id="u${i.id}" method="post" action="${ctx}/app/${base}/save"><input type="hidden" name="_t" value="${csrf}"><input type="hidden" name="id" value="${i.id}"></form>
<form id="d${i.id}" method="post" action="${ctx}/app/${base}/delete"><input type="hidden" name="_t" value="${csrf}"><input type="hidden" name="id" value="${i.id}"></form></c:forEach></div>
<%@ include file="_foot.jspf" %>
