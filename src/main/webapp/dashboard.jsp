<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dashboard | CampusDesk</title><script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
  <style>body{font-family:'DM Sans',sans-serif}h1,h2,h3{font-family:'Manrope',sans-serif}</style>
</head>
<body class="bg-slate-50 text-slate-800"><div class="flex min-h-screen">
  <%@ include file="WEB-INF/views/includes/sidebar.jsp" %>
  <div class="min-w-0 flex-1"><%@ include file="WEB-INF/views/includes/topbar.jsp" %>
    <main class="mx-auto max-w-7xl p-5 sm:p-8">
      <div class="mb-8 flex flex-col justify-between gap-4 sm:flex-row sm:items-end"><div><p class="text-sm font-semibold text-indigo-600"><c:out value="${currentUser.roleName}" /> WORKSPACE</p><h2 class="mt-2 text-3xl font-extrabold tracking-tight text-slate-900"><c:choose><c:when test="${currentUser.roleName eq 'ADMIN'}">Admin dashboard</c:when><c:otherwise>Teacher dashboard</c:otherwise></c:choose></h2><p class="mt-2 text-slate-500"><c:choose><c:when test="${currentUser.roleName eq 'ADMIN'}">Manage student records, departments, and account access.</c:when><c:otherwise>Review student records and department information.</c:otherwise></c:choose></p></div><c:if test="${currentUser.roleName eq 'ADMIN'}"><a href="${pageContext.request.contextPath}/students?action=new" class="inline-flex items-center justify-center rounded-xl bg-indigo-600 px-5 py-3 text-sm font-bold text-white hover:bg-indigo-700">＋ Add new student</a></c:if></div>
      <c:if test="${not empty errorMessage}"><div role="alert" class="mb-5 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800"><c:out value="${errorMessage}" /></div></c:if>
      <section class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
        <div class="rounded-2xl border border-slate-200 bg-white p-5"><p class="text-sm font-semibold text-slate-500">Total students</p><p class="mt-3 text-3xl font-extrabold text-slate-900"><c:choose><c:when test="${databaseUnavailable}">—</c:when><c:otherwise><c:out value="${studentCount}" /></c:otherwise></c:choose></p><p class="mt-2 text-xs text-slate-500">Student records in PostgreSQL</p></div>
        <div class="rounded-2xl border border-slate-200 bg-white p-5"><p class="text-sm font-semibold text-slate-500">Departments</p><p class="mt-3 text-3xl font-extrabold text-slate-900"><c:choose><c:when test="${databaseUnavailable}">—</c:when><c:otherwise><c:out value="${departmentCount}" /></c:otherwise></c:choose></p><p class="mt-2 text-xs text-slate-500">Departments available for enrollment</p></div>
        <div class="rounded-2xl border border-slate-200 bg-white p-5"><p class="text-sm font-semibold text-slate-500">Quick links</p><div class="mt-4 flex flex-wrap gap-3"><a href="${pageContext.request.contextPath}/students" class="text-sm font-bold text-indigo-600">Student directory →</a><a href="${pageContext.request.contextPath}/departments" class="text-sm font-bold text-indigo-600">Departments →</a></div></div>
      </section>
      <section class="mt-8 overflow-hidden rounded-2xl border border-slate-200 bg-white">
        <div class="flex flex-col gap-2 border-b border-slate-100 p-5 sm:flex-row sm:items-center sm:justify-between"><div><h3 class="font-extrabold text-slate-900">Recently added students</h3><p class="mt-1 text-xs text-slate-500">Latest records based on created_at.</p></div><a href="${pageContext.request.contextPath}/students" class="text-sm font-bold text-indigo-600 hover:text-indigo-800">View directory →</a></div>
        <c:choose><c:when test="${databaseUnavailable}"><div class="px-6 py-12 text-center"><p class="font-bold text-slate-800">Recent records unavailable</p><p class="mt-2 text-sm text-slate-500">Reconnect to PostgreSQL to view student data.</p></div></c:when><c:when test="${empty recentStudents}"><div class="px-6 py-12 text-center"><p class="font-bold text-slate-800">No student records yet</p><p class="mt-2 text-sm text-slate-500">Add a student to start building your directory.</p></div></c:when><c:otherwise><div class="overflow-x-auto"><table class="w-full min-w-[600px] text-left text-sm"><thead class="bg-slate-50 text-xs uppercase tracking-wider text-slate-500"><tr><th scope="col" class="px-5 py-3">Student</th><th scope="col" class="px-5 py-3">Record ID</th><th scope="col" class="px-5 py-3">Department</th><th scope="col" class="px-5 py-3">Email</th></tr></thead><tbody class="divide-y divide-slate-100"><c:forEach var="student" items="${recentStudents}"><tr><td class="px-5 py-4 font-semibold"><a class="text-indigo-700 hover:underline" href="${pageContext.request.contextPath}/students?action=view&amp;id=${student.id}"><c:out value="${student.name}" /></a></td><td class="px-5 py-4 text-slate-500">#<c:out value="${student.id}" /></td><td class="px-5 py-4 text-slate-500"><c:out value="${student.departmentName}" default="Unassigned" /></td><td class="px-5 py-4 text-slate-500"><c:out value="${student.email}" /></td></tr></c:forEach></tbody></table></div></c:otherwise></c:choose>
      </section>
    </main>
    <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System</footer>
  </div>
</div></body></html>
