<aside class="hidden w-64 shrink-0 border-r border-slate-200 bg-white lg:flex lg:flex-col">
  <a href="${pageContext.request.contextPath}/dashboard" class="flex h-20 items-center gap-3 border-b border-slate-100 px-6">
    <span class="grid h-10 w-10 place-items-center rounded-xl bg-indigo-600 font-extrabold text-white">C</span>
    <span class="text-lg font-extrabold tracking-tight text-slate-900">CampusDesk</span>
  </a>
  <div class="px-4 py-6">
    <p class="px-3 text-[11px] font-bold uppercase tracking-[.18em] text-slate-400">Workspace</p>
    <nav aria-label="Main navigation" class="mt-3 space-y-1">
      <a href="${pageContext.request.contextPath}/dashboard" class="flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-semibold text-slate-600 hover:bg-slate-50"><span aria-hidden="true">▦</span><c:choose><c:when test="${currentUser.roleName eq 'STUDENT'}">My dashboard</c:when><c:otherwise>Overview</c:otherwise></c:choose></a>
      <c:if test="${currentUser.roleName ne 'STUDENT'}">
        <a href="${pageContext.request.contextPath}/students" class="flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-semibold text-slate-600 hover:bg-slate-50"><span aria-hidden="true">♙</span> Students</a>
        <a href="${pageContext.request.contextPath}/departments" class="flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-semibold text-slate-600 hover:bg-slate-50"><span aria-hidden="true">▤</span> Departments</a>
      </c:if>
      <c:if test="${currentUser.roleName eq 'ADMIN'}"><a href="${pageContext.request.contextPath}/users" class="flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-semibold text-slate-600 hover:bg-slate-50"><span aria-hidden="true">♙</span> User accounts</a></c:if>
    </nav>
  </div>
  <div class="mt-auto p-4"><div class="rounded-2xl bg-slate-50 p-4"><p class="text-sm font-bold text-slate-800"><c:out value="${currentUser.fullName}" /></p><p class="mt-1 text-xs font-semibold uppercase tracking-wide text-indigo-600"><c:out value="${currentUser.roleName}" /></p></div></div>
</aside>
