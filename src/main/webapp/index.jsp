<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %><%@ include file="WEB-INF/views/_head.jspf" %>
<c:if test="${not empty sessionScope.uid}"><c:redirect url="/app/dashboard"/></c:if>
<main class="container fade-up pb-5"><h1 class="hero-title my-3">Task Track</h1>
<div class="row g-4 align-items-end mt-1">
<div class="col-lg-5"><div class="d-flex gap-2 mb-4"><span class="dot" data-c="#c0693c" style="background:#c0693c"></span><span class="dot" data-c="#3b6fd4" style="background:#3b6fd4"></span><span class="dot" data-c="#5f9a7c" style="background:#5f9a7c"></span></div>
<p class="mb-4" style="max-width:420px">Kelola deadline tugas, praktikum, dan ujianmu dalam satu tempat. Pantau yang mendesak, centang sub-tugas, dan tetap fokus dengan timer Pomodoro.</p>
<div class="d-flex gap-2 flex-wrap"><a class="btn btn-ink btn-lg" href="${ctx}/app/register">Mulai Sekarang →</a><a class="btn btn-soft btn-lg" href="${ctx}/app/login">Login</a></div></div>
<div class="col-lg-7"><div class="d-flex align-items-center gap-3"><button id="nextBtn" class="btn btn-ink rounded-circle fs-4 p-0" style="width:46px;height:46px;flex:none">›</button>
<div class="track" id="track">
<div class="fcard lift d-flex flex-column justify-content-between"><div class="fs-1">📊</div><div><h5>Dashboard Deadline</h5><small class="text-secondary">Lihat tugas terlambat dan deadline 3 hari ke depan dalam sekali pandang.</small></div></div>
<div class="fcard lift d-flex flex-column justify-content-between"><div class="fs-1">✅</div><div><h5>Checklist Sub-tugas</h5><small class="text-secondary">Pecah tugas besar jadi langkah kecil dan centang satu per satu.</small></div></div>
<div class="fcard lift d-flex flex-column justify-content-between"><div class="fs-1">🏷️</div><div><h5>Tag &amp; Filter</h5><small class="text-secondary">Kelompokkan tugas dengan tag, lalu cari dan filter secepat kilat.</small></div></div>
<div class="fcard lift d-flex flex-column justify-content-between"><div class="fs-1">🔗</div><div><h5>Catatan &amp; Link</h5><small class="text-secondary">Simpan link repo, Drive, dan referensi di setiap tugas.</small></div></div>
<div class="fcard lift d-flex flex-column justify-content-between"><div class="fs-1">⏱️</div><div><h5>Timer Pomodoro</h5><small class="text-secondary">Fokus 25 menit, istirahat 5 menit, langsung dari dashboard.</small></div></div></div></div>
<div class="d-flex align-items-center gap-3 mt-3 fw-bold fs-5"><span id="cur">01</span><div class="bar"><i id="barfill"></i></div><span>05</span></div></div></div></main>
<%@ include file="WEB-INF/views/_foot.jspf" %>
