<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Student details | CampusDesk</title>
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

      <div class="mb-7"><a href="students.jsp" class="text-sm font-bold text-indigo-600">← Back to students</a></div>
      <section class="overflow-hidden rounded-2xl border border-slate-200 bg-white">
        <div class="bg-gradient-to-r from-indigo-600 to-violet-600 px-7 py-9 text-white"><div class="flex flex-col gap-5 sm:flex-row sm:items-center"><div class="grid h-20 w-20 place-items-center rounded-2xl bg-white/20 text-2xl font-extrabold">AR</div><div class="flex-1"><p class="text-sm font-semibold text-indigo-100">STUDENT PROFILE · STU-1024</p><h2 class="mt-1 text-3xl font-extrabold">Ayesha Rahman</h2><p class="mt-2 text-sm text-indigo-100">Computer Science · Active student</p></div><a href="edit-student.jsp" class="rounded-xl bg-white px-4 py-2.5 text-center text-sm font-bold text-indigo-700">Edit profile</a></div></div>
        <div class="grid gap-8 p-7 md:grid-cols-2"><div><h3 class="font-extrabold">Contact information</h3><dl class="mt-5 space-y-4 text-sm"><div><dt class="text-slate-400">Email address</dt><dd class="mt-1 font-semibold">ayesha@example.com</dd></div><div><dt class="text-slate-400">Phone number</dt><dd class="mt-1 font-semibold">+880 1712-345678</dd></div><div><dt class="text-slate-400">Address</dt><dd class="mt-1 font-semibold">Tangail, Bangladesh</dd></div></dl></div><div><h3 class="font-extrabold">Academic information</h3><dl class="mt-5 space-y-4 text-sm"><div><dt class="text-slate-400">Department</dt><dd class="mt-1 font-semibold">Computer Science</dd></div><div><dt class="text-slate-400">Student ID</dt><dd class="mt-1 font-semibold">STU-1024</dd></div><div><dt class="text-slate-400">Registration date</dt><dd class="mt-1 font-semibold">12 November 2026</dd></div></dl></div></div>
      </section>

    </main>
    <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System</footer>
  </div>
</div>
</body></html>
