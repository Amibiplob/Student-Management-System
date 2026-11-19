<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dashboard | CampusDesk</title>
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

      <div class="mb-8 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
        <div><p class="text-sm font-semibold text-indigo-600">MONDAY, NOVEMBER 2026</p><h2 class="mt-2 text-3xl font-extrabold tracking-tight text-slate-900">Good morning, Admin 👋</h2><p class="mt-2 text-slate-500">Here’s what’s happening with your students today.</p></div>
        <a href="add-student.jsp" class="inline-flex items-center justify-center rounded-xl bg-indigo-600 px-5 py-3 text-sm font-bold text-white shadow-lg shadow-indigo-200 hover:bg-indigo-700">＋ Add new student</a>
      </div>
      <section class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <div class="rounded-2xl border border-slate-200 bg-white p-5"><p class="text-sm font-semibold text-slate-500">Total students</p><p class="mt-3 text-3xl font-extrabold text-slate-900">1,248</p><p class="mt-2 text-xs font-semibold text-emerald-600">↑ 8.2% <span class="font-normal text-slate-400">from last month</span></p></div>
        <div class="rounded-2xl border border-slate-200 bg-white p-5"><p class="text-sm font-semibold text-slate-500">Departments</p><p class="mt-3 text-3xl font-extrabold text-slate-900">08</p><p class="mt-2 text-xs text-slate-400">Active departments</p></div>
        <div class="rounded-2xl border border-slate-200 bg-white p-5"><p class="text-sm font-semibold text-slate-500">New this month</p><p class="mt-3 text-3xl font-extrabold text-slate-900">64</p><p class="mt-2 text-xs font-semibold text-emerald-600">↑ 12 new this week</p></div>
        <div class="rounded-2xl border border-slate-200 bg-white p-5"><p class="text-sm font-semibold text-slate-500">Pending review</p><p class="mt-3 text-3xl font-extrabold text-slate-900">12</p><p class="mt-2 text-xs text-amber-600">Requires attention</p></div>
      </section>
      <section class="mt-8 grid gap-6 xl:grid-cols-3">
        <div class="overflow-hidden rounded-2xl border border-slate-200 bg-white xl:col-span-2">
          <div class="flex items-center justify-between border-b border-slate-100 p-5"><div><h3 class="font-extrabold text-slate-900">Recently added students</h3><p class="mt-1 text-xs text-slate-400">A quick look at the latest records</p></div><a href="students.jsp" class="text-sm font-bold text-indigo-600 hover:text-indigo-800">View all →</a></div>
          <div class="overflow-x-auto"><table class="w-full text-left text-sm"><thead class="bg-slate-50 text-xs uppercase tracking-wider text-slate-400"><tr><th class="px-5 py-3">Student</th><th class="px-5 py-3">ID</th><th class="px-5 py-3">Department</th><th class="px-5 py-3">Status</th></tr></thead><tbody class="divide-y divide-slate-100">
            <tr><td class="px-5 py-4 font-semibold">Ayesha Rahman</td><td class="px-5 py-4 text-slate-500">STU-1024</td><td class="px-5 py-4 text-slate-500">Computer Science</td><td class="px-5 py-4"><span class="rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-700">Active</span></td></tr>
            <tr><td class="px-5 py-4 font-semibold">Tanvir Hasan</td><td class="px-5 py-4 text-slate-500">STU-1023</td><td class="px-5 py-4 text-slate-500">Business</td><td class="px-5 py-4"><span class="rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-700">Active</span></td></tr>
            <tr><td class="px-5 py-4 font-semibold">Nusrat Jahan</td><td class="px-5 py-4 text-slate-500">STU-1022</td><td class="px-5 py-4 text-slate-500">English</td><td class="px-5 py-4"><span class="rounded-full bg-amber-50 px-3 py-1 text-xs font-bold text-amber-700">Pending</span></td></tr>
          </tbody></table></div>
        </div>
        <div class="rounded-2xl border border-slate-200 bg-white p-5"><h3 class="font-extrabold text-slate-900">Quick actions</h3><p class="mt-1 text-xs text-slate-400">Common tasks</p><div class="mt-5 space-y-3"><a href="add-student.jsp" class="flex items-center gap-3 rounded-xl border border-slate-200 p-4 hover:border-indigo-200 hover:bg-indigo-50"><span class="grid h-10 w-10 place-items-center rounded-xl bg-indigo-100 text-indigo-700">＋</span><span><b class="block text-sm">Register student</b><small class="text-slate-400">Create a new record</small></span></a><a href="students.jsp" class="flex items-center gap-3 rounded-xl border border-slate-200 p-4 hover:border-indigo-200 hover:bg-indigo-50"><span class="grid h-10 w-10 place-items-center rounded-xl bg-sky-100 text-sky-700">⌕</span><span><b class="block text-sm">Find a student</b><small class="text-slate-400">Search existing records</small></span></a><a href="departments.jsp" class="flex items-center gap-3 rounded-xl border border-slate-200 p-4 hover:border-indigo-200 hover:bg-indigo-50"><span class="grid h-10 w-10 place-items-center rounded-xl bg-violet-100 text-violet-700">▤</span><span><b class="block text-sm">Departments</b><small class="text-slate-400">View department list</small></span></a></div></div>
      </section>

    </main>
    <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System</footer>
  </div>
</div>
</body></html>
