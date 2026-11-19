<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Home | CampusDesk</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
  <style>body{font-family:'DM Sans',sans-serif}h1,h2,h3{font-family:'Manrope',sans-serif}</style>
</head>
<body class="bg-slate-50 text-slate-800">
  <header class="border-b border-slate-200 bg-white">
    <nav class="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
      <a href="${pageContext.request.contextPath}/" class="flex items-center gap-3">
        <span class="grid h-10 w-10 place-items-center rounded-xl bg-indigo-600 font-extrabold text-white">C</span>
        <span class="text-lg font-extrabold tracking-tight text-slate-900">CampusDesk</span>
      </a>
      <div class="hidden items-center gap-8 text-sm font-semibold text-slate-600 md:flex">
        <a href="#features" class="hover:text-indigo-600">Features</a>
        <a href="#about" class="hover:text-indigo-600">About</a>
      </div>
      <a href="${pageContext.request.contextPath}/login.jsp" class="rounded-xl bg-indigo-600 px-5 py-2.5 text-sm font-bold text-white shadow-lg shadow-indigo-100 transition hover:bg-indigo-700">Admin sign in →</a>
    </nav>
  </header>

  <main>
    <section class="relative overflow-hidden bg-slate-950">
      <div class="absolute -right-24 -top-28 h-96 w-96 rounded-full bg-indigo-600/30 blur-3xl"></div>
      <div class="absolute -bottom-40 left-1/4 h-96 w-96 rounded-full bg-violet-500/20 blur-3xl"></div>
      <div class="relative mx-auto grid max-w-7xl items-center gap-12 px-6 py-20 lg:grid-cols-2 lg:py-28">
        <div>
          <span class="inline-flex items-center gap-2 rounded-full border border-indigo-400/30 bg-indigo-400/10 px-4 py-2 text-xs font-bold uppercase tracking-wider text-indigo-200"><span class="h-2 w-2 rounded-full bg-emerald-400"></span> Student information portal</span>
          <h1 class="mt-7 max-w-2xl text-4xl font-extrabold leading-tight tracking-tight text-white sm:text-5xl lg:text-6xl">Every student record, <span class="text-indigo-300">in one place.</span></h1>
          <p class="mt-6 max-w-xl text-lg leading-8 text-slate-300">CampusDesk helps administrators organize student information, manage academic records, and find the details they need without the paperwork.</p>
          <div class="mt-9 flex flex-col gap-3 sm:flex-row">
            <a href="${pageContext.request.contextPath}/login.jsp" class="rounded-xl bg-indigo-500 px-6 py-3.5 text-center text-sm font-bold text-white shadow-lg shadow-indigo-950/40 transition hover:bg-indigo-400">Access admin portal <span aria-hidden="true">→</span></a>
            <a href="#features" class="rounded-xl border border-slate-700 px-6 py-3.5 text-center text-sm font-bold text-slate-200 transition hover:bg-white/10">Explore features</a>
          </div>
          <div class="mt-12 flex flex-wrap gap-x-8 gap-y-3 text-sm text-slate-400"><span>✓ Organized student records</span><span>✓ Simple administration</span><span>✓ Quick student search</span></div>
        </div>
        <div class="rounded-3xl border border-white/10 bg-white/5 p-3 shadow-2xl shadow-black/30 backdrop-blur">
          <div class="rounded-2xl bg-white p-5 sm:p-7">
            <div class="flex items-center justify-between border-b border-slate-100 pb-5"><div><p class="text-xs font-bold uppercase tracking-wider text-indigo-600">Overview preview</p><h2 class="mt-1 text-xl font-extrabold text-slate-900">Campus at a glance</h2></div><span class="rounded-lg bg-emerald-50 px-3 py-1.5 text-xs font-bold text-emerald-700">Dashboard</span></div>
            <div class="mt-5 grid grid-cols-2 gap-3">
              <div class="rounded-2xl bg-indigo-50 p-4"><p class="text-xs font-semibold text-indigo-600">Total students</p><p class="mt-2 text-3xl font-extrabold text-slate-900">1,248</p><p class="mt-1 text-xs text-slate-500">Registered records</p></div>
              <div class="rounded-2xl bg-emerald-50 p-4"><p class="text-xs font-semibold text-emerald-700">Departments</p><p class="mt-2 text-3xl font-extrabold text-slate-900">08</p><p class="mt-1 text-xs text-slate-500">Academic groups</p></div>
            </div>
            <div class="mt-5"><div class="mb-3 flex items-center justify-between"><h3 class="text-sm font-extrabold text-slate-800">Recently added</h3><span class="text-xs text-slate-400">Sample data</span></div>
              <div class="space-y-3">
                <div class="flex items-center gap-3 rounded-xl border border-slate-100 p-3"><span class="grid h-9 w-9 place-items-center rounded-full bg-violet-100 text-xs font-bold text-violet-700">AR</span><div class="min-w-0 flex-1"><p class="truncate text-sm font-bold text-slate-800">Ayesha Rahman</p><p class="text-xs text-slate-400">Computer Science</p></div><span class="rounded-full bg-emerald-50 px-2.5 py-1 text-[10px] font-bold text-emerald-700">Active</span></div>
                <div class="flex items-center gap-3 rounded-xl border border-slate-100 p-3"><span class="grid h-9 w-9 place-items-center rounded-full bg-sky-100 text-xs font-bold text-sky-700">TH</span><div class="min-w-0 flex-1"><p class="truncate text-sm font-bold text-slate-800">Tanvir Hasan</p><p class="text-xs text-slate-400">Business</p></div><span class="rounded-full bg-emerald-50 px-2.5 py-1 text-[10px] font-bold text-emerald-700">Active</span></div>
                <div class="flex items-center gap-3 rounded-xl border border-slate-100 p-3"><span class="grid h-9 w-9 place-items-center rounded-full bg-amber-100 text-xs font-bold text-amber-700">NJ</span><div class="min-w-0 flex-1"><p class="truncate text-sm font-bold text-slate-800">Nusrat Jahan</p><p class="text-xs text-slate-400">English</p></div><span class="rounded-full bg-amber-50 px-2.5 py-1 text-[10px] font-bold text-amber-700">Pending</span></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section id="features" class="mx-auto max-w-7xl px-6 py-20">
      <div class="mx-auto max-w-2xl text-center"><p class="text-sm font-bold uppercase tracking-[.18em] text-indigo-600">What you can do</p><h2 class="mt-3 text-3xl font-extrabold tracking-tight text-slate-900 sm:text-4xl">Simple tools for everyday student administration</h2><p class="mt-4 leading-7 text-slate-500">A focused set of features to keep student information organized and easy to manage.</p></div>
      <div class="mt-12 grid gap-5 md:grid-cols-2 lg:grid-cols-3">
        <article class="rounded-2xl border border-slate-200 bg-white p-6"><span class="grid h-12 w-12 place-items-center rounded-xl bg-indigo-50 text-xl text-indigo-700">♙</span><h3 class="mt-5 font-extrabold text-slate-900">Student records</h3><p class="mt-2 text-sm leading-6 text-slate-500">Create, view, update, and maintain student profiles from one place.</p></article>
        <article class="rounded-2xl border border-slate-200 bg-white p-6"><span class="grid h-12 w-12 place-items-center rounded-xl bg-emerald-50 text-xl text-emerald-700">⌕</span><h3 class="mt-5 font-extrabold text-slate-900">Quick search</h3><p class="mt-2 text-sm leading-6 text-slate-500">Search for student information by name, email address, or phone number.</p></article>
        <article class="rounded-2xl border border-slate-200 bg-white p-6"><span class="grid h-12 w-12 place-items-center rounded-xl bg-violet-50 text-xl text-violet-700">▤</span><h3 class="mt-5 font-extrabold text-slate-900">Department overview</h3><p class="mt-2 text-sm leading-6 text-slate-500">Keep department information and student organization easy to review.</p></article>
      </div>
    </section>

    <section id="about" class="border-y border-slate-200 bg-white">
      <div class="mx-auto flex max-w-7xl flex-col items-start justify-between gap-6 px-6 py-14 md:flex-row md:items-center">
        <div><p class="text-sm font-bold uppercase tracking-[.18em] text-indigo-600">About CampusDesk</p><h2 class="mt-2 text-2xl font-extrabold text-slate-900">Designed to make student administration easier.</h2><p class="mt-2 max-w-2xl text-sm leading-6 text-slate-500">A student management system project built with Java, JSP, JDBC, and PostgreSQL.</p></div>
        <a href="${pageContext.request.contextPath}/login.jsp" class="shrink-0 rounded-xl bg-slate-900 px-6 py-3 text-sm font-bold text-white hover:bg-slate-700">Go to admin sign in →</a>
      </div>
    </section>
  </main>
  <footer class="bg-slate-950 px-6 py-7 text-center text-xs text-slate-400">© 2026 CampusDesk · Student Management System · Academic project</footer>
</body>
</html>
