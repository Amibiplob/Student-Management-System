<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Student details | CampusDesk</title><script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
  <style>body{font-family:'DM Sans',sans-serif}h1,h2,h3{font-family:'Manrope',sans-serif}</style>
</head>
<body class="bg-slate-50 text-slate-800"><div class="flex min-h-screen">
  <%@ include file="WEB-INF/views/includes/sidebar.jsp" %>
  <div class="min-w-0 flex-1"><%@ include file="WEB-INF/views/includes/topbar.jsp" %>
    <main class="mx-auto max-w-5xl p-5 sm:p-8">
      <div class="mb-7"><a href="${pageContext.request.contextPath}${currentUser.roleName eq 'STUDENT' ? '/dashboard' : '/students'}" class="text-sm font-bold text-indigo-600"><c:choose><c:when test="${currentUser.roleName eq 'STUDENT'}">← Back to my dashboard</c:when><c:otherwise>← Back to students</c:otherwise></c:choose></a></div>
      <c:if test="${not empty errorMessage}"><div role="alert" class="mb-5 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800"><c:out value="${errorMessage}" /></div></c:if>
      <c:if test="${not empty student}">
        <section class="overflow-hidden rounded-2xl border border-slate-200 bg-white">
          <div class="bg-gradient-to-r from-indigo-600 to-violet-600 px-7 py-9 text-white"><div class="flex flex-col gap-5 sm:flex-row sm:items-center"><div class="grid h-20 w-20 place-items-center rounded-2xl bg-white/20 text-2xl font-extrabold" aria-hidden="true">#<c:out value="${student.id}" /></div><div class="flex-1"><p class="text-sm font-semibold text-indigo-100">STUDENT RECORD #<c:out value="${student.id}" /></p><h2 class="mt-1 text-3xl font-extrabold"><c:out value="${student.name}" /></h2><p class="mt-2 text-sm text-indigo-100"><c:out value="${student.departmentName}" default="Unassigned department" /></p></div><c:if test="${currentUser.roleName eq 'ADMIN'}"><a href="${pageContext.request.contextPath}/students?action=edit&amp;id=${student.id}" class="rounded-xl bg-white px-4 py-2.5 text-center text-sm font-bold text-indigo-700">Edit profile</a></c:if></div></div>
          <div class="grid gap-8 p-7 md:grid-cols-2">
            <div><h3 class="font-extrabold">Contact information</h3><dl class="mt-5 space-y-4 text-sm"><div><dt class="text-slate-500">Email address</dt><dd class="mt-1 font-semibold"><c:out value="${student.email}" /></dd></div><div><dt class="text-slate-500">Phone number</dt><dd class="mt-1 font-semibold"><c:out value="${student.phone}" default="Not provided" /></dd></div><div><dt class="text-slate-500">Address</dt><dd class="mt-1 whitespace-pre-line font-semibold"><c:out value="${student.address}" default="Not provided" /></dd></div></dl></div>
            <div><h3 class="font-extrabold">Academic information</h3><dl class="mt-5 space-y-4 text-sm"><div><dt class="text-slate-500">Department</dt><dd class="mt-1 font-semibold"><c:out value="${student.departmentName}" default="Unassigned" /></dd></div><div><dt class="text-slate-500">Date of birth</dt><dd class="mt-1 font-semibold"><c:out value="${student.dateOfBirth}" default="Not provided" /></dd></div><div><dt class="text-slate-500">Gender</dt><dd class="mt-1 font-semibold"><c:out value="${student.gender}" default="Not provided" /></dd></div><div><dt class="text-slate-500">Record created</dt><dd class="mt-1 font-semibold"><c:choose><c:when test="${not empty student.createdAt}"><fmt:formatDate value="${student.createdAt}" pattern="d MMM yyyy" /></c:when><c:otherwise>Not available</c:otherwise></c:choose></dd></div></dl></div>
          </div>
        </section>
      </c:if>
    </main>
    <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System</footer>
  </div>
</div></body></html>
