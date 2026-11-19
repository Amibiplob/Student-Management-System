Student Management System - Static JSP UI starter
==================================================

This is a static UI starter for your existing NetBeans Maven Web Application.
It uses:
- JSP for page files and shared includes
- Tailwind CSS Play CDN for quick local prototyping
- Flowbite CDN for ready-made Tailwind components (dropdowns, sidebar patterns, etc.)
- No database, JDBC, Servlet, or dynamic form processing is connected yet.

HOW TO USE
1. In NetBeans, open your StudentManagementSystem project.
2. Copy the contents of this package's `webapp` folder into your project's Web Pages
   folder (usually src/main/webapp).
3. Keep the `WEB-INF/views` folder as shown.
4. Run the project with Tomcat.
5. Open http://localhost:8080/StudentManagementSystem/ (home page)\n   Admin sign-in: http://localhost:8080/StudentManagementSystem/login.jsp

PAGES
- login.jsp
- dashboard.jsp
- students.jsp
- add-student.jsp
- edit-student.jsp
- student-details.jsp
- departments.jsp

IMPORTANT
- These are static mockup pages. Form submissions and buttons are placeholders.
- Later, we will connect forms to Servlets and JDBC.
- Tailwind Play CDN is convenient for learning/prototyping, not recommended for
  production. For a production release, compile Tailwind locally with the CLI.
- Flowbite is used as the component library. Its CDN script is included in the
  shared topbar include.
