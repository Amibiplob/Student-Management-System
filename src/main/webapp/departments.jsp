<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Departments | CampusDesk</title>
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

      <div class="mb-7"><p class="text-sm font-semibold text-indigo-600">ACADEMIC STRUCTURE</p><h2 class="mt-2 text-3xl font-extrabold text-slate-900">Departments</h2><p class="mt-2 text-slate-500">A simple overview of academic departments.</p></div>
      <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
        <div class="rounded-2xl border border-slate-200 bg-white p-6"><div class="grid h-12 w-12 place-items-center rounded-xl bg-indigo-100 text-xl text-indigo-700">⌘</div><h3 class="mt-5 font-extrabold">Computer Science</h3><p class="mt-1 text-sm text-slate-500">Department of Computer Science & Engineering</p><div class="mt-5 border-t border-slate-100 pt-4 text-sm"><b>324</b><span class="ml-2 text-slate-400">Students</span></div></div>
        <div class="rounded-2xl border border-slate-200 bg-white p-6"><div class="grid h-12 w-12 place-items-center rounded-xl bg-emerald-100 text-xl text-emerald-700">▥</div><h3 class="mt-5 font-extrabold">Business Administration</h3><p class="mt-1 text-sm text-slate-500">Department of Business Studies</p><div class="mt-5 border-t border-slate-100 pt-4 text-sm"><b>286</b><span class="ml-2 text-slate-400">Students</span></div></div>
        <div class="rounded-2xl border border-slate-200 bg-white p-6"><div class="grid h-12 w-12 place-items-center rounded-xl bg-amber-100 text-xl text-amber-700">文</div><h3 class="mt-5 font-extrabold">English</h3><p class="mt-1 text-sm text-slate-500">Department of English</p><div class="mt-5 border-t border-slate-100 pt-4 text-sm"><b>198</b><span class="ml-2 text-slate-400">Students</span></div></div>
      </div>

    </main>
    <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System</footer>
  </div>
</div>
</body></html>
