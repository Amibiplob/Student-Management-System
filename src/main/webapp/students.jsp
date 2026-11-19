<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Students | CampusDesk</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
  <style>body{font-family:'DM Sans',sans-serif}h1,h2,h3{font-family:'Manrope',sans-serif}</style>
</head>
<body class="bg-slate-50 text-slate-800">
<div class="flex min-h-screen">
  <%@ include file="WEB-INF/views/includes/sidebar.jsp" %>
  <div class="min-w-0 flex-1">
    <%@ include file="WEB-INF/views/includes/topbar.jsp" %>
    <main class="mx-auto max-w-7xl p-5 sm:p-8">
      <div class="mb-7 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
        <div><p class="text-sm font-semibold text-indigo-600">DIRECTORY</p><h2 class="mt-2 text-3xl font-extrabold text-slate-900">Students</h2><p class="mt-2 text-slate-500">Manage and review student records.</p></div>
        <c:if test="${currentUser.roleName eq 'ADMIN'}"><a href="${pageContext.request.contextPath}/students?action=new" class="rounded-xl bg-indigo-600 px-5 py-3 text-center text-sm font-bold text-white hover:bg-indigo-700">＋ Add student</a></c:if>
      </div>

      <c:if test="${not empty successMessage}"><div role="status" class="mb-5 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800"><c:out value="${successMessage}" /></div></c:if>
      <c:if test="${not empty errorMessage}"><div role="alert" class="mb-5 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800"><c:out value="${errorMessage}" /></div></c:if>

      <section class="overflow-hidden rounded-2xl border border-slate-200 bg-white">
        <div class="flex flex-col gap-4 border-b border-slate-100 p-5 lg:flex-row lg:items-center lg:justify-between">
          <div><h3 class="font-extrabold text-slate-900">Student records <span class="ml-1 rounded-lg bg-slate-100 px-2 py-1 text-xs text-slate-600"><c:choose><c:when test="${databaseUnavailable}">—</c:when><c:otherwise><c:out value="${students.size()}" /></c:otherwise></c:choose></span></h3><p class="mt-1 text-xs text-slate-500">Records are loaded from PostgreSQL.</p></div>
          <form method="get" action="${pageContext.request.contextPath}/students" class="flex flex-col gap-2 sm:flex-row">
            <label class="sr-only" for="search">Search students</label>
            <input id="search" name="search" type="search" value="<c:out value='${search}' />" placeholder="Search name, email, or phone" class="rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-indigo-400">
            <label class="sr-only" for="departmentId">Filter by department</label>
            <select id="departmentId" name="departmentId" class="rounded-xl border border-slate-200 px-3 py-2.5 text-sm text-slate-700">
              <option value="">All departments</option>
              <option value="unassigned" ${selectedUnassigned ? 'selected' : ''}>Unassigned</option>
              <c:forEach var="department" items="${departments}">
                <option value="${department.id}" ${selectedDepartmentId == department.id ? 'selected' : ''}><c:out value="${department.name}" /></option>
              </c:forEach>
            </select>
            <button type="submit" class="rounded-xl bg-slate-900 px-4 py-2.5 text-sm font-bold text-white hover:bg-slate-700">Search</button>
            <a href="${pageContext.request.contextPath}/students" class="rounded-xl border border-slate-200 px-4 py-2.5 text-center text-sm font-semibold text-slate-600 hover:bg-slate-50">Clear</a>
          </form>
        </div>
        <c:choose>
          <c:when test="${empty students and databaseUnavailable}">
            <div class="px-6 py-14 text-center"><h3 class="font-extrabold text-slate-900">Student list unavailable</h3><p class="mt-2 text-sm text-slate-500">The database could not return records. Check the connection and try again.</p></div>
          </c:when>
          <c:when test="${empty students}">
            <div class="px-6 py-14 text-center"><h3 class="font-extrabold text-slate-900">No students found</h3><p class="mt-2 text-sm text-slate-500">${currentUser.roleName eq 'ADMIN' ? 'Add a student or adjust your search and department filter.' : 'Adjust your search and department filter.'}</p><c:if test="${currentUser.roleName eq 'ADMIN'}"><a class="mt-5 inline-flex rounded-xl bg-indigo-600 px-5 py-3 text-sm font-bold text-white hover:bg-indigo-700" href="${pageContext.request.contextPath}/students?action=new">Add a student</a></c:if></div>
          </c:when>
          <c:otherwise>
            <div class="overflow-x-auto"><table class="w-full min-w-[760px] text-left text-sm">
              <thead class="bg-slate-50 text-xs uppercase tracking-wider text-slate-500"><tr><th scope="col" class="px-5 py-4">Student</th><th scope="col" class="px-5 py-4">Record ID</th><th scope="col" class="px-5 py-4">Department</th><th scope="col" class="px-5 py-4">Email</th><th scope="col" class="px-5 py-4">Phone</th><th scope="col" class="px-5 py-4 text-right">Actions</th></tr></thead>
              <tbody class="divide-y divide-slate-100">
                <c:forEach var="student" items="${students}">
                  <tr>
                    <td class="px-5 py-4 font-semibold text-slate-900"><c:out value="${student.name}" /></td>
                    <td class="px-5 py-4 text-slate-500">#<c:out value="${student.id}" /></td>
                    <td class="px-5 py-4"><c:out value="${student.departmentName}" default="Unassigned" /></td>
                    <td class="px-5 py-4 text-slate-600"><c:out value="${student.email}" /></td>
                    <td class="px-5 py-4 text-slate-500"><c:out value="${student.phone}" default="—" /></td>
                    <td class="px-5 py-4"><div class="flex items-center justify-end gap-3"><a class="font-bold text-indigo-600 hover:text-indigo-800" href="${pageContext.request.contextPath}/students?action=view&amp;id=${student.id}">View</a><c:if test="${currentUser.roleName eq 'ADMIN'}"><a class="font-bold text-slate-600 hover:text-slate-900" href="${pageContext.request.contextPath}/students?action=edit&amp;id=${student.id}">Edit</a><form method="post" action="${pageContext.request.contextPath}/students" onsubmit="return confirm('Delete this student record?');"><input type="hidden" name="_csrf" value="<c:out value='${csrfToken}' />"><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${student.id}"><button type="submit" class="font-bold text-rose-600 hover:text-rose-800">Delete</button></form></c:if></div></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table></div>
          </c:otherwise>
        </c:choose>
      </section>
    </main>
    <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System</footer>
  </div>
</div>
</body></html>
