<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %><%@ include file="_head.jspf" %><div class="container pb-5 fade-up"><%@ include file="_flash.jspf" %>
<div class="card card-body mx-auto" style="max-width:420px"><h4>${reg?'Register':'Login'}</h4>
<form method="post" action="${ctx}/app/${reg?'register':'login'}"><input type="hidden" name="_t" value="${csrf}">
<c:if test="${reg}"><input name="name" class="form-control mb-2" placeholder="Nama" value="<c:out value='${param.name}'/>" required></c:if>
<input name="email" type="email" class="form-control mb-2" placeholder="Email" value="<c:out value='${param.email}'/>" required>
<input name="password" type="password" class="form-control mb-2" placeholder="Password" required>
<c:if test="${reg}"><input name="password_confirmation" type="password" class="form-control mb-2" placeholder="Ulangi password" required></c:if>
<button class="btn btn-primary w-100">${reg?'Daftar':'Masuk'}</button></form>
<a class="mt-2" href="${ctx}/app/${reg?'login':'register'}">${reg?'Sudah punya akun? Login':'Belum punya akun? Register'}</a></div></div>
<%@ include file="_foot.jspf" %>
