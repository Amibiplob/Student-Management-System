<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Access denied | CampusDesk</title><script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
  <style>body{font-family:'DM Sans',sans-serif}h1{font-family:'Manrope',sans-serif}</style>
</head><body class="grid min-h-screen place-items-center bg-slate-50 p-5 text-slate-800">
  <main class="w-full max-w-lg rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
    <div class="mx-auto grid h-14 w-14 place-items-center rounded-2xl bg-rose-100 text-2xl font-extrabold text-rose-700" aria-hidden="true">!</div>
    <p class="mt-6 text-sm font-bold uppercase tracking-widest text-rose-600">Access denied</p>
    <h1 class="mt-2 text-3xl font-extrabold">You cannot open this page</h1>
    <p class="mt-3 text-sm leading-6 text-slate-600"><c:choose><c:when test="${not empty errorMessage}"><c:out value="${errorMessage}" /></c:when><c:otherwise>Your account does not have permission to view this page.</c:otherwise></c:choose></p>
    <a href="${pageContext.request.contextPath}/dashboard" class="mt-7 inline-flex rounded-xl bg-indigo-600 px-5 py-3 text-sm font-bold text-white hover:bg-indigo-700">Return to your dashboard</a>
  </main>
</body></html>
