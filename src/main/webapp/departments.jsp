<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Departments | CampusDesk</title><script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
  <style>body{font-family:'DM Sans',sans-serif}h1,h2,h3{font-family:'Manrope',sans-serif}</style>
</head>
<body class="bg-slate-50 text-slate-800"><div class="flex min-h-screen">
  <%@ include file="WEB-INF/views/includes/sidebar.jsp" %>
  <div class="min-w-0 flex-1"><%@ include file="WEB-INF/views/includes/topbar.jsp" %>
    <main class="mx-auto max-w-7xl p-5 sm:p-8">
      <div class="mb-7"><p class="text-sm font-semibold text-indigo-600">ACADEMIC STRUCTURE</p><h2 class="mt-2 text-3xl font-extrabold text-slate-900">Departments</h2><p class="mt-2 text-slate-500"><c:choose><c:when test="${currentUser.roleName eq 'ADMIN'}">Manage departments and see their current student totals.</c:when><c:otherwise>Review departments and their current student totals.</c:otherwise></c:choose></p></div>
      <c:if test="${not empty successMessage}"><div role="status" class="mb-5 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800"><c:out value="${successMessage}" /></div></c:if>
      <c:if test="${not empty errorMessage}"><div role="alert" class="mb-5 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800"><c:out value="${errorMessage}" /></div></c:if>

      <c:if test="${currentUser.roleName eq 'ADMIN'}"><section class="mb-7 rounded-2xl border border-slate-200 bg-white p-6 sm:p-8">
        <h3 class="font-extrabold text-slate-900"><c:choose><c:when test="${editing}">Edit department</c:when><c:otherwise>Add a department</c:otherwise></c:choose></h3>
        <p class="mt-1 text-sm text-slate-500"><c:choose><c:when test="${editing}">Update the department details below.</c:when><c:otherwise>Departments are used to organize student records.</c:otherwise></c:choose></p>
        <form action="${pageContext.request.contextPath}/departments" method="post" class="mt-5 grid gap-4 md:grid-cols-2">
          <input type="hidden" name="_csrf" value="<c:out value='${csrfToken}' />">
          <input type="hidden" name="action" value="${editing ? 'update' : 'add'}"><c:if test="${editing}"><input type="hidden" name="id" value="${department.id}"></c:if>
          <div><label for="name" class="mb-2 block text-sm font-semibold">Department name <span class="text-rose-600">*</span></label><input id="name" name="name" type="text" maxlength="100" required value="<c:out value='${department.name}' />" class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400"></div>
          <div><label for="description" class="mb-2 block text-sm font-semibold">Description</label><input id="description" name="description" type="text" value="<c:out value='${department.description}' />" class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400"></div>
          <div class="flex flex-wrap gap-3 md:col-span-2"><button type="submit" class="rounded-xl bg-indigo-600 px-5 py-3 text-sm font-bold text-white hover:bg-indigo-700"><c:choose><c:when test="${editing}">Save department</c:when><c:otherwise>Add department</c:otherwise></c:choose></button><c:if test="${editing}"><a href="${pageContext.request.contextPath}/departments" class="rounded-xl border border-slate-200 px-5 py-3 text-sm font-bold text-slate-600 hover:bg-slate-50">Cancel</a></c:if></div>
        </form>
      </section></c:if>

      <c:choose>
        <c:when test="${databaseUnavailable}"><section class="rounded-2xl border border-slate-200 bg-white px-6 py-14 text-center"><h3 class="font-extrabold text-slate-900">Department list unavailable</h3><p class="mt-2 text-sm text-slate-500">Reconnect to PostgreSQL to review and manage departments.</p></section></c:when>
        <c:when test="${empty departments}"><section class="rounded-2xl border border-slate-200 bg-white px-6 py-14 text-center"><h3 class="font-extrabold text-slate-900">No departments yet</h3><p class="mt-2 text-sm text-slate-500">Add a department above before registering students.</p></section></c:when>
        <c:otherwise><section class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          <c:forEach var="item" items="${departments}">
            <article class="rounded-2xl border border-slate-200 bg-white p-6">
              <div class="grid h-12 w-12 place-items-center rounded-xl bg-indigo-100 text-xl text-indigo-700" aria-hidden="true">▤</div>
              <h3 class="mt-5 font-extrabold text-slate-900"><c:out value="${item.name}" /></h3>
              <p class="mt-1 min-h-10 text-sm text-slate-500"><c:out value="${item.description}" default="No description provided." /></p>
              <div class="mt-5 border-t border-slate-100 pt-4 text-sm"><b><c:out value="${item.studentCount}" /></b><span class="ml-2 text-slate-500">students</span><a class="ml-3 text-xs font-bold text-indigo-600 hover:text-indigo-800" href="${pageContext.request.contextPath}/students?departmentId=${item.id}">View students</a></div>
              <c:if test="${currentUser.roleName eq 'ADMIN'}"><div class="mt-4 flex items-center justify-between"><a class="text-sm font-bold text-slate-600 hover:text-slate-900" href="${pageContext.request.contextPath}/departments?action=edit&amp;id=${item.id}">Edit</a><c:choose><c:when test="${item.studentCount gt 0}"><span class="text-xs text-slate-400">Has students</span></c:when><c:otherwise><form action="${pageContext.request.contextPath}/departments" method="post" onsubmit="return confirm('Delete this department?');"><input type="hidden" name="_csrf" value="<c:out value='${csrfToken}' />"><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${item.id}"><button type="submit" class="text-sm font-bold text-rose-600 hover:text-rose-800">Delete</button></form></c:otherwise></c:choose></div></c:if>
            </article>
          </c:forEach>
        </section></c:otherwise>
      </c:choose>
    </main>
    <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System</footer>
  </div>
</div></body></html>
