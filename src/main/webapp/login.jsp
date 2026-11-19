<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sign in | CampusDesk</title><script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
  <style>body{font-family:'DM Sans',sans-serif}.display{font-family:'Manrope',sans-serif}</style>
</head>
<body class="min-h-screen bg-slate-950 text-slate-100">
  <div class="grid min-h-screen lg:grid-cols-2">
    <section class="relative hidden overflow-hidden bg-indigo-600 p-12 lg:flex lg:flex-col lg:justify-between">
      <div class="absolute -right-28 -top-28 h-96 w-96 rounded-full bg-violet-400/30 blur-3xl"></div><div class="absolute -bottom-32 -left-24 h-96 w-96 rounded-full bg-cyan-300/20 blur-3xl"></div>
      <a href="${pageContext.request.contextPath}/login" class="relative flex items-center gap-3 text-xl font-bold"><span class="grid h-11 w-11 place-items-center rounded-2xl bg-white text-indigo-700">C</span>CampusDesk</a>
      <div class="relative max-w-xl"><p class="mb-5 inline-flex rounded-full border border-white/30 bg-white/10 px-4 py-2 text-sm text-indigo-50">Secure workspace</p><h1 class="display text-5xl font-extrabold leading-tight">A clearer view of your campus.</h1><p class="mt-6 max-w-md text-lg leading-8 text-indigo-100">Sign in to review student records and department information for your role.</p><div class="mt-12 grid grid-cols-2 gap-3"><div class="rounded-2xl border border-white/20 bg-white/10 p-4"><p class="text-2xl font-bold">01</p><p class="mt-1 text-xs text-indigo-100">Student records</p></div><div class="rounded-2xl border border-white/20 bg-white/10 p-4"><p class="text-2xl font-bold">02</p><p class="mt-1 text-xs text-indigo-100">Departments</p></div></div></div>
      <p class="relative text-sm text-indigo-100">© 2026 CampusDesk · Student Management System</p>
    </section>
    <main class="flex items-center justify-center px-6 py-12"><div class="w-full max-w-md">
      <div class="mb-10 flex items-center gap-3 lg:hidden"><span class="grid h-11 w-11 place-items-center rounded-2xl bg-indigo-500 font-bold">C</span><span class="text-xl font-bold">CampusDesk</span></div>
      <p class="text-sm font-semibold uppercase tracking-[.2em] text-indigo-400">Account sign in</p><h2 class="display mt-3 text-3xl font-extrabold">Welcome back</h2><p class="mt-2 text-sm text-slate-400">Use your CampusDesk account to continue.</p>
      <c:if test="${not empty errorMessage}"><div role="alert" class="mt-6 rounded-xl border border-rose-800 bg-rose-950/70 px-4 py-3 text-sm text-rose-100"><c:out value="${errorMessage}" /></div></c:if>
      <c:if test="${param.loggedOut == '1'}"><div role="status" class="mt-6 rounded-xl border border-emerald-800 bg-emerald-950/70 px-4 py-3 text-sm text-emerald-100">You have been signed out.</div></c:if>
      <form action="${pageContext.request.contextPath}/login" method="post" class="mt-7 space-y-5">
        <input type="hidden" name="_csrf" value="<c:out value='${csrfToken}' />">
        <div><label for="email" class="mb-2 block text-sm font-semibold text-slate-200">Email address</label><input id="email" name="email" type="email" autocomplete="username" maxlength="150" required value="<c:out value='${email}' />" class="w-full rounded-xl border border-slate-700 bg-slate-900 px-4 py-3.5 text-white outline-none focus:border-indigo-400 focus:ring-4 focus:ring-indigo-950"></div>
        <div><label for="password" class="mb-2 block text-sm font-semibold text-slate-200">Password</label><input id="password" name="password" type="password" autocomplete="current-password" required class="w-full rounded-xl border border-slate-700 bg-slate-900 px-4 py-3.5 text-white outline-none focus:border-indigo-400 focus:ring-4 focus:ring-indigo-950"></div>
        <button type="submit" class="w-full rounded-xl bg-indigo-500 px-5 py-3.5 text-center font-bold text-white transition hover:bg-indigo-400">Sign in <span aria-hidden="true">→</span></button>
      </form>
      <p class="mt-7 text-center text-xs leading-5 text-slate-500">If you cannot sign in to an initial account, ask the system administrator to check the protected Tomcat environment and account status.</p>
    </div></main>
  </div>
</body></html>
