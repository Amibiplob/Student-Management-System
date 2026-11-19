<%@ page contentType="text/html;charset=UTF-8" %>
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

      <div class="mb-7 flex flex-col justify-between gap-4 sm:flex-row sm:items-end"><div><p class="text-sm font-semibold text-indigo-600">DIRECTORY</p><h2 class="mt-2 text-3xl font-extrabold text-slate-900">Students</h2><p class="mt-2 text-slate-500">Manage and review all registered students.</p></div><a href="add-student.jsp" class="rounded-xl bg-indigo-600 px-5 py-3 text-center text-sm font-bold text-white hover:bg-indigo-700">＋ Add student</a></div>
      <div class="rounded-2xl border border-slate-200 bg-white">
        <div class="flex flex-col gap-3 border-b border-slate-100 p-5 md:flex-row md:items-center md:justify-between"><div><h3 class="font-extrabold text-slate-900">All students <span class="ml-1 rounded-lg bg-slate-100 px-2 py-1 text-xs text-slate-500">1,248</span></h3><p class="mt-1 text-xs text-slate-400">Static sample records for layout preview</p></div><div class="flex flex-col gap-2 sm:flex-row"><input type="search" placeholder="Search by name or email..." class="rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-indigo-400"><select class="rounded-xl border border-slate-200 px-3 py-2.5 text-sm text-slate-600"><option>All departments</option><option>Computer Science</option><option>Business</option><option>English</option></select></div></div>
        <div class="overflow-x-auto"><table class="w-full min-w-[760px] text-left text-sm"><thead class="bg-slate-50 text-xs uppercase tracking-wider text-slate-400"><tr><th class="px-5 py-4">Student</th><th class="px-5 py-4">Student ID</th><th class="px-5 py-4">Department</th><th class="px-5 py-4">Email</th><th class="px-5 py-4">Status</th><th class="px-5 py-4 text-right">Actions</th></tr></thead><tbody class="divide-y divide-slate-100">
          <tr><td class="px-5 py-4"><div class="flex items-center gap-3"><span class="grid h-9 w-9 place-items-center rounded-full bg-violet-100 font-bold text-violet-700">AR</span><b>Ayesha Rahman</b></div></td><td class="px-5 py-4 text-slate-500">STU-1024</td><td class="px-5 py-4">Computer Science</td><td class="px-5 py-4 text-slate-500">ayesha@example.com</td><td class="px-5 py-4"><span class="rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-700">Active</span></td><td class="px-5 py-4 text-right"><a href="student-details.jsp" class="font-bold text-indigo-600">View</a><span class="px-2 text-slate-300">·</span><a href="edit-student.jsp" class="font-bold text-slate-600">Edit</a></td></tr>
          <tr><td class="px-5 py-4"><div class="flex items-center gap-3"><span class="grid h-9 w-9 place-items-center rounded-full bg-sky-100 font-bold text-sky-700">TH</span><b>Tanvir Hasan</b></div></td><td class="px-5 py-4 text-slate-500">STU-1023</td><td class="px-5 py-4">Business</td><td class="px-5 py-4 text-slate-500">tanvir@example.com</td><td class="px-5 py-4"><span class="rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-700">Active</span></td><td class="px-5 py-4 text-right"><a href="student-details.jsp" class="font-bold text-indigo-600">View</a><span class="px-2 text-slate-300">·</span><a href="edit-student.jsp" class="font-bold text-slate-600">Edit</a></td></tr>
          <tr><td class="px-5 py-4"><div class="flex items-center gap-3"><span class="grid h-9 w-9 place-items-center rounded-full bg-amber-100 font-bold text-amber-700">NJ</span><b>Nusrat Jahan</b></div></td><td class="px-5 py-4 text-slate-500">STU-1022</td><td class="px-5 py-4">English</td><td class="px-5 py-4 text-slate-500">nusrat@example.com</td><td class="px-5 py-4"><span class="rounded-full bg-amber-50 px-3 py-1 text-xs font-bold text-amber-700">Pending</span></td><td class="px-5 py-4 text-right"><a href="student-details.jsp" class="font-bold text-indigo-600">View</a><span class="px-2 text-slate-300">·</span><a href="edit-student.jsp" class="font-bold text-slate-600">Edit</a></td></tr>
        </tbody></table></div>
        <div class="flex items-center justify-between border-t border-slate-100 px-5 py-4 text-xs text-slate-500"><span>Showing 1–3 of 1,248 students</span><div class="flex gap-2"><button class="rounded-lg border border-slate-200 px-3 py-2" disabled>Previous</button><button class="rounded-lg border border-slate-200 px-3 py-2">Next</button></div></div>
      </div>

    </main>
    <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System</footer>
  </div>
</div>
</body></html>
