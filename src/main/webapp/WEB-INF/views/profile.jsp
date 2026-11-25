<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %><%@ include file="_head.jspf" %><div class="container pb-5 fade-up"><%@ include file="_flash.jspf" %>
<h4>Edit Profil</h4><form method="post" action="${ctx}/app/profile" class="card card-body" style="max-width:480px"><input type="hidden" name="_t" value="${csrf}">
<input name="name" class="form-control mb-2" value="<c:out value='${me.name}'/>" required><input name="email" type="email" class="form-control mb-2" value="<c:out value='${me.email}'/>" required>
<input name="password" type="password" class="form-control mb-2" placeholder="Password baru (kosongkan jika tidak diganti)"><input name="password_confirmation" type="password" class="form-control mb-2" placeholder="Ulangi password baru">
<button class="btn btn-primary">Simpan</button></form></div>
<%@ include file="_foot.jspf" %>
