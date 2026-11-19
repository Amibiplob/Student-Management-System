<header class="border-b border-slate-200 bg-white px-5 sm:px-8">
  <div class="flex min-h-20 items-center justify-between gap-4">
    <div><p class="text-xs font-medium text-slate-500">CampusDesk / <c:out value="${currentUser.roleName}" /></p><h1 class="mt-1 text-lg font-extrabold tracking-tight text-slate-900">Student Management</h1></div>
    <div class="flex items-center gap-3">
      <div class="max-w-[34vw] truncate text-right sm:max-w-none"><p class="truncate text-xs font-bold text-slate-800 sm:text-sm"><c:out value="${currentUser.fullName}" /></p><p class="text-[10px] font-semibold uppercase text-indigo-600 sm:text-xs"><c:out value="${currentUser.roleName}" /></p></div>
      <form action="${pageContext.request.contextPath}/logout" method="post"><input type="hidden" name="_csrf" value="<c:out value='${csrfToken}' />"><button type="submit" class="rounded-xl border border-slate-200 px-3 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50">Sign out</button></form>
    </div>
  </div>
  <nav aria-label="Mobile navigation" class="-mx-5 flex gap-2 overflow-x-auto border-t border-slate-100 px-5 py-3 lg:hidden">
    <a href="${pageContext.request.contextPath}/dashboard" class="shrink-0 rounded-lg px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"><c:choose><c:when test="${currentUser.roleName eq 'STUDENT'}">My profile</c:when><c:otherwise>Overview</c:otherwise></c:choose></a>
    <c:if test="${currentUser.roleName ne 'STUDENT'}"><a href="${pageContext.request.contextPath}/students" class="shrink-0 rounded-lg px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100">Students</a><a href="${pageContext.request.contextPath}/departments" class="shrink-0 rounded-lg px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100">Departments</a></c:if>
    <c:if test="${currentUser.roleName eq 'ADMIN'}"><a href="${pageContext.request.contextPath}/users" class="shrink-0 rounded-lg px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100">Users</a></c:if>
  </nav>
</header>
