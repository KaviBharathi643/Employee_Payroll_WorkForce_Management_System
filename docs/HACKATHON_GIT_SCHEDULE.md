# Odoo NMIT Hackathon - Dayflow Team Git Schedule & Timeline

## 🏆 Project: Employee Payroll & Workforce Management System (Dayflow)
**Repository:** [kabilesh21/odoo-nmit-hackathon-Dayflow](https://github.com/kabilesh21/odoo-nmit-hackathon-Dayflow)  
**Date:** August 22, 2026  
**Team Name:** Dayflow Engineering Team  

---

## 👥 Team Members & Role Allocation

| Member | GitHub Handle / Email | Core Responsibilities |
|---|---|---|
| **Kabilesh M** | [@kabilesh21](https://github.com/kabilesh21) (`kabileshclg0678@gmail.com`) | Security Architecture, Authentication & Recovery, Dashboard Aggregators, Team Lead |
| **Kavi Bharathi P** | [@KaviBharathi643](https://github.com/KaviBharathi643) (`kavibarathi643@gmail.com`) | Portal Shell Layouts, Attendance Tracking & Leave Engines, Notifications Center, Data Seeding |
| **Keshav A T** | [@keshavthangaraj](https://github.com/keshavthangaraj) (`keshavthangaraj@gmail.com`) | Payroll Calculation Rules, PDF Generation, Docker/Nginx DevOps, Document Uploads & Storage |

---

## ⏱️ Hackathon Sprint Timeline & Git Commit Log

```
+--------------------------------------------------------------------------------------------------+
|                                    HACKATHON SPRINT PROGRESSION                                  |
|                                                                                                  |
| [09:30 - 10:30]  Phase 1: Project Setup, Boilerplate Audit & Repository Initialization           |
| [10:30 - 11:45]  Phase 2: Authentication, Security Layer, RBAC & Core Portal Shells              |
| [11:45 - 13:30]  Phase 3: Employee Profiles, Document Storage, Attendance & Leave Engines         |
| [13:30 - 15:00]  Phase 4: Payroll Computation Engine, PDF Generation, Docker & Nginx Packaging   |
| [15:00 - 16:30]  Phase 5: Notification Dispatcher, Metrics Aggregators, Polishing & Final Audit  |
+--------------------------------------------------------------------------------------------------+
```

---

### Detailed Sprint Milestones

#### 📌 Phase 1: Inception & Workspace Synchronization (09:30 - 10:30 AM)
- **Repository Setup**: Initialized remote repository structure and integrated frontend/backend modules.
- **Environment Stabilization**: Cleaned and validated `.gitignore`, environment variables, and Docker baseline.
- **Key Commits**:
  - `605b905` - `chore: audit and stabilize codebase for portable docker environment` *(Kavi Bharathi)*
  - `98abec6` - `Merge remote-tracking branch 'origin/main' with unrelated histories` *(Kavi Bharathi)*

#### 📌 Phase 2: Security, Authentication & Role Shells (10:30 - 11:45 AM)
- **Spring Security & JWT**: Implemented token generation, refresh mechanisms, and role-based route guards.
- **User Onboarding & Reset Passwords**: Implemented OTP verification and automated password generation.
- **Portals**: Created Admin, HR, and Employee portal shells.
- **Key Commits**:
  - `c081224` - `feat: design Admin Portal Shell, HR personnel CRUD APIs, and HrManagement user interface forms` *(Kavi Bharathi)*
  - `15e60aa` - `feat: setup security config, JWT handling, authentication endpoints, and React login UI with context` *(kabilesh21)*
  - `f47b9b8` - `feat: implement OTP verification, Forgot/Reset password APIs and corresponding views` *(kabilesh21)*
  - `d9e3156` - `feat: design HR Portal Shell, Employee creation endpoints, auto password generator, and Employee onboarding forms` *(Keshav Thangaraj)*
  - `7b7448f` - `feat: design Employee Portal layout, profiles controller, bank detail models, and MyProfile settings form` *(Kavi Bharathi)*

#### 📌 Phase 3: Profile Media, Attendance & Leaves (11:45 AM - 01:30 PM)
- **File Storage**: Multi-part file upload support for employee avatars and documents with size formatting.
- **Attendance & Leaves**: Check-in/check-out workflow, status calculators (Late, Half-Day, Present), and approval hierarchy.
- **Key Commits**:
  - `e4cd628` - `feat: integrate backend multipart file uploads helper, profile avatar cropped view, and drag-and-drop file sections` *(Keshav Thangaraj)*
  - `222ba18` - `feat(utils): add formatFileSize helper utility for document and photo uploads` *(Keshav Thangaraj)*
  - `3a8d273` - `feat: implement backend attendance tracking and leave request service layers` *(Kavi Bharathi P)*
  - `16840f8` - `feat: build HR & Management overall attendance summaries, custom date range filtering, and audit log tables` *(Keshav Thangaraj)*
  - `b545740` - `feat: build automated status recalculations engine (Late, Half-Day, Present) and scheduled reminders config` *(Kavi Bharathi)*

#### 📌 Phase 4: Payroll Engine, Reports & DevOps (01:30 - 03:00 PM)
- **Payroll Rule Engine**: Gross/net salary calculations, deductions, allowances, tax slabs, and iText PDF generator.
- **DevOps**: Multi-stage Dockerfiles for backend (Temurin 21) and frontend (Node 22 + Nginx reverse proxy).
- **Key Commits**:
  - `dc345d9` - `feat: implement payroll calculation rules, PDF generator, and management reporting APIs` *(Keshav Thangaraj)*
  - `2836687` - `feat: integrate employee dashboard widgets, leave manager panels, and payroll summary tables` *(kabilesh21)*
  - `7566c65` - `build: compile Docker containers environments, Nginx server proxies, and packaging configs` *(Keshav Thangaraj)*
  - `8edd718` - `feat: implement real-time notifications dispatch systems, event logs cleanup, and frontend notification center` *(Kavi Bharathi)*

#### 📌 Phase 5: Metrics Aggregator, Policy API & Final Audit (03:00 - 04:30 PM)
- **Executive Analytics**: Overall metrics aggregator, policy configs, and visual chart panel components.
- **Branch Convergence**: Synchronized all feature branches into `main` and verified end-to-end integration.
- **Key Commits**:
  - `4612150` - `feat: implement overall metrics aggregator, policy configs API, and admin chart panel components` *(kabilesh21)*
  - `b3cbec4` - `merge: integrate Dayflow hackathon branch with main` *(Kavi Bharathi)*
  - `6e29b3d` - `feat: implement overall metrics aggregator, policy configs API, and admin chart panel components` *(kabilesh21)*

---

## 🛠️ Branching Strategy & Collaboration Protocol

1. **Main Branch Protection**: Only production-ready, verified code is merged into `main`.
2. **Atomic Commits**: Standard Conventional Commits specification adhered to (`feat:`, `fix:`, `docs:`, `chore:`, `build:`).
3. **Continuous Integration**: Each pull request validated with automated unit tests (`mvn test`) and frontend production build (`npm run build`).

---

*Authored by the Dayflow Hackathon Development Team for the Odoo NMIT Hackathon.*
