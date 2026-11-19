<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Edit student | CampusDesk</title><script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
  <style>body{font-family:'DM Sans',sans-serif}h1,h2,h3{font-family:'Manrope',sans-serif}</style>
</head>
<body class="bg-slate-50 text-slate-800"><div class="flex min-h-screen">
  <%@ include file="WEB-INF/views/includes/sidebar.jsp" %>
  <div class="min-w-0 flex-1"><%@ include file="WEB-INF/views/includes/topbar.jsp" %>
    <main class="mx-auto max-w-5xl p-5 sm:p-8">
      <div class="mb-7"><a href="${pageContext.request.contextPath}/students" class="text-sm font-bold text-indigo-600">← Back to students</a><p class="mt-6 text-sm font-semibold text-indigo-600">STUDENT RECORD #<c:out value="${student.id}" /></p><h2 class="mt-2 text-3xl font-extrabold text-slate-900">Edit student</h2><p class="mt-2 text-slate-500">Update the student's contact and department information.</p></div>
      <c:if test="${not empty errorMessage}"><div role="alert" class="mb-5 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800"><c:out value="${errorMessage}" /></div></c:if>
      <form action="${pageContext.request.contextPath}/students" method="post" class="rounded-2xl border border-slate-200 bg-white p-6 sm:p-8">
        <input type="hidden" name="_csrf" value="<c:out value='${csrfToken}' />">
        <input type="hidden" name="action" value="update"><input type="hidden" name="id" value="${student.id}">
        <div class="mb-6"><h3 class="font-extrabold text-slate-900">Personal information</h3><p class="mt-1 text-xs text-slate-500">Fields marked required must be completed.</p></div>
        <div class="grid gap-5 md:grid-cols-2">
          <div><label for="name" class="mb-2 block text-sm font-semibold">Full name <span class="text-rose-600">*</span></label><input id="name" name="name" type="text" maxlength="100" required value="<c:out value='${student.name}' />" class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400 focus:ring-4 focus:ring-indigo-50"></div>
          <div><label for="email" class="mb-2 block text-sm font-semibold">Email address <span class="text-rose-600">*</span></label><input id="email" name="email" type="email" maxlength="150" required value="<c:out value='${student.email}' />" class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400 focus:ring-4 focus:ring-indigo-50"></div>
          <div><label for="phone" class="mb-2 block text-sm font-semibold">Phone number</label><input id="phone" name="phone" type="tel" maxlength="20" value="<c:out value='${student.phone}' />" class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400"></div>
          <div><label for="gender" class="mb-2 block text-sm font-semibold">Gender</label><select id="gender" name="gender" class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3"><option value="">Prefer not to say</option><option value="Male" ${student.gender == 'Male' ? 'selected' : ''}>Male</option><option value="Female" ${student.gender == 'Female' ? 'selected' : ''}>Female</option><option value="Other" ${student.gender == 'Other' ? 'selected' : ''}>Other</option></select></div>
          <div><label for="dateOfBirth" class="mb-2 block text-sm font-semibold">Date of birth</label><input id="dateOfBirth" name="dateOfBirth" type="date" value="<c:out value='${student.dateOfBirth}' />" class="w-full rounded-xl border border-slate-200 px-4 py-3"></div>
          <div><label for="departmentId" class="mb-2 block text-sm font-semibold">Department</label><select id="departmentId" name="departmentId" class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3"><option value="" ${empty student.departmentId ? 'selected' : ''}>Unassigned</option><c:forEach var="department" items="${departments}"><option value="${department.id}" ${student.departmentId == department.id ? 'selected' : ''}><c:out value="${department.name}" /></option></c:forEach></select></div>
          <div class="md:col-span-2"><label for="address" class="mb-2 block text-sm font-semibold">Address</label><textarea id="address" name="address" rows="3" class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400"><c:out value="${student.address}" /></textarea></div>
        </div>
        <div class="mt-8 flex flex-col-reverse justify-end gap-3 border-t border-slate-100 pt-6 sm:flex-row"><a href="${pageContext.request.contextPath}/students" class="rounded-xl border border-slate-200 px-5 py-3 text-center text-sm font-bold text-slate-600 hover:bg-slate-50">Cancel</a><button type="submit" class="rounded-xl bg-indigo-600 px-6 py-3 text-sm font-bold text-white hover:bg-indigo-700">Save changes</button></div>
      </form>
    </main>
    <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System</footer>
  </div>
</div></body></html>
