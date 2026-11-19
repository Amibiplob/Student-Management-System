<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>User accounts | CampusDesk</title><script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
  <style>body{font-family:'DM Sans',sans-serif}h1,h2,h3{font-family:'Manrope',sans-serif}</style>
</head>
<body class="bg-slate-50 text-slate-800"><div class="flex min-h-screen">
  <%@ include file="WEB-INF/views/includes/sidebar.jsp" %>
  <div class="min-w-0 flex-1"><%@ include file="WEB-INF/views/includes/topbar.jsp" %>
    <main class="mx-auto max-w-7xl p-5 sm:p-8">
      <div class="mb-7"><p class="text-sm font-semibold text-indigo-600">ACCESS CONTROL</p><h2 class="mt-2 text-3xl font-extrabold text-slate-900">User accounts</h2><p class="mt-2 text-slate-500">Create accounts, assign roles, link student profiles, and deactivate access.</p></div>
      <c:if test="${not empty successMessage}"><div role="status" class="mb-5 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800"><c:out value="${successMessage}" /></div></c:if>
      <c:if test="${not empty errorMessage}"><div role="alert" class="mb-5 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800"><c:out value="${errorMessage}" /></div></c:if>

      <section class="mb-7 rounded-2xl border border-slate-200 bg-white p-6 sm:p-8">
        <h3 class="font-extrabold text-slate-900"><c:choose><c:when test="${editing}">Edit account</c:when><c:otherwise>Create an account</c:otherwise></c:choose></h3>
        <p class="mt-1 text-sm text-slate-500"><c:choose><c:when test="${editing}">Leave the password blank to keep the current password. A new value replaces it.</c:when><c:otherwise>Set an initial password for this account.</c:otherwise></c:choose></p>
        <form action="${pageContext.request.contextPath}/users" method="post" class="mt-5 grid gap-4 md:grid-cols-2">
          <input type="hidden" name="_csrf" value="<c:out value='${csrfToken}' />">
          <input type="hidden" name="action" value="${editing ? 'update' : 'create'}"><c:if test="${editing}"><input type="hidden" name="id" value="${editingUser.id}"></c:if>
          <div><label for="fullName" class="mb-2 block text-sm font-semibold">Full name <span class="text-rose-600">*</span></label><input id="fullName" name="fullName" type="text" maxlength="150" required value="<c:out value='${editingUser.fullName}' />" class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400"></div>
          <div><label for="email" class="mb-2 block text-sm font-semibold">Email address <span class="text-rose-600">*</span></label><input id="email" name="email" type="email" maxlength="150" autocomplete="username" required value="<c:out value='${editingUser.email}' />" class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400"></div>
          <div><label for="role" class="mb-2 block text-sm font-semibold">Role <span class="text-rose-600">*</span></label><select id="role" name="role" required class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3"><option value="ADMIN" ${editingUser.roleName eq 'ADMIN' ? 'selected' : ''}>ADMIN</option><option value="TEACHER" ${editingUser.roleName eq 'TEACHER' ? 'selected' : ''}>TEACHER</option><option value="STUDENT" ${editingUser.roleName eq 'STUDENT' ? 'selected' : ''}>STUDENT</option></select></div>
          <div><label for="studentId" class="mb-2 block text-sm font-semibold">Linked student profile</label><select id="studentId" name="studentId" class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3"><option value="">No student profile</option><c:forEach var="studentOption" items="${studentOptions}"><option value="${studentOption.id}" ${editingUser.studentId == studentOption.id ? 'selected' : ''}><c:out value="${studentOption.name}" /> · <c:out value="${studentOption.email}" /></option></c:forEach></select><p class="mt-1 text-xs text-slate-500">Required for STUDENT accounts; ignored for ADMIN and TEACHER roles.</p></div>
          <div class="md:col-span-2"><label for="password" class="mb-2 block text-sm font-semibold"><c:choose><c:when test="${editing}">Set a new password</c:when><c:otherwise>Initial password</c:otherwise></c:choose><c:if test="${not editing}"> <span class="text-rose-600">*</span></c:if></label><input id="password" name="password" type="password" autocomplete="new-password" ${editing ? '' : 'required'} maxlength="60" class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400"></div>
          <div class="flex flex-wrap gap-3 md:col-span-2"><button type="submit" ${databaseUnavailable ? 'disabled' : ''} class="rounded-xl bg-indigo-600 px-5 py-3 text-sm font-bold text-white hover:bg-indigo-700 disabled:opacity-50"><c:choose><c:when test="${editing}">Save account</c:when><c:otherwise>Create account</c:otherwise></c:choose></button><c:if test="${editing}"><a href="${pageContext.request.contextPath}/users" class="rounded-xl border border-slate-200 px-5 py-3 text-sm font-bold text-slate-600 hover:bg-slate-50">Cancel</a></c:if></div>
        </form>
      </section>

      <section class="overflow-hidden rounded-2xl border border-slate-200 bg-white">
        <div class="border-b border-slate-100 px-6 py-5"><h3 class="font-extrabold text-slate-900">Accounts</h3><p class="mt-1 text-sm text-slate-500">Passwords are never displayed.</p></div>
        <c:choose>
          <c:when test="${databaseUnavailable}"><div class="px-6 py-12 text-center text-sm text-slate-500">Account data is unavailable until PostgreSQL is connected and the authentication migration is applied.</div></c:when>
          <c:when test="${empty users}"><div class="px-6 py-12 text-center text-sm text-slate-500">No accounts were found.</div></c:when>
          <c:otherwise><div class="overflow-x-auto"><table class="w-full min-w-[760px] text-left text-sm"><thead class="bg-slate-50 text-xs uppercase tracking-wider text-slate-500"><tr><th scope="col" class="px-5 py-3">Account</th><th scope="col" class="px-5 py-3">Role</th><th scope="col" class="px-5 py-3">Student profile</th><th scope="col" class="px-5 py-3">Status</th><th scope="col" class="px-5 py-3 text-right">Actions</th></tr></thead><tbody class="divide-y divide-slate-100"><c:forEach var="account" items="${users}"><tr><td class="px-5 py-4"><p class="font-bold text-slate-800"><c:out value="${account.fullName}" /></p><p class="mt-1 text-xs text-slate-500"><c:out value="${account.email}" /></p></td><td class="px-5 py-4"><span class="rounded-full bg-indigo-50 px-3 py-1 text-xs font-bold text-indigo-700"><c:out value="${account.roleName}" /></span></td><td class="px-5 py-4 text-slate-600"><c:choose><c:when test="${account.roleName eq 'STUDENT'}"><c:out value="${account.studentName}" default="Linked record unavailable" /> <span class="text-xs text-slate-400">#<c:out value="${account.studentId}" /></span></c:when><c:otherwise>—</c:otherwise></c:choose></td><td class="px-5 py-4"><c:choose><c:when test="${account.active}"><span class="font-semibold text-emerald-700">Active</span></c:when><c:otherwise><span class="font-semibold text-slate-500">Inactive</span></c:otherwise></c:choose></td><td class="px-5 py-4"><div class="flex items-center justify-end gap-4"><a class="font-bold text-indigo-600 hover:text-indigo-800" href="${pageContext.request.contextPath}/users?action=edit&amp;id=${account.id}">Edit</a><form method="post" action="${pageContext.request.contextPath}/users" onsubmit="return confirm('Change this account status?');"><input type="hidden" name="_csrf" value="<c:out value='${csrfToken}' />"><input type="hidden" name="action" value="${account.active ? 'deactivate' : 'activate'}"><input type="hidden" name="id" value="${account.id}"><button type="submit" class="font-bold ${account.active ? 'text-rose-600 hover:text-rose-800' : 'text-emerald-700 hover:text-emerald-900'}"><c:choose><c:when test="${account.active}">Deactivate</c:when><c:otherwise>Reactivate</c:otherwise></c:choose></button></form></div></td></tr></c:forEach></tbody></table></div></c:otherwise>
        </c:choose>
      </section>
    </main>
    <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System</footer>
  </div>
</div></body></html>
