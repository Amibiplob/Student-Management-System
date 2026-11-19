
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add student | CampusDesk</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'DM Sans', sans-serif; }
        h1, h2, h3 { font-family: 'Manrope', sans-serif; }
    </style>
</head>

<body class="bg-slate-50 text-slate-800">
<div class="flex min-h-screen">

    <%@ include file="WEB-INF/views/includes/sidebar.jsp" %>

    <div class="min-w-0 flex-1">
        <%@ include file="WEB-INF/views/includes/topbar.jsp" %>

        <main class="mx-auto max-w-7xl p-5 sm:p-8">

            <div class="mb-7">
                <a href="${pageContext.request.contextPath}/students"
                   class="text-sm font-bold text-indigo-600 hover:text-indigo-800">
                    ← Back to students
                </a>

                <p class="mt-6 text-sm font-semibold text-indigo-600">
                    STUDENT RECORD
                </p>

                <h2 class="mt-2 text-3xl font-extrabold text-slate-900">
                    Add a new student
                </h2>

                <p class="mt-2 text-slate-500">
                    Fill in the student information below.
                </p>
            </div>

            <form action="${pageContext.request.contextPath}/students"
                  method="POST"
                  class="rounded-2xl border border-slate-200 bg-white p-6 sm:p-8">

                <input type="hidden" name="action" value="add">

                <div class="mb-6">
                    <h3 class="font-extrabold text-slate-900">
                        Personal information
                    </h3>
                    <p class="mt-1 text-xs text-slate-400">
                        Basic details about the student
                    </p>
                </div>

                <div class="grid gap-5 md:grid-cols-2">

                    <div>
                        <label class="mb-2 block text-sm font-semibold">
                            Full name *
                        </label>
                        <input type="text"
                               name="name"
                               placeholder="Enter full name"
                               required
                               class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400 focus:ring-4 focus:ring-indigo-50">
                    </div>

                    <div>
                        <label class="mb-2 block text-sm font-semibold">
                            Email address *
                        </label>
                        <input type="email"
                               name="email"
                               placeholder="student@example.com"
                               required
                               class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400 focus:ring-4 focus:ring-indigo-50">
                    </div>

                    <div>
                        <label class="mb-2 block text-sm font-semibold">
                            Phone number
                        </label>
                        <input type="tel"
                               name="phone"
                               placeholder="+880 1XXXXXXXXX"
                               class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400">
                    </div>

                    <div>
                        <label class="mb-2 block text-sm font-semibold">
                            Gender
                        </label>
                        <select name="gender"
                                class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3">
                            <option value="">Select gender</option>
                            <option value="Male">Male</option>
                            <option value="Female">Female</option>
                            <option value="Other">Other</option>
                        </select>
                    </div>

                    <div>
                        <label class="mb-2 block text-sm font-semibold">
                            Date of birth
                        </label>
                        <input type="date"
                               name="dateOfBirth"
                               class="w-full rounded-xl border border-slate-200 px-4 py-3">
                    </div>

                    <div>
                        <label class="mb-2 block text-sm font-semibold">
                            Department *
                        </label>
                        <select name="departmentId"
                                required
                                class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3">
                            <option value="">Select department</option>
                            <option value="1">Computer Science</option>
                            <option value="2">Electrical Engineering</option>
                            <option value="3">Business Administration</option>
                            <option value="4">English</option>
                        </select>
                    </div>

                    <div class="md:col-span-2">
                        <label class="mb-2 block text-sm font-semibold">
                            Address
                        </label>
                        <textarea name="address"
                                  rows="3"
                                  placeholder="Enter current address"
                                  class="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-400"></textarea>
                    </div>

                </div>

                <div class="mt-8 flex flex-col-reverse justify-end gap-3 border-t border-slate-100 pt-6 sm:flex-row">

                    <a href="${pageContext.request.contextPath}/students"
                       class="rounded-xl border border-slate-200 px-5 py-3 text-center text-sm font-bold text-slate-600 hover:bg-slate-50">
                        Cancel
                    </a>

                    <button type="submit"
                            class="rounded-xl bg-indigo-600 px-6 py-3 text-sm font-bold text-white hover:bg-indigo-700">
                        Save student
                    </button>

                </div>

            </form>
        </main>

        <footer class="border-t border-slate-200 px-8 py-5 text-center text-xs text-slate-400">
            © 2026 CampusDesk · Student Management System
        </footer>
    </div>
</div>
</body>
</html>
